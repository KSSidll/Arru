package com.kssidll.arru.ui.screen.spendingcomparison.shopspendingcomparison

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.spendingcomparison.SpendingComparisonScreen
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ShopSpendingComparisonRoute(
    navigateBack: () -> Unit,
    year: Int,
    month: Int,
    viewModel: ShopSpendingComparisonViewModel = hiltViewModel(),
) {
    LaunchedEffect(year) { viewModel.handleEvent(ShopSpendingComparisonEvent.SetYear(year)) }

    LaunchedEffect(month) { viewModel.handleEvent(ShopSpendingComparisonEvent.SetMonth(month)) }

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
