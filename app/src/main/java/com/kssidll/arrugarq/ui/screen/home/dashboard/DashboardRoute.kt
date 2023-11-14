package com.kssidll.arrugarq.ui.screen.home.dashboard


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun DashboardRoute(
    navigateSettings: () -> Unit,
    onCategoryCardClick: () -> Unit,
    onShopCardClick: () -> Unit,
) {
    val viewModel: DashboardViewModel = hiltViewModel()

    DashboardScreen(
        navigateSettings = navigateSettings,
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
