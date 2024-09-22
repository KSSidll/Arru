package com.kssidll.arru.ui.component.chart

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.domain.data.ChartSource
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberTopAxis
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.scroll.ChartScrollState
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.compose.style.currentChartStyle
import com.patrykandpatrick.vico.core.chart.edges.FadingEdges
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.scroll.AutoScrollCondition
import com.patrykandpatrick.vico.core.scroll.InitialScroll

@Composable
fun OneDimensionalColumnChart(
    data: List<ChartSource>,
    modifier: Modifier = Modifier,
    chartEntryModelProducer: ChartEntryModelProducer = remember { ChartEntryModelProducer() },
    fadingEdges: FadingEdges? = null,
    isZoomEnabled: Boolean = false,
    columnWidth: Dp = 75.dp,
    columnSpacing: Dp = 12.dp,
    autoScrollSpec: AnimationSpec<Float> = tween(1200),
    diffAnimationSpec: AnimationSpec<Float> = autoScrollSpec,
    scrollState: ChartScrollState = rememberChartScrollState(),
    runInitialAnimation: Boolean = true,
    initialScroll: InitialScroll = InitialScroll.End,
) {
    val defaultColumns = currentChartStyle.columnChart.columns

    val chart = columnChart(
        remember(defaultColumns) {
            defaultColumns.map { defaultColumn ->
                LineComponent(
                    defaultColumn.color,
                    columnWidth.value,
                    Shapes.roundedCornerShape(allPercent = 30)
                )
            }
        },
        spacing = columnSpacing,
    )

    LaunchedEffect(data) {
        chartEntryModelProducer.setEntries(data.mapIndexed { index, iChartable ->
            iChartable.chartEntry(
                index
            )
        })
    }

    com.patrykandpatrick.vico.compose.chart.Chart(
        modifier = modifier,
        chartScrollState = scrollState,
        chartScrollSpec = rememberChartScrollSpec(
            isScrollEnabled = true,
            initialScroll = initialScroll,
            autoScrollCondition = AutoScrollCondition.OnModelSizeIncreased,
            autoScrollAnimationSpec = autoScrollSpec,
        ),
        diffAnimationSpec = diffAnimationSpec,
        chart = chart,
        chartModelProducer = chartEntryModelProducer,
        topAxis = rememberTopAxis(
            valueFormatter = { value, _ ->
                data.getOrNull(value.toInt())
                    ?.topAxisLabel()
                    .orEmpty()
            },
        ),
        bottomAxis = rememberBottomAxis(
            valueFormatter = { value, _ ->
                data.getOrNull(value.toInt())
                    ?.bottomAxisLabel()
                    .orEmpty()
            },
        ),
        runInitialAnimation = runInitialAnimation,
        fadingEdges = fadingEdges,
        isZoomEnabled = isZoomEnabled,
        autoScaleUp = AutoScaleUp.None,
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
