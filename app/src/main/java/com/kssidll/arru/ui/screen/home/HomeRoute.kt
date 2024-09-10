package com.kssidll.arru.ui.screen.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Notes
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.home.analysis.AnalysisRoute
import com.kssidll.arru.ui.screen.home.component.HomeBottomNavBar
import com.kssidll.arru.ui.screen.home.component.HomeRailNavBar
import com.kssidll.arru.ui.screen.home.dashboard.DashboardRoute
import com.kssidll.arru.ui.screen.home.transactions.TransactionsRoute
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeRoute(
    isExpandedScreen: Boolean,
    navigateSettings: () -> Unit,
    navigateSearch: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateItemAdd: (transactionId: Long) -> Unit,
    navigateTransactionAdd: () -> Unit,
    navigateTransactionEdit: (transactionId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateCategoryRanking: () -> Unit,
    navigateShopRanking: () -> Unit,
    navigateCategorySpendingComparison: (year: Int, month: Int) -> Unit,
    navigateShopSpendingComparison: (year: Int, month: Int) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = HomeRouteLocations.entries.first { it.initial }.ordinal,
        initialPageOffsetFraction = 0F,
        pageCount = { HomeRouteLocations.entries.size },
    )

    if (isExpandedScreen) {
        ExpandedHomeRouteNavigation(
            currentLocation = HomeRouteLocations.getByOrdinal(pagerState.currentPage)!!,
            onLocationChange = {
                scope.launch {
                    pagerState.animateScrollToPage(it.ordinal)
                }
            },
            navigateSettings = navigateSettings,
            navigateTransactionAdd = navigateTransactionAdd,
        ) {
            HomeRouteContent(
                isExpandedScreen = true,
                pagerState = pagerState,
                navigateSettings = navigateSettings,
                navigateSearch = navigateSearch,
                navigateProduct = navigateProduct,
                navigateCategory = navigateCategory,
                navigateProducer = navigateProducer,
                navigateShop = navigateShop,
                navigateItemAdd = navigateItemAdd,
                navigateTransactionEdit = navigateTransactionEdit,
                navigateItemEdit = navigateItemEdit,
                navigateCategoryRanking = navigateCategoryRanking,
                navigateShopRanking = navigateShopRanking,
                navigateCategorySpendingComparison = navigateCategorySpendingComparison,
                navigateShopSpendingComparison = navigateShopSpendingComparison,
            )
        }
    } else {
        HomeRouteNavigation(
            currentLocation = HomeRouteLocations.getByOrdinal(pagerState.currentPage)!!,
            onLocationChange = {
                scope.launch {
                    pagerState.animateScrollToPage(it.ordinal)
                }
            },
            navigateTransactionAdd = navigateTransactionAdd
        ) {
            HomeRouteContent(
                isExpandedScreen = false,
                pagerState = pagerState,
                navigateSettings = navigateSettings,
                navigateSearch = navigateSearch,
                navigateProduct = navigateProduct,
                navigateCategory = navigateCategory,
                navigateProducer = navigateProducer,
                navigateShop = navigateShop,
                navigateItemAdd = navigateItemAdd,
                navigateTransactionEdit = navigateTransactionEdit,
                navigateItemEdit = navigateItemEdit,
                navigateCategoryRanking = navigateCategoryRanking,
                navigateShopRanking = navigateShopRanking,
                navigateCategorySpendingComparison = navigateCategorySpendingComparison,
                navigateShopSpendingComparison = navigateShopSpendingComparison,
            )
        }
    }
}

@Composable
fun ExpandedHomeRouteNavigation(
    currentLocation: HomeRouteLocations = HomeRouteLocations.entries.first { it.initial },
    onLocationChange: (HomeRouteLocations) -> Unit,
    navigateSettings: () -> Unit,
    navigateTransactionAdd: () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = Modifier.windowInsetsPadding(
            WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
        )
    ) {
        HomeRailNavBar(
            currentLocation = currentLocation,
            onLocationChange = onLocationChange,
            onActionButtonClick = navigateTransactionAdd,
            onSettingsAction = navigateSettings,
        )

        content()
    }
}

@Composable
fun HomeRouteNavigation(
    currentLocation: HomeRouteLocations = HomeRouteLocations.entries.first { it.initial },
    onLocationChange: (HomeRouteLocations) -> Unit,
    navigateTransactionAdd: () -> Unit,
    content: @Composable () -> Unit,
) {
    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                currentLocation = currentLocation,
                onLocationChange = onLocationChange,
                onActionButtonClick = navigateTransactionAdd,
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal)
    ) {
        Box(
            modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it)
        ) {
            content()
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeRouteContent(
    isExpandedScreen: Boolean,
    pagerState: PagerState,
    navigateSettings: () -> Unit,
    navigateSearch: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateItemAdd: (transactionId: Long) -> Unit,
    navigateTransactionEdit: (transactionId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateCategoryRanking: () -> Unit,
    navigateShopRanking: () -> Unit,
    navigateCategorySpendingComparison: (year: Int, month: Int) -> Unit,
    navigateShopSpendingComparison: (year: Int, month: Int) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        userScrollEnabled = false,
        beyondViewportPageCount = HomeRouteLocations.entries.size,
    ) { location ->
        when (HomeRouteLocations.getByOrdinal(location)!!) {
            HomeRouteLocations.Dashboard -> {
                DashboardRoute(
                    isExpandedScreen = isExpandedScreen,
                    navigateSettings = navigateSettings,
                    navigateCategoryRanking = navigateCategoryRanking,
                    navigateShopRanking = navigateShopRanking,
                )
            }

            HomeRouteLocations.Analysis -> {
                AnalysisRoute(
                    isExpandedScreen = isExpandedScreen,
                    navigateCategorySpendingComparison = navigateCategorySpendingComparison,
                    navigateShopSpendingComparison = navigateShopSpendingComparison,
                )
            }

            HomeRouteLocations.Transactions -> {
                TransactionsRoute(
                    isExpandedScreen = isExpandedScreen,
                    navigateSearch = navigateSearch,
                    navigateProduct = navigateProduct,
                    navigateCategory = navigateCategory,
                    navigateProducer = navigateProducer,
                    navigateShop = navigateShop,
                    navigateItemAdd = navigateItemAdd,
                    navigateItemEdit = navigateItemEdit,
                    navigateTransactionEdit = navigateTransactionEdit,
                )
            }
        }
    }
}

enum class HomeRouteLocations(
    val initial: Boolean = false,
) {
    Dashboard(initial = true),
    Analysis,
    Transactions,
    ;

    val description: String
        @Composable
        @ReadOnlyComposable
        get() = when (this) {
            Dashboard -> stringResource(R.string.navigate_to_dashboard_description)
            Analysis -> stringResource(R.string.navigate_to_analysis_description)
            Transactions -> stringResource(R.string.navigate_to_transactions_description)
        }

    val imageVector: ImageVector
        @Composable
        get() = when (this) {
            Dashboard -> Icons.Rounded.Home
            Analysis -> Icons.Rounded.Analytics
            Transactions -> Icons.AutoMirrored.Rounded.Notes
        }

    companion object {
        private val idMap = entries.associateBy { it.ordinal }
        fun getByOrdinal(ordinal: Int) = idMap[ordinal]

    }
}

@Composable
@ReadOnlyComposable
internal fun HomeRouteLocations.getTranslation(): String {
    return when (this) {
        HomeRouteLocations.Dashboard -> stringResource(R.string.dashboard_nav_label)
        HomeRouteLocations.Analysis -> stringResource(R.string.analysis_nav_label)
        HomeRouteLocations.Transactions -> stringResource(R.string.transactions_nav_label)
    }
}
