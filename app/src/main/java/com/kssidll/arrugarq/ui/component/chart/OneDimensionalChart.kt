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
fun OneDimensionalChart(
    spentByTimeData: List<Chartable>,
    modifier: Modifier = Modifier,
    fadingEdges: FadingEdges? = null,
    isZoomEnabled: Boolean = false,
    autoScrollSpec: AnimationSpec<Float> = tween(1200),
    diffAnimationSpec: AnimationSpec<Float> = autoScrollSpec,
) {
    val scope = rememberCoroutineScope()
    val scroll = rememberChartScrollState()
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    var previousDataSize by remember { mutableIntStateOf(spentByTimeData.size) }

    val defaultColumns = currentChartStyle.columnChart.columns

    val chart = columnChart(
        remember(defaultColumns) {
            defaultColumns.map { defaultColumn ->
                LineComponent(
                    defaultColumn.color,
                    75.dp.value,
                    Shapes.roundedCornerShape(allPercent = 30)
                )
            }
        },
        spacing = 12.dp,
    )

    LaunchedEffect(spentByTimeData) {
        chartEntryModelProducer.setEntries(spentByTimeData.mapIndexed { index, iChartable -> iChartable.chartEntry(index) })
    }

    com.patrykandpatrick.vico.compose.chart.Chart(
        modifier = modifier,
        chartScrollState = scroll,
        chartScrollSpec = rememberChartScrollSpec(
            isScrollEnabled = true,
            initialScroll = InitialScroll.End,
            autoScrollCondition = { _, oldModel ->
                if (oldModel == null) return@rememberChartScrollSpec false

                // handle back scroll
                if (spentByTimeData.isNotEmpty() && spentByTimeData.size < previousDataSize) {
                    val itemWidth = (scroll.maxValue + chart.bounds.width()).div(previousDataSize)
                    val itemDiff = spentByTimeData.size - previousDataSize
                    val scrollAmount = itemWidth * itemDiff
                    val relativeScrollAmount = (scroll.maxValue - scroll.value) + scrollAmount
                    scope.launch {
                        scroll.animateScrollBy(
                            value = relativeScrollAmount,
                            animationSpec = autoScrollSpec,
                        )
                    }
                    false
                } else {
                    true
                }.also {
                    previousDataSize = spentByTimeData.size
                }

            },
            autoScrollAnimationSpec = autoScrollSpec,
        ),
        diffAnimationSpec = diffAnimationSpec,
        chart = chart,
        chartModelProducer = chartEntryModelProducer,
        topAxis = rememberTopAxis(
            valueFormatter = { value, _ ->
                spentByTimeData.getOrNull(value.toInt())
                    ?.topAxisLabel()
                    .orEmpty()
            },
        ),
        bottomAxis = rememberBottomAxis(
            valueFormatter = { value, _ ->
                spentByTimeData.getOrNull(value.toInt())
                    ?.bottomAxisLabel()
                    .orEmpty()
            },
        ),
        fadingEdges = fadingEdges,
        isZoomEnabled = isZoomEnabled,
        autoScaleUp = AutoScaleUp.None,
    )
}

@Preview(
    group = "One Dimensional Chart",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "One Dimensional Chart",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun OneDimesionalChartPreview() {
    ArrugarqTheme {
        Surface {
            OneDimensionalChart(
                spentByTimeData = getFakeSpentByTimeData(),
            )
        }
    }
}
