package com.kssidll.arrugarq.presentation.screen.home

import android.content.res.Configuration.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.*
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
import com.patrykandpatrick.vico.core.entry.*
import com.patrykandpatrick.vico.core.scroll.*

@Composable
fun HomeScreen(
    onAddItem: () -> Unit,
    itemMonthlyTotals: List<ItemMonthlyTotal>,
) {
    val chartScrollState = rememberChartScrollState()
    val chartData: SnapshotStateList<ChartEntry> = remember { mutableStateListOf() }
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
    }

    Column {
        // Content
        Column(
            modifier = Modifier
                .weight(1F)
                .padding(start = 8.dp, top = 8.dp, end = 8.dp)
        ) {
            Chart(
                chartScrollState = chartScrollState,
                chartScrollSpec = rememberChartScrollSpec(
                    isScrollEnabled = true,
                    initialScroll = InitialScroll.End,
                ),
                chart = columnChart(
                    columns = listOf(currentChartStyle.columnChart.columns[0].apply {
                        this.thicknessDp = 50.dp.value
                    }),
                    spacing = 12.dp,
                ),
                chartModelProducer = ChartEntryModelProducer(chartData),
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
                isZoomEnabled = false,
            )


        }

        // Bottom Bar
        Row {
            val configuration = LocalConfiguration.current
            val screenWidth = configuration.screenWidthDp.dp
            val addItemButtonSize = 72.dp
            val canvasSize = 40.dp

            val color = MaterialTheme.colorScheme.surfaceContainer
            val bg = MaterialTheme.colorScheme.surface

            Row(
                modifier = Modifier
                    .width(screenWidth - addItemButtonSize - canvasSize)
                    .height(addItemButtonSize)
                    .background(color)
            ) {

            }

            Box(
                modifier = Modifier.width(addItemButtonSize + canvasSize)
            ) {
                Canvas(
                    modifier = Modifier
                        .width(canvasSize)
                        .height(addItemButtonSize)
                ) {
                    drawRect(
                        color = color,
                    )
                    drawCircle(
                        color = bg,
                        blendMode = BlendMode.SrcIn,
                        radius = size.height,
                        center = Offset(size.height, size.height.div(5)),
                    )
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .width(addItemButtonSize)
                        .offset(1.dp, (-12).dp)
                ) {
                    FilledIconButton(
                        modifier = Modifier.fillMaxWidth().aspectRatio(1F),
                        onClick = onAddItem,
                        colors = IconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add new item",
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
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
            Surface(modifier = Modifier.fillMaxSize()) {
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