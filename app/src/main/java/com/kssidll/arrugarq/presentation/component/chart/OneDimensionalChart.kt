package com.kssidll.arrugarq.presentation.component.chart

import android.content.res.*
import androidx.compose.foundation.gestures.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.chart.*
import com.kssidll.arrugarq.presentation.theme.*
import com.patrykandpatrick.vico.compose.axis.horizontal.*
import com.patrykandpatrick.vico.compose.chart.column.*
import com.patrykandpatrick.vico.compose.chart.scroll.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*
import com.patrykandpatrick.vico.core.chart.scale.*
import com.patrykandpatrick.vico.core.entry.*
import com.patrykandpatrick.vico.core.scroll.*
import kotlinx.coroutines.*

@Composable
fun OneDimensionalChart(
    spentByTimeData: List<IChartable>
) {
    val scope = rememberCoroutineScope()
    val scroll = rememberChartScrollState()
    val chartEntryModelProducer = remember { ChartEntryModelProducer() }
    var previousDataSize by remember { mutableIntStateOf(spentByTimeData.size) }

    val chart = columnChart(
        columns = listOf(currentChartStyle.columnChart.columns[0].apply {
            this.thicknessDp = 75.dp.value
        }),
        spacing = 12.dp,
    )

    LaunchedEffect(spentByTimeData) {
        chartEntryModelProducer.setEntries(spentByTimeData.mapIndexed { index, iChartable -> iChartable.chartEntry(index) })
    }

    com.patrykandpatrick.vico.compose.chart.Chart(
        chartScrollState = scroll,
        chartScrollSpec = rememberChartScrollSpec(
            isScrollEnabled = true,
            initialScroll = InitialScroll.End,
            autoScrollCondition = { _, oldModel ->
                if (oldModel == null) return@rememberChartScrollSpec false

                // handle back scroll
                if(spentByTimeData.isNotEmpty() && spentByTimeData.size < previousDataSize) {
                    val itemWidth = (scroll.maxValue + chart.bounds.width()).div(previousDataSize)
                    val itemDiff = spentByTimeData.size - previousDataSize
                    val scrollAmount = itemWidth * itemDiff
                    scope.launch {
                        scroll.animateScrollBy((scroll.maxValue - scroll.value) + scrollAmount)
                    }
                    false
                } else {
                    true
                }.also {
                    previousDataSize = spentByTimeData.size
                }

            },
        ),
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
        isZoomEnabled = true,
        autoScaleUp = AutoScaleUp.None,
    )
}

@Preview(
    group = "OneDimensionalChart",
    name = "One Dimonsional Chart Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "OneDimensionalChart",
    name = "One Dimensional Chart Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun OneDimesionalChartPreview() {
    ArrugarqTheme {
        ProvideChartStyle(
            chartStyle = m3ChartStyle(
                entityColors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                )
            )
        ) {
            Surface {
                OneDimensionalChart(
                    spentByTimeData = listOf(
                        ItemSpentByTime(time = "2022-08", total = 34821),
                        ItemSpentByTime(time = "2022-09", total = 25000),
                        ItemSpentByTime(time = "2022-10", total = 50000),
                        ItemSpentByTime(time = "2022-11", total = 12345),
                    ),
                )
            }
        }
    }
}
