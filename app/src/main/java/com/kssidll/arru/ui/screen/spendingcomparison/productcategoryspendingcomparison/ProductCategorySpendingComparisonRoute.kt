package com.kssidll.arru.ui.screen.spendingcomparison.productcategoryspendingcomparison

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.spendingcomparison.SpendingComparisonScreen
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ProductCategorySpendingComparisonRoute(
    navigateBack: () -> Unit,
    year: Int,
    month: Int,
    viewModel: ProductCategorySpendingComparisonViewModel = hiltViewModel(),
) {
    LaunchedEffect(year) {
        viewModel.handleEvent(ProductCategorySpendingComparisonEvent.SetYear(year))
    }

    LaunchedEffect(month) {
        viewModel.handleEvent(ProductCategorySpendingComparisonEvent.SetMonth(month))
    }

    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    SpendingComparisonScreen(
        onBack = navigateBack,
        title = uiState.title,
        leftSideItems = uiState.previousSpent,
        leftSideHeader = stringResource(id = R.string.previous),
        rightSideItems = uiState.currentSpent,
        rightSideHeader = stringResource(id = R.string.current),
    )
}
