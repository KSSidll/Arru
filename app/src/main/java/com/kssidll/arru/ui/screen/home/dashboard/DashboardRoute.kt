package com.kssidll.arru.ui.screen.home.dashboard


import androidx.compose.runtime.*
import com.kssidll.arru.domain.data.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun DashboardRoute(
    isExpandedScreen: Boolean,
    navigateSettings: () -> Unit,
    navigateCategoryRanking: () -> Unit,
    navigateShopRanking: () -> Unit,
) {
    val viewModel: DashboardViewModel = hiltViewModel()

    DashboardScreen(
        isExpandedScreen = isExpandedScreen,
        onSettingsAction = navigateSettings,
        onCategoryRankingCardClick = navigateCategoryRanking,
        onShopRankingCardClick = navigateShopRanking,
        totalSpentData = viewModel.getTotalSpent()
            .collectAsState(initial = Data.Loading()).value,
        spentByShopData = viewModel.getSpentByShop()
            .collectAsState(initial = Data.Loading()).value,
        spentByCategoryData = viewModel.getSpentByCategory()
            .collectAsState(initial = Data.Loading()).value,
        spentByTimeData = viewModel.spentByTimeData.collectAsState(initial = Data.Loading()).value,
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodUpdate = {
            viewModel.switchToSpentByTimePeriod(it)
        },
    )
}
