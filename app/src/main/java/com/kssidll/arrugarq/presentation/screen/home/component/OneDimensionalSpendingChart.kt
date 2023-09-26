package com.kssidll.arrugarq.presentation.screen.home.component

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.chart.*
import com.kssidll.arrugarq.presentation.component.button.*
import com.kssidll.arrugarq.presentation.component.chart.*
import com.kssidll.arrugarq.presentation.screen.home.*
import com.kssidll.arrugarq.presentation.theme.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*

@Composable
fun OneDimensionalSpendingChart(
    spentByTimeData: List<IChartable>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    Column {
        PeriodButtons(
            spentByTimePeriod = spentByTimePeriod,
            onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
        )

        OneDimensionalChart(spentByTimeData = spentByTimeData)
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

@Preview(
    group = "OneDimensionalSpendingChart",
    name = "One Dimonsional Spending Chart Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "OneDimensionalSpendingChart",
    name = "One Dimensional Spending Chart Light",
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