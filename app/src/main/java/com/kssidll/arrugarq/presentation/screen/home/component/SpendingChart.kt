package com.kssidll.arrugarq.presentation.screen.home.component

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.chart.*
import com.kssidll.arrugarq.presentation.component.button.*
import com.kssidll.arrugarq.presentation.screen.home.*
import com.kssidll.arrugarq.presentation.theme.*
import com.patrykandpatrick.vico.compose.axis.horizontal.*
import com.patrykandpatrick.vico.compose.chart.column.*
import com.patrykandpatrick.vico.compose.chart.scroll.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*
import com.patrykandpatrick.vico.core.entry.*
import com.patrykandpatrick.vico.core.scroll.*

@Composable
fun SpendingChart(
    spentByTimeData: List<IChartable>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    Column {
        PeriodButtons(
            spentByTimePeriod = spentByTimePeriod,
            onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
        )

        Chart(spentByTimeData = spentByTimeData)
    }
}

@Composable
private fun PeriodButtons(
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SpentByTimePeriod.entries.forEach {
            SelectButton(
                selected = (it == spentByTimePeriod),
                onClick = {
                    onSpentByTimePeriodSwitch(it)
                },
                text = it.name,
            )
        }
    }
}

@Composable
private fun Chart(
    spentByTimeData: List<IChartable>
) {
    val chartData: SnapshotStateList<ChartEntry> = remember { mutableStateListOf() }
    LaunchedEffect(spentByTimeData) {
        // avoid setting empty chart data because initial scroll likes to think it already happened when we do that
        if (spentByTimeData.isNotEmpty()) {
            chartData.clear()
            chartData.addAll(spentByTimeData.mapIndexed { index, iChartable -> iChartable.chartEntry(index) })
        }
    }

    com.patrykandpatrick.vico.compose.chart.Chart(
        chartScrollState = rememberChartScrollState(),
        chartScrollSpec = rememberChartScrollSpec(
            isScrollEnabled = true,
            initialScroll = InitialScroll.End,
        ),
        chart = columnChart(
            columns = listOf(currentChartStyle.columnChart.columns[0].apply {
                this.thicknessDp = 75.dp.value
            }),
            spacing = 12.dp,
        ),
        chartModelProducer = ChartEntryModelProducer(chartData),
        topAxis = rememberTopAxis(
            valueFormatter = { value, _ ->
                spentByTimeData.getOrNull(value.toInt())
                    ?.topAxisLabel()
                    .orEmpty()
            }
        ),
        bottomAxis = rememberBottomAxis(
            valueFormatter = { value, _ ->
                spentByTimeData.getOrNull(value.toInt())
                    ?.bottomAxisLabel()
                    .orEmpty()
            },
        ),
        isZoomEnabled = false,
    )
}

@Preview(
    group = "SpendingChart",
    name = "Spending Chart Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "SpendingChart",
    name = "Spending Chart Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun SpendingChartPreview() {
    ArrugarqTheme {
        ProvideChartStyle(
            chartStyle = m3ChartStyle(
                entityColors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                )
            )
        ) {
            Surface {
                SpendingChart(
                    spentByTimeData = listOf(
                        ItemSpentByTime(time = "2022-08", total = 34821),
                        ItemSpentByTime(time = "2022-09", total = 25000),
                        ItemSpentByTime(time = "2022-10", total = 50000),
                        ItemSpentByTime(time = "2022-11", total = 12345),
                    ),
                    spentByTimePeriod = SpentByTimePeriod.Month,
                    onSpentByTimePeriodSwitch = {},
                )
            }
        }
    }
}