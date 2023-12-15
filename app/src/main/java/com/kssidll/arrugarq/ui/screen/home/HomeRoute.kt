package com.kssidll.arrugarq.ui.screen.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.automirrored.rounded.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.vector.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.home.analysis.*
import com.kssidll.arrugarq.ui.screen.home.component.*
import com.kssidll.arrugarq.ui.screen.home.dashboard.*
import com.kssidll.arrugarq.ui.screen.home.transactions.*
import kotlinx.coroutines.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeRoute(
    navigateSettings: () -> Unit,
    navigateSearch: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateItemAdd: () -> Unit,
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

    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                currentLocation = HomeRouteLocations.getByOrdinal(pagerState.currentPage)!!,
                onLocationChange = {
                    scope.launch {
                        pagerState.animateScrollToPage(it.ordinal)
                    }
                },
                onActionButtonClick = navigateItemAdd,
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                beyondBoundsPageCount = HomeRouteLocations.entries.size,
            ) { location ->
                when (HomeRouteLocations.getByOrdinal(location)!!) {
                    HomeRouteLocations.Dashboard -> {
                        DashboardRoute(
                            navigateSettings = navigateSettings,
                            navigateCategoryRanking = navigateCategoryRanking,
                            navigateShopRanking = navigateShopRanking,
                        )
                    }

                    HomeRouteLocations.Analysis -> {
                        AnalysisRoute(
                            navigateCategorySpendingComparison = navigateCategorySpendingComparison,
                            navigateShopSpendingComparison = navigateShopSpendingComparison,
                        )
                    }

                    HomeRouteLocations.Transactions -> {
                        TransactionsRoute(
                            navigateSearch = navigateSearch,
                            navigateProduct = navigateProduct,
                            navigateCategory = navigateCategory,
                            navigateProducer = navigateProducer,
                            navigateShop = navigateShop,
                            navigateItemEdit = navigateItemEdit,
                        )
                    }
                }
            }
        }
    }
}

internal enum class HomeRouteLocations(
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
