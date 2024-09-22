package com.kssidll.arru.ui.screen.home.dashboard


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shop
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionSpentByTime
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.domain.data.loadedData
import com.kssidll.arru.domain.data.loadedEmpty
import com.kssidll.arru.ui.component.SpendingSummaryComponent
import com.kssidll.arru.ui.component.TotalAverageAndMedianSpendingComponent
import com.kssidll.arru.ui.component.list.RankingList
import com.kssidll.arru.ui.screen.home.component.ExpandedHomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.screen.home.component.HomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.theme.ArrugarqTheme

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
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
        ) {
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
                                    modifier = Modifier.padding(horizontal = 12.dp)
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
