package com.kssidll.arru.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.domain.utils.*
import com.kssidll.arru.helper.*
import com.kssidll.arru.ui.component.chart.*
import com.kssidll.arru.ui.theme.*
import com.patrykandpatrick.vico.compose.chart.*
import com.patrykandpatrick.vico.compose.chart.line.*
import com.patrykandpatrick.vico.compose.chart.scroll.*
import com.patrykandpatrick.vico.core.entry.*
import com.patrykandpatrick.vico.core.scroll.*
import kotlinx.coroutines.*

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
            modifier = Modifier.height(140.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
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

                Text(
                    text = animatedValue.value.formatToCurrency(),
                    style = Typography.headlineLarge,
                )

                Chart(
                    chart = lineChart(),
                    chartModelProducer = chartProducer,
                    chartScrollSpec = rememberChartScrollSpec(
                        isScrollEnabled = false,
                        initialScroll = InitialScroll.End,
                    ),
                    marker = rememberMarker(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
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

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.average),
                        style = Typography.headlineSmall,
                    )

                    Text(
                        text = animatedAverageValue.value.formatToCurrency(),
                        style = Typography.headlineSmall,
                    )

                    Box(modifier = Modifier.height(88.dp)) {
                        Chart(
                            chart = lineChart(),
                            chartModelProducer = chartProducer,
                            chartScrollSpec = rememberChartScrollSpec(
                                isScrollEnabled = false,
                                initialScroll = InitialScroll.End,
                            ),
                            marker = rememberMarker(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(140.dp)
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

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.median),
                        style = Typography.headlineSmall,
                    )

                    Text(
                        text = animatedMedianValue.value.formatToCurrency(),
                        style = Typography.headlineSmall,
                    )

                    Box(modifier = Modifier.height(88.dp)) {
                        Chart(
                            chart = lineChart(),
                            chartModelProducer = chartProducer,
                            chartScrollSpec = rememberChartScrollSpec(
                                isScrollEnabled = false,
                                initialScroll = InitialScroll.End,
                            ),
                            marker = rememberMarker(),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
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
