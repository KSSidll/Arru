package com.kssidll.arru.ui.screen.search

import android.os.Parcelable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.defaultNavigateContentTransformation
import com.kssidll.arru.defaultPopContentTransformation
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.screen.search.categorylist.CategoryListRoute
import com.kssidll.arru.ui.screen.search.producerlist.ProducerListRoute
import com.kssidll.arru.ui.screen.search.productlist.ProductListRoute
import com.kssidll.arru.ui.screen.search.shoplist.ShopListRoute
import com.kssidll.arru.ui.screen.search.start.StartRoute
import com.kssidll.arru.ui.theme.ArruTheme
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize

/**
 * @param onBack Called to request a back navigation
 * @param state [SearchScreenState] instance representing the screen state
 * @param onProductClick Callback called when product is clicked. Provides product id as parameter
 * @param onCategoryClick Callback called when category is clicked. Provides category id as
 *   parameter
 * @param onProducerClick Callback called when producer is clicked. Provides producer id as
 *   parameter
 * @param onShopClick Callback called when shop is clicked. Provides shop id as parameter
 * @param onProductLongClick Callback called when product is long clicked/pressed. Provides product
 *   id as parameter
 * @param onCategoryLongClick Callback called when category is long clicked/pressed. Provides
 *   category id as parameter
 * @param onProducerLongClick Callback called when producer is long clicked/pressed. Provides
 *   producer id as parameter
 * @param onShopLongClick Callback called when shop is long clicked/pressed. Provides shop id as
 *   parameter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    state: SearchScreenState,
    onProductClick: (productId: Long) -> Unit,
    onCategoryClick: (categoryId: Long) -> Unit,
    onProducerClick: (producerId: Long) -> Unit,
    onShopClick: (shopId: Long) -> Unit,
    onProductLongClick: (productId: Long) -> Unit,
    onCategoryLongClick: (categoryId: Long) -> Unit,
    onProducerLongClick: (producerId: Long) -> Unit,
    onShopLongClick: (shopId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    with(state) {
        NavBackHandler(controller = state.navController)

        val screenWidth = LocalConfiguration.current.screenWidthDp

        Scaffold(
            topBar = {
                SecondaryAppBar(
                    onBack = {
                        state.navController.apply {
                            if (backstack.entries.size > 1) pop() else onBack()
                        }
                    },
                    title = {},
                )
            },
            modifier = modifier.windowInsetsPadding(WindowInsets.navigationBars),
        ) {
            AnimatedNavHost(
                controller = navController,
                transitionSpec = { action, _, _ ->
                    if (action != NavAction.Pop) {
                        defaultNavigateContentTransformation(screenWidth)
                    } else {
                        defaultPopContentTransformation(screenWidth)
                    }
                },
            ) { screen ->
                when (screen) {
                    is SearchDestinations.Start -> {
                        StartRoute(
                            onProductClick = {
                                navController.navigate(SearchDestinations.ProductList)
                            },
                            onCategoryClick = {
                                navController.navigate(SearchDestinations.CategoryList)
                            },
                            onShopClick = { navController.navigate(SearchDestinations.ShopList) },
                            onProducerClick = {
                                navController.navigate(SearchDestinations.ProducerList)
                            },
                        )
                    }

                    is SearchDestinations.ProductList -> {
                        Box(modifier = Modifier.padding(it).consumeWindowInsets(it)) {
                            ProductListRoute(
                                onProductClick = { productId -> onProductClick(productId) },
                                onProductLongClick = { productId -> onProductLongClick(productId) },
                            )
                        }
                    }

                    is SearchDestinations.CategoryList -> {
                        Box(modifier = Modifier.padding(it).consumeWindowInsets(it)) {
                            CategoryListRoute(
                                onCategoryClick = { productCategoryId ->
                                    onCategoryClick(productCategoryId)
                                },
                                onCategoryLongClick = { productCategoryId ->
                                    onCategoryLongClick(productCategoryId)
                                },
                            )
                        }
                    }

                    is SearchDestinations.ProducerList -> {
                        Box(modifier = Modifier.padding(it).consumeWindowInsets(it)) {
                            ProducerListRoute(
                                onProducerClick = { productProducerId ->
                                    onProducerClick(productProducerId)
                                },
                                onProducerLongClick = { productProducerId ->
                                    onProducerLongClick(productProducerId)
                                },
                            )
                        }
                    }

                    is SearchDestinations.ShopList -> {
                        Box(modifier = Modifier.padding(it).consumeWindowInsets(it)) {
                            ShopListRoute(
                                onShopClick = { shopId -> onShopClick(shopId) },
                                onShopLongClick = { shopId -> onShopLongClick(shopId) },
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Possible internal navigation destinations for [SearchScreen] */
@Parcelize
sealed class SearchDestinations : Parcelable {
    data object Start : SearchDestinations()

    data object ProductList : SearchDestinations()

    data object CategoryList : SearchDestinations()

    data object ShopList : SearchDestinations()

    data object ProducerList : SearchDestinations()
}

/** Data representing [SearchScreen] state */
data class SearchScreenState(
    val navController: NavController<SearchDestinations> =
        navController(startDestination = SearchDestinations.Start)
)

@PreviewLightDark
@ExpandedPreviews
@Composable
private fun SearchScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SearchScreen(
                onBack = {},
                state = SearchScreenState(),
                onProductClick = {},
                onProductLongClick = {},
                onCategoryClick = {},
                onCategoryLongClick = {},
                onShopClick = {},
                onShopLongClick = {},
                onProducerClick = {},
                onProducerLongClick = {},
            )
        }
    }
}
