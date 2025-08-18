package com.kssidll.arru.ui.component

import androidx.collection.FloatFloatPair
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.LocalCurrencyFormatLocale
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.data.interfaces.avg
import com.kssidll.arru.domain.data.interfaces.median
import com.kssidll.arru.domain.data.interfaces.movingAverageChartData
import com.kssidll.arru.domain.data.interfaces.movingMedianChartData
import com.kssidll.arru.domain.data.interfaces.movingTotalChartData
import com.kssidll.arru.domain.utils.formatToCurrency
import com.kssidll.arru.helper.generateRandomFloatValue
import com.kssidll.arru.ui.component.chart.rememberMarker
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import kotlinx.collections.immutable.ImmutableList

private val HALF_CHART_TOP_PADDING: Dp = 36.dp
private val FULL_CHART_TOP_PADDING: Dp = 18.dp
private val CARD_SPACING: Dp = 8.dp
private val CARD_HEIGHT: Dp = 140.dp
private val CARD_TEXT_TOP_PADDING: Dp = 10.dp

@Composable
fun TotalAverageAndMedianSpendingComponent(
    spentByTimeData: ImmutableList<ChartSource>,
    totalSpentData: Float,
    modifier: Modifier = Modifier,
    totalChartEntryModelProducer: CartesianChartModelProducer = remember {
        CartesianChartModelProducer()
    },
    averageChartEntryModelProducer: CartesianChartModelProducer = remember {
        CartesianChartModelProducer()
    },
    medianChartEntryModelProducer: CartesianChartModelProducer = remember {
        CartesianChartModelProducer()
    },
    animationSpec: AnimationSpec<Float> = tween(800, easing = EaseIn),
    skipAnimation: Boolean = false,
    onAnimationEnd: () -> Unit = {},
) {
    val totalStartValue = if (skipAnimation) totalSpentData else 0f
    val averageStartValue = if (skipAnimation) spentByTimeData.avg() else 0f
    val medianStartValue = if (skipAnimation) spentByTimeData.median() else 0f
    val currencyLocale = LocalCurrencyFormatLocale.current

    Column(modifier) {
        Card(
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
            modifier = Modifier.height(CARD_HEIGHT),
        ) {
            var targetValue by remember { mutableFloatStateOf(totalStartValue) }

            LaunchedEffect(totalSpentData) { targetValue = totalSpentData }

            LaunchedEffect(spentByTimeData) {
                val newData = spentByTimeData.movingTotalChartData().toMutableList()

                if (newData.size == 1) {
                    newData.add(
                        FloatFloatPair(newData.first().first + 1.0f, newData.first().second)
                    )
                }

                if (newData.isNotEmpty()) {
                    totalChartEntryModelProducer.runTransaction {
                        lineSeries {
                            series(x = newData.map { it.first }, y = newData.map { it.second })
                        }
                    }
                }
            }

            val animatedValue =
                animateFloatAsState(
                    targetValue = targetValue,
                    animationSpec = animationSpec,
                    label = "total spent value animation",
                    finishedListener = {
                        // only called here since all animations use same spec and the total is the
                        // highest value
                        // so will take the longest no matter the spec
                        onAnimationEnd()
                    },
                )

            Box(modifier = Modifier.padding(top = CARD_TEXT_TOP_PADDING)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = animatedValue.value.formatToCurrency(currencyLocale),
                        style = Typography.headlineLarge,
                    )
                }
                CartesianChartHost(
                    chart =
                        rememberCartesianChart(
                            rememberLineCartesianLayer(
                                lineProvider =
                                    LineCartesianLayer.LineProvider.series(
                                        vicoTheme.lineCartesianLayerColors.map { color ->
                                            LineCartesianLayer.rememberLine(
                                                areaFill =
                                                    LineCartesianLayer.AreaFill.single(
                                                        fill(
                                                            ShaderProvider.verticalGradient(
                                                                arrayOf(
                                                                    color.copy(0.4f),
                                                                    Color.Transparent,
                                                                )
                                                            )
                                                        )
                                                    )
                                            )
                                        }
                                    )
                            ),
                            marker = rememberMarker(),
                        ),
                    modelProducer = totalChartEntryModelProducer,
                    scrollState =
                        rememberVicoScrollState(
                            scrollEnabled = false,
                            initialScroll = Scroll.Absolute.End,
                        ),
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(top = FULL_CHART_TOP_PADDING)
                            .align(Alignment.BottomCenter),
                )
            }
        }

        Spacer(Modifier.height(CARD_SPACING))

        Row {
            Card(
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                modifier = Modifier.weight(1f).height(CARD_HEIGHT),
            ) {
                var averageTargetValue by remember { mutableFloatStateOf(averageStartValue) }

                LaunchedEffect(spentByTimeData) {
                    averageTargetValue = spentByTimeData.avg()
                    val newData = spentByTimeData.movingAverageChartData().toMutableList()

                    if (newData.size == 1) {
                        newData.add(
                            FloatFloatPair(newData.first().first + 1.0f, newData.first().second)
                        )
                    }

                    if (newData.isNotEmpty()) {
                        averageChartEntryModelProducer.runTransaction {
                            lineSeries {
                                series(x = newData.map { it.first }, y = newData.map { it.second })
                            }
                        }
                    }
                }

                val animatedAverageValue =
                    animateFloatAsState(
                        targetValue = averageTargetValue,
                        animationSpec = animationSpec,
                        label = "average spent value animation",
                    )

                Box(modifier = Modifier.padding(top = CARD_TEXT_TOP_PADDING)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(id = R.string.average),
                            style = Typography.headlineSmall,
                        )

                        Text(
                            text = animatedAverageValue.value.formatToCurrency(currencyLocale),
                            style = Typography.headlineSmall,
                        )
                    }

                    CartesianChartHost(
                        chart =
                            rememberCartesianChart(
                                rememberLineCartesianLayer(
                                    lineProvider =
                                        LineCartesianLayer.LineProvider.series(
                                            vicoTheme.lineCartesianLayerColors.map { color ->
                                                LineCartesianLayer.rememberLine(
                                                    areaFill =
                                                        LineCartesianLayer.AreaFill.single(
                                                            fill(
                                                                ShaderProvider.verticalGradient(
                                                                    arrayOf(
                                                                        color.copy(0.4f),
                                                                        Color.Transparent,
                                                                    )
                                                                )
                                                            )
                                                        )
                                                )
                                            }
                                        )
                                ),
                                marker = rememberMarker(),
                            ),
                        modelProducer = averageChartEntryModelProducer,
                        scrollState =
                            rememberVicoScrollState(
                                scrollEnabled = false,
                                initialScroll = Scroll.Absolute.End,
                            ),
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(top = HALF_CHART_TOP_PADDING)
                                .align(Alignment.BottomCenter),
                    )
                }
            }

            Spacer(Modifier.width(CARD_SPACING))

            Card(
                colors =
                    CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                modifier = Modifier.weight(1f).height(CARD_HEIGHT),
            ) {
                var medianTargetValue by remember { mutableFloatStateOf(medianStartValue) }

                LaunchedEffect(spentByTimeData) {
                    medianTargetValue = spentByTimeData.median()
                    val newData = spentByTimeData.movingMedianChartData().toMutableList()

                    if (newData.size == 1) {
                        newData.add(
                            FloatFloatPair(newData.first().first + 1.0f, newData.first().second)
                        )
                    }

                    if (newData.isNotEmpty()) {
                        medianChartEntryModelProducer.runTransaction {
                            lineSeries {
                                series(x = newData.map { it.first }, y = newData.map { it.second })
                            }
                        }
                    }
                }

                val animatedMedianValue =
                    animateFloatAsState(
                        targetValue = medianTargetValue,
                        animationSpec = animationSpec,
                        label = "median spent value animation",
                    )

                Box(modifier = Modifier.padding(top = CARD_TEXT_TOP_PADDING)) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(id = R.string.median),
                            style = Typography.headlineSmall,
                        )

                        Text(
                            text = animatedMedianValue.value.formatToCurrency(currencyLocale),
                            style = Typography.headlineSmall,
                        )
                    }

                    CartesianChartHost(
                        chart =
                            rememberCartesianChart(
                                rememberLineCartesianLayer(
                                    lineProvider =
                                        LineCartesianLayer.LineProvider.series(
                                            vicoTheme.lineCartesianLayerColors.map { color ->
                                                LineCartesianLayer.rememberLine(
                                                    areaFill =
                                                        LineCartesianLayer.AreaFill.single(
                                                            fill(
                                                                ShaderProvider.verticalGradient(
                                                                    arrayOf(
                                                                        color.copy(0.4f),
                                                                        Color.Transparent,
                                                                    )
                                                                )
                                                            )
                                                        )
                                                )
                                            }
                                        )
                                ),
                                marker = rememberMarker(),
                            ),
                        modelProducer = medianChartEntryModelProducer,
                        scrollState =
                            rememberVicoScrollState(
                                scrollEnabled = false,
                                initialScroll = Scroll.Absolute.End,
                            ),
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(top = HALF_CHART_TOP_PADDING)
                                .align(Alignment.BottomCenter),
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
                spentByTimeData = ItemSpentChartData.generateList(),
                totalSpentData = generateRandomFloatValue(),
            )
        }
    }
}
