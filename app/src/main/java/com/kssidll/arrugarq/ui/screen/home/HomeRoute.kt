package com.kssidll.arrugarq.ui.screen.home

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun HomeRoute(
    onAddItem: () -> Unit
) {
    val homeViewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        onAddItem = onAddItem,
        totalSpentData = homeViewModel.getTotalSpent(),
        spentByShopData = homeViewModel.getSpentByShop(),
        spentByTimeData = homeViewModel.spentByTimeData,
        spentByTimePeriod = homeViewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            homeViewModel.switchToSpentByTimePeriod(it)
        }
    )
}