package com.kssidll.arru.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.ui.component.chart.OneDimensionalColumnChart
import com.kssidll.arru.ui.component.chart.oneDimensionalColumnChartDefaultScrollState
import com.kssidll.arru.ui.theme.ArruTheme
import com.patrykandpatrick.vico.compose.cartesian.VicoScrollState
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.collections.immutable.ImmutableList

enum class SpendingSummaryPeriod {
    Day,
    Week,
    Month,
    Year,
}

@Composable
@ReadOnlyComposable
fun SpendingSummaryPeriod.getTranslation(): String {
    return when (this) {
        SpendingSummaryPeriod.Day -> stringResource(R.string.day)
        SpendingSummaryPeriod.Week -> stringResource(R.string.week)
        SpendingSummaryPeriod.Month -> stringResource(R.string.month)
        SpendingSummaryPeriod.Year -> stringResource(R.string.year)
    }
}

@Composable
fun SpendingSummaryComponent(
    spentByTimeData: ImmutableList<ChartSource>,
    spentByTimePeriod: SpendingSummaryPeriod?,
    onSpentByTimePeriodUpdate: (SpendingSummaryPeriod) -> Unit,
    modifier: Modifier = Modifier,
    buttonsModifier: Modifier = Modifier,
    chartModifier: Modifier = Modifier,
    scrollState: VicoScrollState = oneDimensionalColumnChartDefaultScrollState(),
    columnChartEntryModelProducer: CartesianChartModelProducer = remember {
        CartesianChartModelProducer()
    },
    runInitialAnimation: Boolean = true,
    columnWidth: Dp = 75.dp,
    columnSpacing: Dp = 12.dp,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        PeriodButtons(
            modifier = buttonsModifier,
            spentByTimePeriod = spentByTimePeriod,
            onSpentByTimePeriodUpdate = onSpentByTimePeriodUpdate,
        )

        Spacer(modifier = Modifier.height(24.dp))

        OneDimensionalColumnChart(
            data = spentByTimeData,
            modifier = chartModifier,
            chartEntryModelProducer = columnChartEntryModelProducer,
            scrollState = scrollState,
            runInitialAnimation = runInitialAnimation,
            columnWidth = columnWidth,
            columnSpacing = columnSpacing,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodButtons(
    spentByTimePeriod: SpendingSummaryPeriod?,
    onSpentByTimePeriodUpdate: (SpendingSummaryPeriod) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier.padding(start = 10.dp, end = 10.dp)) {
        SpendingSummaryPeriod.entries.forEachIndexed { index, it ->
            val shape =
                when (index) {
                    0 -> RoundedCornerShape(topStartPercent = 50, bottomStartPercent = 50)

                    SpendingSummaryPeriod.entries.size - 1 ->
                        RoundedCornerShape(topEndPercent = 50, bottomEndPercent = 50)

                    else -> RectangleShape
                }

            SegmentedButton(
                selected = it == spentByTimePeriod,
                shape = shape,
                label = { Text(it.getTranslation()) },
                icon = {},
                onClick = { onSpentByTimePeriodUpdate(it) },
                colors =
                    SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        inactiveContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        inactiveContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        activeBorderColor = Color.Transparent,
                        inactiveBorderColor = Color.Transparent,
                    ),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SpendingSummaryComponentPreview() {
    ArruTheme {
        Surface {
            SpendingSummaryComponent(
                spentByTimeData = ItemSpentChartData.generateList(),
                spentByTimePeriod = SpendingSummaryPeriod.Month,
                onSpentByTimePeriodUpdate = {},
            )
        }
    }
}
