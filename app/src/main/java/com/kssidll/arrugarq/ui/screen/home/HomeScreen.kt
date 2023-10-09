package com.kssidll.arrugarq.ui.screen.home

import android.content.res.Configuration.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.screen.home.component.*
import com.kssidll.arrugarq.ui.screen.home.dashboard.*
import com.kssidll.arrugarq.ui.screen.home.predictions.*
import com.kssidll.arrugarq.ui.screen.home.transactions.*
import com.kssidll.arrugarq.ui.theme.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onAddItem: () -> Unit,
    onDashboardCategoryCardClick: () -> Unit,
    onDashboardShopCardClick: () -> Unit,
    items: Flow<List<FullItem>>,
    totalSpentData: Flow<Float>,
    spentByShopData: Flow<List<ItemSpentByShop>>,
    spentByCategoryData: Flow<List<ItemSpentByCategory>>,
    spentByTimeData: Flow<List<ItemSpentByTime>>,
    spentByTimePeriod: SpentByTimePeriod,
    onSpentByTimePeriodSwitch: (SpentByTimePeriod) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = HomeScreenLocations.entries.first { it.initial }.ordinal,
        initialPageOffsetFraction = 0F,
        pageCount = { HomeScreenLocations.entries.size },
    )

    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                currentLocation = HomeScreenLocations.getByOrdinal(pagerState.currentPage)!!,
                onLocationChange = {
                    scope.launch {
                        pagerState.animateScrollToPage(it.ordinal)
                    }
                },
                onAddItem = onAddItem,
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                beyondBoundsPageCount = HomeScreenLocations.entries.size,
            ) { location ->
                when (HomeScreenLocations.getByOrdinal(location)!!) {
                    HomeScreenLocations.Dashboard -> {
                        DashboardScreen(
                            onCategoryCardClick = onDashboardCategoryCardClick,
                            onShopCardClick = onDashboardShopCardClick,
                            totalSpentData = totalSpentData,
                            spentByShopData = spentByShopData,
                            spentByCategoryData = spentByCategoryData,
                            spentByTimeData = spentByTimeData,
                            spentByTimePeriod = spentByTimePeriod,
                            onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                        )
                    }

                    HomeScreenLocations.Predictions -> {
                        PredictionsScreen(

                        )
                    }

                    HomeScreenLocations.Transactions -> {
                        TransactionsScreen(
                            items = items,
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    group = "Home Screen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "Home Screen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun HomeScreenPreview() {
    ArrugarqTheme {
        ProvideChartStyle(
            chartStyle = m3ChartStyle(
                entityColors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                )
            )
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                HomeScreen(
                    onAddItem = {},
                    onDashboardCategoryCardClick = {},
                    onDashboardShopCardClick = {},
                    items = generateRandomFullItemListFlow(),
                    totalSpentData = flowOf(1357452F),
                    spentByShopData = generateRandomItemSpentByShopListFlow(),
                    spentByCategoryData = generateRandomItemSpentByCategoryListFlow(),
                    spentByTimeData = generateRandomItemSpentByTimeListFlow(),
                    spentByTimePeriod = SpentByTimePeriod.Month,
                    onSpentByTimePeriodSwitch = {},
                )
            }
        }
    }
}