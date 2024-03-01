package com.kssidll.arru.ui.screen.home.dashboard


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.screen.home.component.*
import com.kssidll.arru.ui.theme.*

private val TileOuterPadding: Dp = 8.dp
private val TileInnerPadding: Dp = 12.dp

/**
 * @param onSettingsAction Callback called when the 'settings' action is triggered
 * @param onCategoryRankingCardClick Callback to call wen [ProductCategory] ranking card is clicked
 * @param onShopRankingCardClick Callback to call when [Shop] ranking card is clicked
 * @param totalSpentData Number representing total [Item] spending
 * @param spentByShopData List of items representing [Shop] spending in time
 * @param spentByCategoryData List of items representing [ProductCategory] spending in time
 * @param spentByTimeData List of items representing [Item] spending in time
 * @param spentByTimePeriod Current [totalSpentData] time period
 * @param onSpentByTimePeriodUpdate Callback called as a request to update the [totalSpentData] time period, Provides new time period as parameter
 */
@Composable
internal fun DashboardScreen(
    isExpandedScreen: Boolean,
    onSettingsAction: () -> Unit,
    onCategoryRankingCardClick: () -> Unit,
    onShopRankingCardClick: () -> Unit,
    totalSpentData: Data<Float?>,
    spentByShopData: Data<List<TransactionTotalSpentByShop>>,
    spentByCategoryData: Data<List<ItemSpentByCategory>>,
    spentByTimeData: Data<List<TransactionSpentByTime>>,
    spentByTimePeriod: TimePeriodFlowHandler.Periods,
    onSpentByTimePeriodUpdate: (newPeriod: TimePeriodFlowHandler.Periods) -> Unit,
) {
    val totalSpent = if (totalSpentData is Data.Loaded) {
        totalSpentData.data ?: 0f
    } else 0f

    if (isExpandedScreen) {
        ExpandedDashboardScreenContent(
            onCategoryRankingCardClick = onCategoryRankingCardClick,
            onShopRankingCardClick = onShopRankingCardClick,
            totalSpentData = totalSpent,
            spentByShopData = spentByShopData,
            spentByCategoryData = spentByCategoryData,
            spentByTimeData = spentByTimeData,
            spentByTimePeriod = spentByTimePeriod,
            onSpentByTimePeriodUpdate = onSpentByTimePeriodUpdate,
        )
    } else {
        DashboardScreenContent(
            onSettingsAction = onSettingsAction,
            onCategoryRankingCardClick = onCategoryRankingCardClick,
            onShopRankingCardClick = onShopRankingCardClick,
            totalSpentData = totalSpent,
            spentByShopData = spentByShopData,
            spentByCategoryData = spentByCategoryData,
            spentByTimeData = spentByTimeData,
            spentByTimePeriod = spentByTimePeriod,
            onSpentByTimePeriodUpdate = onSpentByTimePeriodUpdate,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenContent(
    onSettingsAction: () -> Unit,
    onCategoryRankingCardClick: () -> Unit,
    onShopRankingCardClick: () -> Unit,
    totalSpentData: Float,
    spentByShopData: Data<List<TransactionTotalSpentByShop>>,
    spentByCategoryData: Data<List<ItemSpentByCategory>>,
    spentByTimeData: Data<List<TransactionSpentByTime>>,
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
        Box(modifier = Modifier.padding(paddingValues)) {
            AnimatedVisibility(
                visible = spentByTimeData.loadedEmpty() && spentByCategoryData.loadedEmpty() && spentByShopData.loadedEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                HomeScreenNothingToDisplayOverlay()
            }

            AnimatedVisibility(
                visible = spentByTimeData.loadedData() || spentByCategoryData.loadedData() || spentByShopData.loadedData(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .animateContentSize()
                        .nestedScroll(nestedScrollConnection)
                        .verticalScroll(state = scrollState)
                ) {
                    Spacer(Modifier.height(16.dp))

                    AnimatedVisibility(
                        visible = spentByTimeData.loadedData(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        if (spentByTimeData is Data.Loaded) {
                            Column {
                                TotalAverageAndMedianSpendingComponent(
                                    spentByTimeData = spentByTimeData.data,
                                    totalSpentData = totalSpentData,
                                )

                                Spacer(Modifier.height(28.dp))

                                SpendingSummaryComponent(
                                    spentByTimeData = spentByTimeData.data,
                                    spentByTimePeriod = spentByTimePeriod,
                                    onSpentByTimePeriodUpdate = onSpentByTimePeriodUpdate,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    AnimatedVisibility(
                        visible = spentByCategoryData.loadedData(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        if (spentByCategoryData is Data.Loaded) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                                modifier = Modifier
                                    .padding(TileOuterPadding)
                                    .fillMaxWidth()
                            ) {
                                RankingList(
                                    innerItemPadding = PaddingValues(TileInnerPadding),
                                    items = spentByCategoryData.data,
                                    modifier = Modifier
                                        .heightIn(min = 144.dp)
                                        .clickable {
                                            onCategoryRankingCardClick()
                                        }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = spentByShopData.loadedData(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                    ) {
                        if (spentByShopData is Data.Loaded) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                                modifier = Modifier
                                    .padding(TileOuterPadding)
                                    .fillMaxWidth()
                            ) {
                                RankingList(
                                    innerItemPadding = PaddingValues(TileInnerPadding),
                                    items = spentByShopData.data,
                                    modifier = Modifier
                                        .heightIn(min = 144.dp)
                                        .clickable {
                                            onShopRankingCardClick()
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedDashboardScreenContent(
    onCategoryRankingCardClick: () -> Unit,
    onShopRankingCardClick: () -> Unit,
    totalSpentData: Float,
    spentByShopData: Data<List<TransactionTotalSpentByShop>>,
    spentByCategoryData: Data<List<ItemSpentByCategory>>,
    spentByTimeData: Data<List<TransactionSpentByTime>>,
    spentByTimePeriod: TimePeriodFlowHandler.Periods,
    onSpentByTimePeriodUpdate: (newPeriod: TimePeriodFlowHandler.Periods) -> Unit,
) {
    Box {
        // overlay displayed when there is no data available
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = spentByTimeData.loadedEmpty() && spentByCategoryData.loadedEmpty() && spentByShopData.loadedEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                ExpandedHomeScreenNothingToDisplayOverlay()
            }
        }

        val scrollState = rememberScrollState()

        AnimatedVisibility(
            visible = spentByTimeData.loadedData() || spentByCategoryData.loadedData() || spentByShopData.loadedData(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(
                modifier = Modifier
                    .animateContentSize()
                    .verticalScroll(state = scrollState)
            ) {
                Spacer(Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = spentByTimeData.loadedData(),
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    if (spentByTimeData is Data.Loaded) {
                        Column {
                            TotalAverageAndMedianSpendingComponent(
                                spentByTimeData = spentByTimeData.data,
                                totalSpentData = totalSpentData,
                            )

                            Spacer(Modifier.height(28.dp))

                            SpendingSummaryComponent(
                                spentByTimeData = spentByTimeData.data,
                                spentByTimePeriod = spentByTimePeriod,
                                onSpentByTimePeriodUpdate = onSpentByTimePeriodUpdate,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row {
                    AnimatedVisibility(
                        visible = spentByCategoryData.loadedData(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (spentByCategoryData is Data.Loaded) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                                modifier = Modifier.padding(TileOuterPadding)
                            ) {
                                RankingList(
                                    innerItemPadding = PaddingValues(TileInnerPadding),
                                    items = spentByCategoryData.data,
                                    modifier = Modifier
                                        .heightIn(min = 144.dp)
                                        .clickable {
                                            onCategoryRankingCardClick()
                                        }
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = spentByShopData.loadedData(),
                        enter = fadeIn(),
                        exit = fadeOut(),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (spentByShopData is Data.Loaded) {
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                                ),
                                modifier = Modifier.padding(TileOuterPadding)
                            ) {
                                RankingList(
                                    innerItemPadding = PaddingValues(TileInnerPadding),
                                    items = spentByShopData.data,
                                    modifier = Modifier
                                        .heightIn(min = 144.dp)
                                        .clickable {
                                            onShopRankingCardClick()
                                        }
                                )
                            }
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
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(
                isExpandedScreen = false,
                onSettingsAction = {},
                onCategoryRankingCardClick = {},
                onShopRankingCardClick = {},
                totalSpentData = Data.Loaded(16832.18F),
                spentByShopData = Data.Loaded(TransactionTotalSpentByShop.generateList()),
                spentByCategoryData = Data.Loaded(ItemSpentByCategory.generateList()),
                spentByTimeData = Data.Loaded(TransactionSpentByTime.generateList()),
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
                onCategoryRankingCardClick = {},
                onShopRankingCardClick = {},
                totalSpentData = Data.Loaded(16832.18F),
                spentByShopData = Data.Loading(),
                spentByCategoryData = Data.Loading(),
                spentByTimeData = Data.Loading(),
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
                onCategoryRankingCardClick = {},
                onShopRankingCardClick = {},
                totalSpentData = Data.Loaded(16832.18F),
                spentByShopData = Data.Loaded(TransactionTotalSpentByShop.generateList()),
                spentByCategoryData = Data.Loaded(ItemSpentByCategory.generateList()),
                spentByTimeData = Data.Loaded(TransactionSpentByTime.generateList()),
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
                onCategoryRankingCardClick = {},
                onShopRankingCardClick = {},
                totalSpentData = Data.Loaded(16832.18F),
                spentByShopData = Data.Loading(),
                spentByCategoryData = Data.Loading(),
                spentByTimeData = Data.Loading(),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodUpdate = {},
            )
        }
    }
}
