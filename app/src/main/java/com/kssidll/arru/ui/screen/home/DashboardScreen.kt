package com.kssidll.arru.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.R
import com.kssidll.arru.helper.BetterNavigationSuiteScaffoldDefaults
import com.kssidll.arru.ui.component.SpendingSummaryComponent
import com.kssidll.arru.ui.component.TotalAverageAndMedianSpendingComponent
import com.kssidll.arru.ui.component.list.RankingList
import com.kssidll.arru.ui.screen.home.component.ExpandedHomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.screen.home.component.HomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.theme.ArruTheme

private val TileOuterPadding: Dp = 8.dp
private val TileInnerPadding: Dp = 12.dp

@Composable
fun DashboardScreen(
    uiState: HomeUiState,
    onEvent: (event: HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navSuiteType =
        BetterNavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    if (navSuiteType != NavigationSuiteType.NavigationBar) {
        ExpandedDashboardScreenContent(uiState = uiState, onEvent = onEvent, modifier = modifier)
    } else {
        DashboardScreenContent(uiState = uiState, onEvent = onEvent, modifier = modifier)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreenContent(
    uiState: HomeUiState,
    onEvent: (event: HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val nestedScrollConnection = scrollBehavior.nestedScrollConnection

    val indication = LocalIndication.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    // 'settings' action
                    IconButton(onClick = { onEvent(HomeEvent.NavigateSettings) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription =
                                stringResource(id = R.string.navigate_to_settings_description),
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = WindowInsets(0),
        modifier = modifier,
    ) { paddingValues ->
        Crossfade(
            targetState = uiState.dashboardScreenNothingToDisplayVisible,
            label = "crossfade between nothing overlay and data display screen",
        ) { nothingToDisplayVisible ->
            if (nothingToDisplayVisible) {
                HomeScreenNothingToDisplayOverlay(
                    modifier = Modifier.padding(paddingValues).consumeWindowInsets(paddingValues)
                )
            } else {
                Column(
                    modifier =
                        Modifier.padding(paddingValues)
                            .consumeWindowInsets(paddingValues)
                            .nestedScroll(nestedScrollConnection)
                            .verticalScroll(state = uiState.dashboardScrollState)
                ) {
                    Spacer(Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = uiState.dashboardChartSectionVisible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Column {
                            TotalAverageAndMedianSpendingComponent(
                                totalChartEntryModelProducer =
                                    uiState.dashboardTotalChartEntryModelProducer,
                                spentByTimeData = uiState.dashboardSpentByTimeChartData,
                                totalSpentData = uiState.totalSpent,
                                modifier = Modifier.padding(horizontal = 12.dp),
                            )

                            Spacer(Modifier.height(28.dp))

                            SpendingSummaryComponent(
                                spentByTimeData = uiState.dashboardSpentByTimeChartData,
                                spentByTimePeriod = uiState.dashboardSpentByTimeChartCurrentPeriod,
                                onSpentByTimePeriodUpdate = {
                                    onEvent(HomeEvent.ChangeDashboardSpentByTimeChartPeriod(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    AnimatedVisibility(
                        visible = uiState.dashboardCategoryCardVisible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                            modifier = Modifier.padding(TileOuterPadding).fillMaxWidth(),
                        ) {
                            RankingList(
                                innerItemPadding = PaddingValues(TileInnerPadding),
                                items = uiState.dashboardCategorySpendingRankingData,
                                modifier =
                                    Modifier.heightIn(min = 144.dp).clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = indication,
                                    ) {
                                        onEvent(HomeEvent.NavigateCategoryRanking)
                                    },
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.dashboardShopCardVisible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                            modifier = Modifier.padding(TileOuterPadding).fillMaxWidth(),
                        ) {
                            RankingList(
                                innerItemPadding = PaddingValues(TileInnerPadding),
                                items = uiState.dashboardShopSpendingRankingData,
                                modifier =
                                    Modifier.heightIn(min = 144.dp).clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = indication,
                                    ) {
                                        onEvent(HomeEvent.NavigateShopRanking)
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandedDashboardScreenContent(
    uiState: HomeUiState,
    onEvent: (event: HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val indication = LocalIndication.current

    Crossfade(
        targetState = uiState.dashboardScreenNothingToDisplayVisible,
        label = "crossfade between nothing overlay and data display screen",
        modifier = modifier,
    ) { nothingToDisplayVisible ->
        if (nothingToDisplayVisible) {
            ExpandedHomeScreenNothingToDisplayOverlay()
        } else {
            Column(
                modifier =
                    Modifier.animateContentSize()
                        .verticalScroll(state = uiState.dashboardScrollState)
            ) {
                Spacer(Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = uiState.dashboardSpentByTimeChartData.isNotEmpty(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Column {
                        TotalAverageAndMedianSpendingComponent(
                            totalChartEntryModelProducer =
                                uiState.dashboardTotalChartEntryModelProducer,
                            spentByTimeData = uiState.dashboardSpentByTimeChartData,
                            totalSpentData = uiState.totalSpent,
                            modifier = Modifier.padding(horizontal = 12.dp),
                        )

                        Spacer(Modifier.height(28.dp))

                        SpendingSummaryComponent(
                            spentByTimeData = uiState.dashboardSpentByTimeChartData,
                            spentByTimePeriod = uiState.dashboardSpentByTimeChartCurrentPeriod,
                            onSpentByTimePeriodUpdate = {
                                onEvent(HomeEvent.ChangeDashboardSpentByTimeChartPeriod(it))
                            },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row {
                    AnimatedVisibility(
                        visible = uiState.dashboardCategoryCardVisible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.weight(1f),
                    ) {
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                            modifier = Modifier.padding(TileOuterPadding).fillMaxWidth(),
                        ) {
                            RankingList(
                                innerItemPadding = PaddingValues(TileInnerPadding),
                                items = uiState.dashboardCategorySpendingRankingData,
                                modifier =
                                    Modifier.heightIn(min = 144.dp).clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = indication,
                                    ) {
                                        onEvent(HomeEvent.NavigateCategoryRanking)
                                    },
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = uiState.dashboardShopCardVisible,
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.weight(1f),
                    ) {
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                            modifier = Modifier.padding(TileOuterPadding).fillMaxWidth(),
                        ) {
                            RankingList(
                                innerItemPadding = PaddingValues(TileInnerPadding),
                                items = uiState.dashboardShopSpendingRankingData,
                                modifier =
                                    Modifier.heightIn(min = 144.dp).clickable(
                                        interactionSource = remember { MutableInteractionSource() },
                                        indication = indication,
                                    ) {
                                        onEvent(HomeEvent.NavigateShopRanking)
                                    },
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DashboardScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(uiState = HomeUiState(), onEvent = {})
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedDashboardScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(uiState = HomeUiState(), onEvent = {})
        }
    }
}
