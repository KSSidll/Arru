package com.kssidll.arru.ui.screen.home.analysis


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun AnalysisRoute(
    isExpandedScreen: Boolean,
    navigateCategorySpendingComparison: (year: Int, month: Int) -> Unit,
    navigateShopSpendingComparison: (year: Int, month: Int) -> Unit,
) {
    val viewModel: AnalysisViewModel = hiltViewModel()

    AnalysisScreen(
        isExpandedScreen = isExpandedScreen,
        year = viewModel.year,
        month = viewModel.month,
        onMonthDecrement = {
            viewModel.monthDecrement()
        },
        onMonthIncrement = {
            viewModel.monthIncrement()
        },
        setCategorySpending = viewModel.setCategorySpending.collectAsState(initial = emptyList()).value,
        compareCategorySpending = viewModel.compareCategorySpending.collectAsState(initial = emptyList()).value,
        setShopSpending = viewModel.setShopSpending.collectAsState(initial = emptyList()).value,
        compareShopSpending = viewModel.compareShopSpending.collectAsState(initial = emptyList()).value,
        onCategorySpendingComparisonCardClick = {
            navigateCategorySpendingComparison(
                viewModel.year,
                viewModel.month
            )
        },
        onShopSpendingComparisonCardClick = {
            navigateShopSpendingComparison(
                viewModel.year,
                viewModel.month
            )
        },
    )
}
