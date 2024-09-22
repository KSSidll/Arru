package com.kssidll.arru.ui.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.domain.data.ChartSource
import com.kssidll.arru.domain.data.avg
import com.kssidll.arru.domain.data.median
import com.kssidll.arru.domain.data.movingAverageChartData
import com.kssidll.arru.domain.data.movingMedianChartData
import com.kssidll.arru.domain.data.movingTotalChartData
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.helper.generateRandomFloatValue
import com.kssidll.arru.ui.component.chart.rememberMarker
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.scroll.InitialScroll
import kotlinx.coroutines.delay

private val HALF_CHART_TOP_PADDING: Dp = 36.dp
private val FULL_CHART_TOP_PADDING: Dp = 18.dp
private val CARD_SPACING: Dp = 8.dp
private val CARD_HEIGHT: Dp = 140.dp
private val CARD_TEXT_TOP_PADDING: Dp = 10.dp

@Composable
fun TotalAverageAndMedianSpendingComponent(
    modifier: Modifier = Modifier,
    spentByTimeData: List<ChartSource>,
    totalSpentData: Float,
    animationSpec: AnimationSpec<Float> = tween(
        800,
        easing = EaseIn
    ),
    skipAnimation: Boolean = false,
    onAnimationEnd: () -> Unit = {},
) {
    val totalStartValue = if (skipAnimation) totalSpentData else 0f
    val averageStartValue = if (skipAnimation) spentByTimeData.avg() else 0f
    val medianStartValue = if (skipAnimation) spentByTimeData.median() else 0f

    Column(modifier) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            modifier = Modifier.height(CARD_HEIGHT)
        ) {
            var targetValue by remember { mutableFloatStateOf(totalStartValue) }

            val chartProducer = remember {
                ChartEntryModelProducer()
            }

            LaunchedEffect(totalSpentData) {
                targetValue = totalSpentData

                val oldDataAdjusted = chartProducer.getModel()?.entries?.map { chart ->
                    chart.map { it.withY(0f) }
                }
                    .orEmpty()

                if (oldDataAdjusted.isNotEmpty()) {
                    chartProducer.setEntries(oldDataAdjusted)
                }

                val newData = spentByTimeData.movingTotalChartData()

                if (oldDataAdjusted.isNotEmpty()) {
                    delay(com.patrykandpatrick.vico.core.Animation.DIFF_DURATION.toLong())
                }

                chartProducer.setEntries(newData)
            }

            val animatedValue = animateFloatAsState(
                targetValue = targetValue,
                animationSpec = animationSpec,
                label = "total spent value animation",
                finishedListener = {
                    // only called here since all animations use same spec and the total is the highest value
                    // so will take the longest no matter the spec
                    onAnimationEnd()
                }
            )

            Box(modifier = Modifier.padding(top = CARD_TEXT_TOP_PADDING)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = animatedValue.value.formatToCurrency(),
                        style = Typography.headlineLarge,
                    )
                }

                Chart(
                    chart = lineChart(),
                    chartModelProducer = chartProducer,
                    chartScrollSpec = rememberChartScrollSpec(
                        isScrollEnabled = false,
                        initialScroll = InitialScroll.End,
                    ),
                    marker = rememberMarker(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = FULL_CHART_TOP_PADDING)
                        .align(Alignment.BottomCenter)
                )
            }
        }

        Spacer(Modifier.height(CARD_SPACING))

        Row {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(CARD_HEIGHT)
            ) {
                var averageTargetValue by remember { mutableFloatStateOf(averageStartValue) }
                val chartProducer = remember {
                    ChartEntryModelProducer()
                }

                LaunchedEffect(spentByTimeData) {
                    averageTargetValue = spentByTimeData.avg()

                    val oldDataAdjusted = chartProducer.getModel()?.entries?.map { chart ->
                        chart.map { it.withY(0f) }
                    }
                        .orEmpty()

                    if (oldDataAdjusted.isNotEmpty()) {
                        chartProducer.setEntries(oldDataAdjusted)
                    }

                    val newData = spentByTimeData.movingAverageChartData()

                    if (oldDataAdjusted.isNotEmpty()) {
                        delay(com.patrykandpatrick.vico.core.Animation.DIFF_DURATION.toLong())
                    }

                    chartProducer.setEntries(newData)
                }

                val animatedAverageValue = animateFloatAsState(
                    targetValue = averageTargetValue,
                    animationSpec = animationSpec,
                    label = "average spent value animation"
                )

                Box(modifier = Modifier.padding(top = CARD_TEXT_TOP_PADDING)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.average),
                            style = Typography.headlineSmall,
                        )

                        Text(
                            text = animatedAverageValue.value.formatToCurrency(),
                            style = Typography.headlineSmall,
                        )
                    }

                    Chart(
                        chart = lineChart(),
                        chartModelProducer = chartProducer,
                        chartScrollSpec = rememberChartScrollSpec(
                            isScrollEnabled = false,
                            initialScroll = InitialScroll.End,
                        ),
                        marker = rememberMarker(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = HALF_CHART_TOP_PADDING)
                            .align(Alignment.BottomCenter)
                    )
                }
            }

            Spacer(Modifier.width(CARD_SPACING))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(CARD_HEIGHT)
            ) {
                var medianTargetValue by remember { mutableFloatStateOf(medianStartValue) }
                val chartProducer = remember {
                    ChartEntryModelProducer()
                }

                LaunchedEffect(spentByTimeData) {
                    medianTargetValue = spentByTimeData.median()
                    val oldDataAdjusted = chartProducer.getModel()?.entries?.map { chart ->
                        chart.map { it.withY(0f) }
                    }
                        .orEmpty()

                    if (oldDataAdjusted.isNotEmpty()) {
                        chartProducer.setEntries(oldDataAdjusted)
                    }

                    val newData = spentByTimeData.movingMedianChartData()

                    if (oldDataAdjusted.isNotEmpty()) {
                        delay(com.patrykandpatrick.vico.core.Animation.DIFF_DURATION.toLong())
                    }

                    chartProducer.setEntries(newData)
                }

                val animatedMedianValue = animateFloatAsState(
                    targetValue = medianTargetValue,
                    animationSpec = animationSpec,
                    label = "median spent value animation"
                )

                Box(modifier = Modifier.padding(top = CARD_TEXT_TOP_PADDING)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.median),
                            style = Typography.headlineSmall,
                        )

                        Text(
                            text = animatedMedianValue.value.formatToCurrency(),
                            style = Typography.headlineSmall,
                        )
                    }

                    Chart(
                        chart = lineChart(),
                        chartModelProducer = chartProducer,
                        chartScrollSpec = rememberChartScrollSpec(
                            isScrollEnabled = false,
                            initialScroll = InitialScroll.End,
                        ),
                        marker = rememberMarker(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = HALF_CHART_TOP_PADDING)
                            .align(Alignment.BottomCenter)
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TotalAverageAndMedianSpendingComponentPreview() {
    ArrugarqTheme {
        Surface {
            TotalAverageAndMedianSpendingComponent(
                spentByTimeData = ItemSpentByTime.generateList(),
                totalSpentData = generateRandomFloatValue(),
            )
        }
    }
}
