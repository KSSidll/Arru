package com.kssidll.arrugarq.ui.screen.home

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun HomeRoute(
    onAddItem: () -> Unit,
    onDashboardCategoryCardClick: () -> Unit,
    onDashboardShopCardClick: () -> Unit,
) {
    val homeViewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        onAddItem = onAddItem,
        onDashboardCategoryCardClick = onDashboardCategoryCardClick,
        onDashboardShopCardClick = onDashboardShopCardClick,
        totalSpentData = homeViewModel.getTotalSpent(),
        spentByShopData = homeViewModel.getSpentByShop(),
        spentByCategoryData = homeViewModel.getSpentByCategory(),
        spentByTimeData = homeViewModel.spentByTimeData,
        spentByTimePeriod = homeViewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            homeViewModel.switchToSpentByTimePeriod(it)
        }
    )
}