package com.kssidll.arru.ui.component.chart

import androidx.compose.animation.core.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.theme.*
import com.patrykandpatrick.vico.compose.axis.horizontal.*
import com.patrykandpatrick.vico.compose.chart.column.*
import com.patrykandpatrick.vico.compose.chart.scroll.*
import com.patrykandpatrick.vico.compose.style.*
import com.patrykandpatrick.vico.core.chart.edges.*
import com.patrykandpatrick.vico.core.chart.scale.*
import com.patrykandpatrick.vico.core.component.shape.*
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.*
import com.patrykandpatrick.vico.core.scroll.*

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
        chartEntryModelProducer.setEntries(data.mapIndexed { index, iChartable -> iChartable.chartEntry(index) })
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
