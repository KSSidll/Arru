package com.kssidll.arrugarq.ui.screen.home.dashboard


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun DashboardRoute(
    onCategoryCardClick: () -> Unit,
    onShopCardClick: () -> Unit,
) {
    val viewModel: DashboardViewModel = hiltViewModel()

    DashboardScreen(
        onCategoryCardClick = onCategoryCardClick,
        onShopCardClick = onShopCardClick,
        totalSpentData = viewModel.getTotalSpent(),
        spentByShopData = viewModel.getSpentByShop(),
        spentByCategoryData = viewModel.getSpentByCategory(),
        spentByTimeData = viewModel.spentByTimeData,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchToSpentByTimePeriod(it)
        },
    )
}
