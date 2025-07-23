package com.kssidll.arru.ui.component.chart

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.LocalCurrencyFormatLocale
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.domain.data.ChartSource
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberTop
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.core.cartesian.AutoScrollCondition
import com.patrykandpatrick.vico.core.cartesian.FadingEdges
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import kotlinx.collections.immutable.ImmutableList

private val TopAxisLabelKey = ExtraStore.Key<Map<Float, String>>()
private val BottomAxisLabelKey = ExtraStore.Key<Map<Float, String>>()

@Composable
fun oneDimensionalColumnChartDefaultScrollState() = rememberVicoScrollState(
    initialScroll = Scroll.Absolute.End,
    autoScrollAnimationSpec = snap(0),
    autoScrollCondition = AutoScrollCondition.OnModelGrowth
)


@Composable
fun OneDimensionalColumnChart(
    data: ImmutableList<ChartSource>,
    modifier: Modifier = Modifier,
    chartEntryModelProducer: CartesianChartModelProducer = remember { CartesianChartModelProducer() },
    fadingEdges: FadingEdges? = null,
    isZoomEnabled: Boolean = false,
    columnWidth: Dp = 75.dp,
    columnSpacing: Dp = 12.dp,
    diffAnimationSpec: AnimationSpec<Float> = tween(1200),
    scrollState: VicoScrollState = oneDimensionalColumnChartDefaultScrollState(),
    runInitialAnimation: Boolean = true,
) {
    val currencyLocale = LocalCurrencyFormatLocale.current

    LaunchedEffect(data) {
        // Peak engineering
        val newData = data.mapIndexed { index, it ->
            Triple(
                it.chartEntry(index),
                it.topAxisLabel(currencyLocale) ?: "??",
                it.bottomAxisLabel(currencyLocale) ?: "??"
            )
        }

        chartEntryModelProducer.runTransaction {
            columnSeries {
                series(
                    x = newData.map { it.first.first },
                    y = newData.map { it.first.second }
                )
            }

            extras { extraStore ->
                extraStore[TopAxisLabelKey] = buildMap { newData.forEach { put(it.first.first, it.second) } }
                extraStore[BottomAxisLabelKey] = buildMap { newData.forEach { put(it.first.first, it.third) } }
            }
        }
    }

    val chart = rememberCartesianChart(
        rememberColumnCartesianLayer(
            columnCollectionSpacing = columnSpacing,
            columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                vicoTheme.columnCartesianLayerColors.map { color ->
                    rememberLineComponent(
                        fill = fill(color),
                        thickness = columnWidth,
                        shape = CorneredShape.rounded(allPercent = 30),
                    )
                }
            )
        ),
        fadingEdges = fadingEdges,
        topAxis = HorizontalAxis.rememberTop(
            valueFormatter = CartesianValueFormatter { context, value, _ ->
                context.model.extraStore.getOrNull(TopAxisLabelKey)?.get(value.toFloat()) ?: "??"
            },
            itemPlacer = HorizontalAxis.ItemPlacer.segmented()
        ),
        bottomAxis = HorizontalAxis.rememberBottom(
            valueFormatter = CartesianValueFormatter { context, value, _ ->
                context.model.extraStore.getOrNull(BottomAxisLabelKey)?.get(value.toFloat()) ?: "??"
            },
            itemPlacer = HorizontalAxis.ItemPlacer.segmented()
        ),
        decorations = listOf()
    )

    CartesianChartHost(
        modifier = modifier,
        modelProducer = chartEntryModelProducer,
        scrollState = scrollState,
        zoomState = rememberVicoZoomState(zoomEnabled = isZoomEnabled),
        animationSpec = diffAnimationSpec,
        animateIn = runInitialAnimation,
        chart = chart
    )
}

@PreviewLightDark
@Composable
private fun OneDimesionalColumnChartPreview() {
    ArrugarqTheme {
        Surface {
            OneDimensionalColumnChart(
                data = ItemSpentByTime.generateList(),
            )
        }
    }
}
