package com.kssidll.arrugarq.ui.screen.home.analysis


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun AnalysisRoute(
    navigateCategorySpendingComparison: (year: Int, month: Int) -> Unit,
    navigateShopSpendingComparison: (year: Int, month: Int) -> Unit,
) {
    val viewModel: AnalysisViewModel = hiltViewModel()

    AnalysisScreen(
        year = viewModel.year,
        month = viewModel.month,
        onMonthDecrement = {
            viewModel.monthDecrement()
        },
        onMonthIncrement = {
            viewModel.monthIncrement()
        },
        setCategorySpending = viewModel.setCategorySpending,
        compareCategorySpending = viewModel.compareCategorySpending,
        setShopSpending = viewModel.setShopSpending,
        compareShopSpending = viewModel.compareShopSpending,
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
