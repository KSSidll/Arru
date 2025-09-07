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
import com.kssidll.arru.ui.screen.home.analysis.AnalysisEvent
import com.kssidll.arru.ui.screen.home.analysis.AnalysisViewModel
import com.kssidll.arru.ui.screen.home.dashboard.DashboardEvent
import com.kssidll.arru.ui.screen.home.dashboard.DashboardViewModel
import com.kssidll.arru.ui.screen.home.transactions.TransactionsEvent
import com.kssidll.arru.ui.screen.home.transactions.TransactionsViewModel
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
    navigateEditProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateEditProductProducer: (producerId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditShop: (shopId: Long) -> Unit,
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
    dashboardViewModel: DashboardViewModel = hiltViewModel(),
    analysisViewModel: AnalysisViewModel = hiltViewModel(),
    transactionsViewModel: TransactionsViewModel = hiltViewModel(),
) {
    HomeScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        dashboardUiState = dashboardViewModel.uiState.collectAsStateWithLifecycle().value,
        analysisUiState = analysisViewModel.uiState.collectAsStateWithLifecycle().value,
        transactionsUiState = transactionsViewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                is HomeEvent.ChangeScreenDestination -> viewModel.handleEvent(event)

                is HomeEvent.NavigateAddTransaction -> navigateAddTransaction()
            }
        },
        dashboardOnEvent = { event ->
            when (event) {
                is DashboardEvent.ChangeSpentByTimePeriod -> dashboardViewModel.handleEvent(event)
                is DashboardEvent.NavigateCategoryRanking -> navigateCategoryRanking()
                is DashboardEvent.NavigateSettings -> navigateSettings()
                is DashboardEvent.NavigateShopRanking -> navigateShopRanking()
            }
        },
        analysisOnEvent = { event ->
            when (event) {
                is AnalysisEvent.DecrementCurrentDate -> analysisViewModel.handleEvent(event)
                is AnalysisEvent.IncrementCurrentDate -> analysisViewModel.handleEvent(event)
                is AnalysisEvent.NavigateCategorySpendingComparison ->
                    navigateCategorySpendingComparison(event.year, event.month)
                is AnalysisEvent.NavigateShopSpendingComparison ->
                    navigateShopSpendingComparison(event.year, event.month)
            }
        },
        transactionsOnEvent = { event ->
            when (event) {
                is TransactionsEvent.NavigateAddItem -> navigateAddItem(event.transactionId)
                is TransactionsEvent.NavigateDisplayProduct ->
                    navigateDisplayProduct(event.productId)
                is TransactionsEvent.NavigateDisplayProductCategory ->
                    navigateDisplayProductCategory(event.productCategoryId)
                is TransactionsEvent.NavigateEditProductCategory ->
                    navigateEditProductCategory(event.productCategoryId)
                is TransactionsEvent.NavigateDisplayProductProducer ->
                    navigateDisplayProductProducer(event.productProducerId)
                is TransactionsEvent.NavigateEditProductProducer ->
                    navigateEditProductProducer(event.productProducerId)
                is TransactionsEvent.NavigateDisplayShop -> navigateDisplayShop(event.shopId)
                is TransactionsEvent.NavigateEditShop -> navigateEditShop(event.shopId)
                is TransactionsEvent.NavigateEditItem -> navigateEditItem(event.itemId)
                is TransactionsEvent.NavigateEditTransaction ->
                    navigateEditTransaction(event.transactionId)
                is TransactionsEvent.NavigateSearch -> navigateSearch()
                is TransactionsEvent.ToggleTransactionItemVisibility ->
                    transactionsViewModel.handleEvent(event)
            }
        },
        modifier = modifier,
    )
}
