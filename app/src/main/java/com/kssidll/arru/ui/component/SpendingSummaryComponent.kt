package com.kssidll.arru.ui.component

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.data.ChartSource
import com.kssidll.arru.domain.getTranslation
import com.kssidll.arru.ui.component.chart.OneDimensionalColumnChart
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.patrykandpatrick.vico.compose.chart.scroll.ChartScrollState
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollState
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.scroll.InitialScroll

@Composable
fun SpendingSummaryComponent(
    spentByTimeData: List<ChartSource>,
    spentByTimePeriod: TimePeriodFlowHandler.Periods?,
    onSpentByTimePeriodUpdate: (TimePeriodFlowHandler.Periods) -> Unit,
    modifier: Modifier = Modifier,
    buttonsModifier: Modifier = Modifier,
    chartModifier: Modifier = Modifier,
    autoScrollSpec: AnimationSpec<Float> = tween(1200),
    scrollState: ChartScrollState = rememberChartScrollState(),
    columnChartEntryModelProducer: ChartEntryModelProducer = remember { ChartEntryModelProducer() },
    runInitialAnimation: Boolean = true,
    initialScroll: InitialScroll = InitialScroll.End,
    columnWidth: Dp = 75.dp,
    columnSpacing: Dp = 12.dp,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
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
            autoScrollSpec = autoScrollSpec,
            scrollState = scrollState,
            runInitialAnimation = runInitialAnimation,
            columnWidth = columnWidth,
            columnSpacing = columnSpacing,
            initialScroll = initialScroll,
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodButtons(
    modifier: Modifier,
    spentByTimePeriod: TimePeriodFlowHandler.Periods?,
    onSpentByTimePeriodUpdate: (TimePeriodFlowHandler.Periods) -> Unit,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .padding(
                start = 10.dp,
                end = 10.dp
            )
    ) {
        TimePeriodFlowHandler.Periods.entries.forEachIndexed { index, it ->
            val shape = when (index) {
                0 -> RoundedCornerShape(
                    topStartPercent = 50,
                    bottomStartPercent = 50
                )

                TimePeriodFlowHandler.Periods.entries.size - 1 -> RoundedCornerShape(
                    topEndPercent = 50,
                    bottomEndPercent = 50
                )

                else -> RectangleShape
            }

            SegmentedButton(
                selected = it == spentByTimePeriod,
                shape = shape,
                label = {
                    Text(it.getTranslation())
                },
                icon = {},
                onClick = {
                    onSpentByTimePeriodUpdate(it)
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.primary,
                    inactiveContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onPrimary,
                    inactiveContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    activeBorderColor = Color.Transparent,
                    inactiveBorderColor = Color.Transparent,
                )
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SpendingSummaryComponentPreview() {
    ArrugarqTheme {
        Surface {
            SpendingSummaryComponent(
                spentByTimeData = ItemSpentByTime.generateList(),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodUpdate = {},
            )
        }
    }
}