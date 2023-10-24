package com.kssidll.arrugarq.ui.screen.home

import android.content.res.Configuration.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.ui.screen.home.component.*
import com.kssidll.arrugarq.ui.screen.home.dashboard.*
import com.kssidll.arrugarq.ui.screen.home.search.*
import com.kssidll.arrugarq.ui.screen.home.transactions.*
import com.kssidll.arrugarq.ui.theme.*
import com.patrykandpatrick.vico.compose.m3.style.*
import com.patrykandpatrick.vico.compose.style.*
import kotlinx.coroutines.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HomeScreen(
    onAddItem: () -> Unit,
    onDashboardCategoryCardClick: () -> Unit,
    onDashboardShopCardClick: () -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onProductEdit: (productId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onCategoryEdit: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onProducerEdit: (producerId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = HomeScreenLocations.entries.first { it.initial }.ordinal,
        initialPageOffsetFraction = 0F,
        pageCount = { HomeScreenLocations.entries.size },
    )

    Scaffold(
        bottomBar = {
            HomeBottomNavBar(
                currentLocation = HomeScreenLocations.getByOrdinal(pagerState.currentPage)!!,
                onLocationChange = {
                    scope.launch {
                        pagerState.animateScrollToPage(it.ordinal)
                    }
                },
                onAddItem = onAddItem,
            )
        }
    ) {
        Box(modifier = Modifier.padding(it)) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = false,
                beyondBoundsPageCount = HomeScreenLocations.entries.size,
            ) { location ->
                when (HomeScreenLocations.getByOrdinal(location)!!) {
                    HomeScreenLocations.Dashboard -> {
                        DashboardRoute(
                            onCategoryCardClick = onDashboardCategoryCardClick,
                            onShopCardClick = onDashboardShopCardClick,
                        )
                    }

                    HomeScreenLocations.Search -> {
                        SearchRoute(
                            navBackHandlerEnabled = pagerState.currentPage == location,
                            onProductSelect = onProductSelect,
                            onProductEdit = onProductEdit,
                            onShopSelect = onShopSelect,
                            onShopEdit = onShopEdit,
                            onCategorySelect = onCategorySelect,
                            onCategoryEdit = onCategoryEdit,
                            onProducerSelect = onProducerSelect,
                            onProducerEdit = onProducerEdit,
                        )
                    }

                    HomeScreenLocations.Transactions -> {
                        TransactionsRoute(
                            onItemEdit = onItemEdit,
                            onProductSelect = onProductSelect,
                            onCategorySelect = onCategorySelect,
                            onProducerSelect = onProducerSelect,
                            onShopSelect = onShopSelect,
                        )
                    }
                }
            }
        }
    }
}

@Preview(
    group = "Home Screen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "Home Screen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun HomeScreenPreview() {
    ArrugarqTheme {
        ProvideChartStyle(
            chartStyle = m3ChartStyle(
                entityColors = listOf(
                    MaterialTheme.colorScheme.tertiary,
                )
            )
        ) {
            Surface(modifier = Modifier.fillMaxSize()) {
                HomeScreen(
                    onAddItem = {},
                    onDashboardCategoryCardClick = {},
                    onDashboardShopCardClick = {},
                    onItemEdit = {},
                    onProductSelect = {},
                    onProductEdit = {},
                    onCategorySelect = {},
                    onCategoryEdit = {},
                    onShopSelect = {},
                    onShopEdit = {},
                    onProducerSelect = {},
                    onProducerEdit = {},
                )
            }
        }
    }
}