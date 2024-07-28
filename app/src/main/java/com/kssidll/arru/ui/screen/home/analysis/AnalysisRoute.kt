package com.kssidll.arru.ui.screen.home.analysis


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.kssidll.arru.domain.data.Data
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

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
        setCategorySpending = viewModel.setCategorySpending.collectAsState(initial = Data.Loading()).value,
        compareCategorySpending = viewModel.compareCategorySpending.collectAsState(initial = Data.Loading()).value,
        setShopSpending = viewModel.setShopSpending.collectAsState(initial = Data.Loading()).value,
        compareShopSpending = viewModel.compareShopSpending.collectAsState(initial = Data.Loading()).value,
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
