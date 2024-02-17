package com.kssidll.arru.ui.screen.home.dashboard


import android.content.res.Configuration.*
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
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.*
import com.kssidll.arru.ui.component.list.*
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DashboardScreen(
    onSettingsAction: () -> Unit,
    onCategoryRankingCardClick: () -> Unit,
    onShopRankingCardClick: () -> Unit,
    totalSpentData: Float,
    spentByShopData: List<TransactionTotalSpentByShop>,
    spentByCategoryData: List<ItemSpentByCategory>,
    spentByTimeData: List<ChartSource>,
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
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .nestedScroll(nestedScrollConnection)
                .verticalScroll(state = scrollState)
        ) {
            Spacer(Modifier.height(16.dp))

            TotalAverageAndMedianSpendingComponent(
                spentByTimeData = spentByTimeData,
                totalSpentData = totalSpentData,
            )

            Spacer(Modifier.height(28.dp))

            AnimatedVisibility(visible = spentByTimeData.isNotEmpty()) {
                SpendingSummaryComponent(
                    modifier = Modifier.animateContentSize(),
                    spentByTimeData = spentByTimeData,
                    spentByTimePeriod = spentByTimePeriod,
                    onSpentByTimePeriodUpdate = onSpentByTimePeriodUpdate,
                )
            }

            Spacer(Modifier.height(4.dp))

            AnimatedVisibility(visible = spentByCategoryData.isNotEmpty()) {
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
                        items = spentByCategoryData,
                        modifier = Modifier
                            .heightIn(min = 144.dp)
                            .clickable {
                                onCategoryRankingCardClick()
                            }
                    )
                }
            }

            AnimatedVisibility(visible = spentByShopData.isNotEmpty()) {
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
                        items = spentByShopData,
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

@Preview(
    group = "DashboardScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "DashboardScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun DashboardScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DashboardScreen(
                onSettingsAction = {},
                onCategoryRankingCardClick = {},
                onShopRankingCardClick = {},
                totalSpentData = 16832.18F,
                spentByShopData = TransactionTotalSpentByShop.generateList(),
                spentByCategoryData = ItemSpentByCategory.generateList(),
                spentByTimeData = ItemSpentByTime.generateList(),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodUpdate = {},
            )
        }
    }
}
