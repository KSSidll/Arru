package com.kssidll.arrugarq.presentation.screen.home

import android.content.res.Configuration.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.presentation.theme.*
import com.patrykandpatrick.vico.compose.axis.horizontal.*
import com.patrykandpatrick.vico.compose.chart.*
import com.patrykandpatrick.vico.compose.chart.column.*
import com.patrykandpatrick.vico.compose.chart.scroll.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*
import com.patrykandpatrick.vico.core.chart.scale.*
import com.patrykandpatrick.vico.core.entry.*
import kotlinx.coroutines.*

@Composable
fun HomeScreen(
    onAddItem: () -> Unit,
    itemMonthlyTotals: List<ItemMonthlyTotal>,
) {
    val scope = rememberCoroutineScope()
    val chartScrollState = rememberChartScrollState()
    val chartData: SnapshotStateList<FloatEntry> = remember { mutableStateListOf() }
    var shouldChartScrollToEnd by remember { mutableStateOf(false) }
    LaunchedEffect(itemMonthlyTotals) {
        chartData.clear()
        itemMonthlyTotals.forEachIndexed { index, data ->
            chartData.add(
                FloatEntry(
                    index.toFloat(),
                    data.total.div(100F)
                )
            )
        }
        shouldChartScrollToEnd = true
    }

    // scroll can only happen once the scroll state has been updated with new data
    // thus updating scroll in the same recomposition does nothing
    DisposableEffect(shouldChartScrollToEnd) {
        scope.launch {
            val scrollValue = chartScrollState.maxValue - chartScrollState.value
            chartScrollState.animateScrollBy(
                value = scrollValue,
                animationSpec = FloatTweenSpec(
                    duration = scrollValue.div(6)
                        .toInt(),
                    easing = EaseInOut
                )
            )
        }

        onDispose {
            shouldChartScrollToEnd = false
        }
    }

    Box(modifier = Modifier.padding(8.dp)) {
        Column {
            Row(horizontalArrangement = Arrangement.Center) {
                Chart(
                    chartScrollState = chartScrollState,
                    chart = columnChart(
                        columns = listOf(currentChartStyle.columnChart.columns[0].apply {
                            this.thicknessDp = 50.dp.value
                        }),
                        spacing = 12.dp,
                    ),
                    model = entryModelOf(chartData),
                    topAxis = rememberTopAxis(
                        valueFormatter = { value, _ ->
                            itemMonthlyTotals.getOrNull(value.toInt())?.total?.div(100)
                                .toString()
                        }
                    ),
                    bottomAxis = rememberBottomAxis(
                        valueFormatter = { value, _ ->
                            itemMonthlyTotals.getOrNull(value.toInt())?.yearMonth.orEmpty()
                        },
                    ),
                    autoScaleUp = AutoScaleUp.None,
                    isZoomEnabled = false,
                )
            }
        }

        Box(
            modifier = Modifier.align(Alignment.BottomEnd)
        ) {
            FilledIconButton(
                modifier = Modifier.size(72.dp),
                onClick = onAddItem
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add new item",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}


@Preview(
    group = "HomeScreen",
    name = "Home Screen Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "HomeScreen",
    name = "Home Screen Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun HomeScreenPreview() {
    ArrugarqTheme {
        ProvideChartStyle(
            chartStyle = m3ChartStyle(
                entityColors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                )
            )
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                HomeScreen(
                    onAddItem = {},
                    itemMonthlyTotals = listOf(
                        ItemMonthlyTotal(
                            yearMonth = "2022-08",
                            total = 34821,
                        ),
                        ItemMonthlyTotal(
                            yearMonth = "2022-09",
                            total = 25000,
                        ),
                        ItemMonthlyTotal(
                            yearMonth = "2022-10",
                            total = 50000,
                        ),
                        ItemMonthlyTotal(
                            yearMonth = "2022-11",
                            total = 12345,
                        ),
                    ),
                )
            }
        }
    }
}