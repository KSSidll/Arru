package com.kssidll.arrugarq.ui.screen.home.dashboard


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
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.component.*
import com.kssidll.arrugarq.ui.component.list.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.flow.*

/**
 * @param navigateSettings Callback called as request to navigate to settings
 * @param onCategoryCardClick Callback to call wen [ProductCategory] card is clicked
 * @param onShopCardClick Callback to call when [Shop] card is clicked
 * @param totalSpentData Number representing total [Item] spending as flow
 * @param spentByShopData List of items representing [Shop] spending in time as flow
 * @param spentByCategoryData List of items representing [ProductCategory] spending in time as flow
 * @param spentByTimeData List of items representing [Item] spending in time as flow
 * @param spentByTimePeriod Current [totalSpentData] time period
 * @param onSpentByTimePeriodSwitch Callback called as a request to switch the [totalSpentData] time period, Provides new time period as parameter
 */
@Composable
internal fun DashboardScreen(
    navigateSettings: () -> Unit,
    onCategoryCardClick: () -> Unit,
    onShopCardClick: () -> Unit,
    totalSpentData: Flow<Float>,
    spentByShopData: Flow<List<ItemSpentByShop>>,
    spentByCategoryData: Flow<List<ItemSpentByCategory>>,
    spentByTimeData: Flow<List<ItemSpentByTime>>,
    spentByTimePeriod: TimePeriodFlowHandler.Periods,
    onSpentByTimePeriodSwitch: (newPeriod: TimePeriodFlowHandler.Periods) -> Unit,
) {
    DashboardScreenContent(
        navigateSettings = navigateSettings,
        onCategoryCardClick = onCategoryCardClick,
        onShopCardClick = onShopCardClick,
        totalSpentData = totalSpentData.collectAsState(0F).value,
        spentByShopData = spentByShopData.collectAsState(emptyList()).value,
        spentByCategoryData = spentByCategoryData.collectAsState(emptyList()).value,
        spentByTimeData = spentByTimeData.collectAsState(emptyList()).value,
        spentByTimePeriod = spentByTimePeriod,
        onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
    )
}

private val TileOuterPadding: Dp = 8.dp
private val TileInnerPadding: Dp = 12.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenContent(
    navigateSettings: () -> Unit,
    onCategoryCardClick: () -> Unit,
    onShopCardClick: () -> Unit,
    totalSpentData: Float,
    spentByShopData: List<ItemSpentByShop>,
    spentByCategoryData: List<ItemSpentByCategory>,
    spentByTimeData: List<ItemSpentByTime>,
    spentByTimePeriod: TimePeriodFlowHandler.Periods,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
) {
    val scrollState = rememberScrollState()

    val scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val nestedScrollConnection = scrollBehavior.nestedScrollConnection

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    IconButton(
                        onClick = navigateSettings,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "navigate settings",
                        )
                    }
                },
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

            SpendingSummaryComponent(
                modifier = Modifier.animateContentSize(),
                spentByTimeData = spentByTimeData,
                spentByTimePeriod = spentByTimePeriod,
                onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
            )

            Spacer(Modifier.height(4.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .padding(TileOuterPadding)
            ) {
                RankingList(
                    innerItemPadding = PaddingValues(TileInnerPadding),
                    items = spentByCategoryData,
                    modifier = Modifier
                        .clickable {
                            onCategoryCardClick()
                        }
                )
            }

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                modifier = Modifier
                    .padding(TileOuterPadding)
            ) {
                RankingList(
                    innerItemPadding = PaddingValues(TileInnerPadding),
                    items = spentByShopData,
                    modifier = Modifier
                        .clickable {
                            onShopCardClick()
                        }
                )
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
            DashboardScreenContent(
                navigateSettings = {},
                onCategoryCardClick = {},
                onShopCardClick = {},
                totalSpentData = 16832.18F,
                spentByShopData = generateRandomItemSpentByShopList(),
                spentByCategoryData = generateRandomItemSpentByCategoryList(),
                spentByTimeData = generateRandomItemSpentByTimeList(),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodSwitch = {},
            )
        }
    }
}
