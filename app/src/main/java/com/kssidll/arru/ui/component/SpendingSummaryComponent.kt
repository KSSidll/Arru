package com.kssidll.arru.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.chart.*
import com.kssidll.arru.ui.theme.*
import com.patrykandpatrick.vico.compose.chart.scroll.*
import com.patrykandpatrick.vico.core.entry.*
import com.patrykandpatrick.vico.core.scroll.*

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
                    activeContainerColor = MaterialTheme.colorScheme.tertiary,
                    inactiveContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    activeContentColor = MaterialTheme.colorScheme.onTertiary,
                    inactiveContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
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