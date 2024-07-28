package com.kssidll.arru.ui.screen.home.dashboard


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.ui.screen.home.component.ExpandedHomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.screen.home.component.HomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.theme.ArrugarqTheme

private val TileOuterPadding: Dp = 8.dp
private val TileInnerPadding: Dp = 12.dp

/**
 * @param onSettingsAction Callback called when the 'settings' action is triggered
 * @param totalSpentData Number representing total [ItemEntity] spending
 * @param spentByTimePeriod Current [totalSpentData] time period
 * @param onSpentByTimePeriodUpdate Callback called as a request to update the [totalSpentData] time period, Provides new time period as parameter
 */
@Composable
internal fun DashboardScreen(
    isExpandedScreen: Boolean,
    onSettingsAction: () -> Unit,
    totalSpentData: Data<Float?>,
    spentByTimePeriod: TimePeriodFlowHandler.Periods,
    onSpentByTimePeriodUpdate: (newPeriod: TimePeriodFlowHandler.Periods) -> Unit,
) {
    val totalSpent = if (totalSpentData is Data.Loaded) {
        totalSpentData.data ?: 0f
    } else 0f

    if (isExpandedScreen) {
        ExpandedDashboardScreenContent(
            totalSpentData = totalSpent,
            spentByTimePeriod = spentByTimePeriod,
            onSpentByTimePeriodUpdate = onSpentByTimePeriodUpdate,
        )
    } else {
        DashboardScreenContent(
            totalSpentData = totalSpent,
            onSettingsAction = onSettingsAction,
            spentByTimePeriod = spentByTimePeriod,
            onSpentByTimePeriodUpdate = onSpentByTimePeriodUpdate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenContent(
    onSettingsAction: () -> Unit,
    totalSpentData: Float,
    spentByTimePeriod: TimePeriodFlowHandler.Periods,
    onSpentByTimePeriodUpdate: (newPeriod: TimePeriodFlowHandler.Periods) -> Unit,
) {
    val scrollState = rememberScrollState()

    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val nestedScrollConnection = scrollBehavior.nestedScrollConnection

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    // 'settings' action
                    IconButton(
                        onClick = onSettingsAction,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(id = R.string.navigate_to_settings_description)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                HomeScreenNothingToDisplayOverlay()
            }

            AnimatedVisibility(
                visible = false,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column(
                    modifier = Modifier
                        .nestedScroll(nestedScrollConnection)
                        .verticalScroll(state = scrollState)
                ) {

                }
            }
        }
    }
}

@Composable
private fun ExpandedDashboardScreenContent(
    totalSpentData: Float,
    spentByTimePeriod: TimePeriodFlowHandler.Periods,
    onSpentByTimePeriodUpdate: (newPeriod: TimePeriodFlowHandler.Periods) -> Unit,
) {
    Box {
        // overlay displayed when there is no data available
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                ExpandedHomeScreenNothingToDisplayOverlay()
            }
        }

        val scrollState = rememberScrollState()

        AnimatedVisibility(
            visible = false,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .verticalScroll(state = scrollState)
            ) {

            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DashboardScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(
                isExpandedScreen = false,
                onSettingsAction = {},
                totalSpentData = Data.Loaded(16832.18F),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodUpdate = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyDashboardScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(
                isExpandedScreen = false,
                onSettingsAction = {},
                totalSpentData = Data.Loaded(16832.18F),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodUpdate = {},
            )
        }
    }
}

@PreviewExpanded
@Composable
private fun ExpandedDashboardScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(
                isExpandedScreen = true,
                onSettingsAction = {},
                totalSpentData = Data.Loaded(16832.18F),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodUpdate = {},
            )
        }
    }
}

@PreviewExpanded
@Composable
private fun EmptyExpandedDashboardScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(
                isExpandedScreen = true,
                onSettingsAction = {},
                totalSpentData = Data.Loaded(16832.18F),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodUpdate = {},
            )
        }
    }
}
