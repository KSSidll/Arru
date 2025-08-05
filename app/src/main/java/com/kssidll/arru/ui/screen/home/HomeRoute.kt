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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

enum class HomeDestinations(
    @param:StringRes val label: Int,
    val disabledIcon: ImageVector,
    val enabledIcon: ImageVector,
    @param:StringRes val contentDescription: Int
) {
    DASHBOARD(
        R.string.dashboard_nav_label,
        Icons.Outlined.Home,
        Icons.Filled.Home,
        R.string.navigate_to_dashboard_description
    ),
    ANALYSIS(
        R.string.analysis_nav_label,
        Icons.Outlined.Analytics,
        Icons.Filled.Analytics,
        R.string.navigate_to_analysis_description
    ),
    TRANSACTIONS(
        R.string.transactions_nav_label,
        Icons.AutoMirrored.Outlined.Notes,
        Icons.AutoMirrored.Filled.Notes,
        R.string.navigate_to_transactions_description
    )

    ;

    companion object {
        val DEFAULT = DASHBOARD
        fun get(index: Int?) = index?.let { entries.getOrElse(it) { DEFAULT } } ?: DEFAULT
    }
}

@Composable
fun HomeRoute(
    navigateSettings: () -> Unit,
    navigateSearch: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateTransactionAdd: () -> Unit,
    navigateTransactionEdit: (transactionId: Long) -> Unit,
    navigateItemAdd: (transactionId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateCategoryRanking: () -> Unit,
    navigateShopRanking: () -> Unit,
    navigateCategorySpendingComparison: (year: Int, month: Int) -> Unit,
    navigateShopSpendingComparison: (year: Int, month: Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    HomeScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED).value,
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

                is HomeEvent.NavigateProduct -> {
                    navigateProduct(event.productId)
                }

                is HomeEvent.NavigateCategory -> {
                    navigateCategory(event.categoryId)
                }

                is HomeEvent.NavigateProducer -> {
                    navigateProducer(event.producerId)
                }

                is HomeEvent.NavigateShop -> {
                    navigateShop(event.shopId)
                }

                is HomeEvent.NavigateItemAdd -> {
                    navigateItemAdd(event.transactionId)
                }

                is HomeEvent.NavigateItemEdit -> {
                    navigateItemEdit(event.itemId)
                }

                is HomeEvent.NavigateTransactionAdd -> {
                    navigateTransactionAdd()
                }

                is HomeEvent.NavigateTransactionEdit -> {
                    navigateTransactionEdit(event.transactionId)
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
        modifier = modifier
    )
}
