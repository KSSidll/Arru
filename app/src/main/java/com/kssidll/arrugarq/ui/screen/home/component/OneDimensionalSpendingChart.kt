package com.kssidll.arrugarq.ui.screen.home.component

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.component.button.*
import com.kssidll.arrugarq.ui.component.chart.*
import com.kssidll.arrugarq.ui.screen.home.*
import com.kssidll.arrugarq.ui.theme.*
import com.patrykandpatrick.vico.compose.chart.edges.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*

@Composable
fun OneDimensionalSpendingChart(
    spentByTimeData: List<Chartable>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
    modifier: Modifier = Modifier,
    chartModifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PeriodButtons(
            spentByTimePeriod = spentByTimePeriod,
            onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
        )

        Spacer(modifier = Modifier.height(8.dp))

        OneDimensionalChart(
            spentByTimeData = spentByTimeData,
            modifier = chartModifier,
            fadingEdges = FadingEdges(
                startEdgeWidth = 3.dp
            ),
        )
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
            ) {
                Text(it.name)
            }
        }
    }
}

@Preview(
    group = "One Dimensional Spending Chart",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "One Dimensional Spending Chart",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun OneDimensionalSpendingChartPreview() {
    ArrugarqTheme {
        ProvideChartStyle(
            chartStyle = m3ChartStyle(
                entityColors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                )
            )
        ) {
            Surface {
                OneDimensionalSpendingChart(
                    spentByTimeData = listOf(
                        ItemSpentByTime(
                            time = "2022-08",
                            total = 34821
                        ),
                        ItemSpentByTime(
                            time = "2022-09",
                            total = 25000
                        ),
                        ItemSpentByTime(
                            time = "2022-10",
                            total = 50000
                        ),
                        ItemSpentByTime(
                            time = "2022-11",
                            total = 12345
                        ),
                    ),
                    spentByTimePeriod = SpentByTimePeriod.Month,
                    onSpentByTimePeriodSwitch = {},
                )
            }
        }
    }
}