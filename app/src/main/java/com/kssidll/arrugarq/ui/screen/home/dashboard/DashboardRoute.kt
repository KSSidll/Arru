package com.kssidll.arrugarq.ui.screen.home.dashboard


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun DashboardRoute(
    navigateSettings: () -> Unit,
    navigateCategoryRanking: () -> Unit,
    navigateShopRanking: () -> Unit,
) {
    val viewModel: DashboardViewModel = hiltViewModel()

    DashboardScreen(
        onSettingsAction = navigateSettings,
        onCategoryRankingCardClick = navigateCategoryRanking,
        onShopRankingCardClick = navigateShopRanking,
        totalSpentData = viewModel.getTotalSpent()
            .collectAsState(initial = 0F).value,
        spentByShopData = viewModel.getSpentByShop()
            .collectAsState(initial = emptyList()).value,
        spentByCategoryData = viewModel.getSpentByCategory()
            .collectAsState(initial = emptyList()).value,
        spentByTimeData = viewModel.spentByTimeData.collectAsState(initial = emptyList()).value,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodUpdate = {
            viewModel.switchToSpentByTimePeriod(it)
        },
    )
}
