package com.kssidll.arru.ui.screen.home

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.automirrored.outlined.Notes
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Immutable
enum class HomeDestinations(
    @param:StringRes val label: Int,
    val disabledIcon: ImageVector,
    val enabledIcon: ImageVector,
    @param:StringRes val contentDescription: Int,
) {
    DASHBOARD(
        R.string.dashboard_nav_label,
        Icons.Outlined.Home,
        Icons.Filled.Home,
        R.string.navigate_to_dashboard_description,
    ),
    ANALYSIS(
        R.string.analysis_nav_label,
        Icons.Outlined.Analytics,
        Icons.Filled.Analytics,
        R.string.navigate_to_analysis_description,
    ),
    TRANSACTIONS(
        R.string.transactions_nav_label,
        Icons.AutoMirrored.Outlined.Notes,
        Icons.AutoMirrored.Filled.Notes,
        R.string.navigate_to_transactions_description,
    );

    companion object {
        val DEFAULT = DASHBOARD

        fun get(index: Int?) = index?.let { entries.getOrElse(it) { DEFAULT } } ?: DEFAULT
    }
}

@Composable
fun HomeRoute(
    navigateSettings: () -> Unit,
    navigateSearch: () -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateAddTransaction: () -> Unit,
    navigateEditTransaction: (transactionId: Long) -> Unit,
    navigateAddItem: (transactionId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateCategoryRanking: () -> Unit,
    navigateShopRanking: () -> Unit,
    navigateCategorySpendingComparison: (year: Int, month: Int) -> Unit,
    navigateShopSpendingComparison: (year: Int, month: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    HomeScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                is HomeEvent.ChangeScreenDestination -> {
                    viewModel.handleEvent(event)
                }

                is HomeEvent.ChangeDashboardSpentByTimeChartPeriod -> {
                    viewModel.handleEvent(event)
                }

                is HomeEvent.IncrementCurrentAnalysisDate -> {
                    viewModel.handleEvent(event)
                }

                is HomeEvent.DecrementCurrentAnalysisDate -> {
                    viewModel.handleEvent(event)
                }

                is HomeEvent.NavigateSettings -> {
                    navigateSettings()
                }

                is HomeEvent.NavigateSearch -> {
                    navigateSearch()
                }

                is HomeEvent.NavigateDisplayProduct -> {
                    navigateDisplayProduct(event.productId)
                }

                is HomeEvent.NavigateDisplayProductCategory -> {
                    navigateDisplayProductCategory(event.categoryId)
                }

                is HomeEvent.NavigateDisplayProductProducer -> {
                    navigateDisplayProductProducer(event.producerId)
                }

                is HomeEvent.NavigateDisplayShop -> {
                    navigateDisplayShop(event.shopId)
                }

                is HomeEvent.NavigateAddItem -> {
                    navigateAddItem(event.transactionId)
                }

                is HomeEvent.NavigateEditItem -> {
                    navigateEditItem(event.itemId)
                }

                is HomeEvent.NavigateAddTransaction -> {
                    navigateAddTransaction()
                }

                is HomeEvent.NavigateEditTransaction -> {
                    navigateEditTransaction(event.transactionId)
                }

                is HomeEvent.NavigateCategoryRanking -> {
                    navigateCategoryRanking()
                }

                is HomeEvent.NavigateShopRanking -> {
                    navigateShopRanking()
                }

                is HomeEvent.NavigateCategorySpendingComparison -> {
                    navigateCategorySpendingComparison(event.year, event.month)
                }

                is HomeEvent.NavigateShopSpendingComparison -> {
                    navigateShopSpendingComparison(event.year, event.month)
                }
            }
        },
        modifier = modifier,
    )
}
