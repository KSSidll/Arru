package com.kssidll.arrugarq.ui.component.chart

import android.content.res.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.theme.*
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
import kotlinx.coroutines.*

@Composable
fun OneDimensionalColumnChart(
    data: List<Chartable>,
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
) {
    val scope = rememberCoroutineScope()

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
            initialScroll = InitialScroll.End,
            autoScrollCondition = { _, oldModel ->
                if (oldModel == null) return@rememberChartScrollSpec false

                val newDataSize = data.size
                val previousDataSize = oldModel.entries.getOrElse(0) { emptyList() }
                    .indexOfLast { it.y > 0F }

                // handle back scroll
                if (newDataSize < previousDataSize) {
                    val itemWidth =
                        (scrollState.maxValue + chart.bounds.width()).div(previousDataSize)
                    val itemDiff = newDataSize - previousDataSize
                    val scrollAmount = itemWidth * itemDiff
                    val relativeScrollAmount =
                        (scrollState.maxValue - scrollState.value) + scrollAmount
                    scope.launch {
                        scrollState.animateScrollBy(
                            value = relativeScrollAmount,
                            animationSpec = autoScrollSpec,
                        )
                    }
                    return@rememberChartScrollSpec false
                }

                return@rememberChartScrollSpec true
            },
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

@Preview(
    group = "One Dimensional Column Chart",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "One Dimensional Column Chart",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun OneDimesionalColumnChartPreview() {
    ArrugarqTheme {
        Surface {
            OneDimensionalColumnChart(
                data = generateRandomItemSpentByTimeList(),
            )
        }
    }
}