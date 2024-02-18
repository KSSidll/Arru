package com.kssidll.arru.ui.screen.search


import android.os.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arru.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.screen.search.categorylist.*
import com.kssidll.arru.ui.screen.search.producerlist.*
import com.kssidll.arru.ui.screen.search.productlist.*
import com.kssidll.arru.ui.screen.search.shoplist.*
import com.kssidll.arru.ui.screen.search.start.*
import com.kssidll.arru.ui.theme.*
import dev.olshevski.navigation.reimagined.*
import kotlinx.parcelize.*

/**
 * @param onBack Called to request a back navigation
 * @param state [SearchScreenState] instance representing the screen state
 * @param onProductClick Callback called when product is clicked. Provides product id as parameter
 * @param onCategoryClick Callback called when category is clicked. Provides category id as parameter
 * @param onProducerClick Callback called when producer is clicked. Provides producer id as parameter
 * @param onShopClick Callback called when shop is clicked. Provides shop id as parameter
 * @param onProductLongClick Callback called when product is long clicked/pressed. Provides product id as parameter
 * @param onCategoryLongClick Callback called when category is long clicked/pressed. Provides category id as parameter
 * @param onProducerLongClick Callback called when producer is long clicked/pressed. Provides producer id as parameter
 * @param onShopLongClick Callback called when shop is long clicked/pressed. Provides shop id as parameter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
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
) {
    with(state) {
        NavBackHandler(controller = state.navController)

        val screenWidth = LocalConfiguration.current.screenWidthDp

        Scaffold(
            topBar = {
                SecondaryAppBar(
                    onBack = {
                        state.navController.apply {
                            if (backstack.entries.size > 1) pop()
                            else onBack()
                        }
                    },
                    title = {},
                )
            },
        ) {
            AnimatedNavHost(
                controller = navController,
                transitionSpec = { action, _, _ ->
                    if (action != NavAction.Pop) {
                        defaultNavigateContentTransformation(screenWidth)
                    } else {
                        defaultPopContentTransformation(screenWidth)
                    }
                }
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
                            onShopClick = {
                                navController.navigate(SearchDestinations.ShopList)
                            },
                            onProducerClick = {
                                navController.navigate(SearchDestinations.ProducerList)
                            }
                        )
                    }

                    is SearchDestinations.ProductList -> {
                        Box(modifier = Modifier.padding(it)) {
                            ProductListRoute(
                                onProductClick = {
                                    onProductClick(it)
                                },
                                onProductLongClick = {
                                    onProductLongClick(it)
                                }
                            )
                        }
                    }

                    is SearchDestinations.CategoryList -> {
                        Box(modifier = Modifier.padding(it)) {
                            CategoryListRoute(
                                onCategoryClick = {
                                    onCategoryClick(it)
                                },
                                onCategoryLongClick = {
                                    onCategoryLongClick(it)
                                }
                            )
                        }
                    }

                    is SearchDestinations.ProducerList -> {
                        Box(modifier = Modifier.padding(it)) {
                            ProducerListRoute(
                                onProducerClick = {
                                    onProducerClick(it)
                                },
                                onProducerLongClick = {
                                    onProducerLongClick(it)
                                }
                            )
                        }
                    }

                    is SearchDestinations.ShopList -> {
                        Box(modifier = Modifier.padding(it)) {
                            ShopListRoute(
                                onShopClick = {
                                    onShopClick(it)
                                },
                                onShopLongClick = {
                                    onShopLongClick(it)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Possible internal navigation destinations for [SearchScreen]
 */
@Parcelize
internal sealed class SearchDestinations: Parcelable {
    data object Start: SearchDestinations()
    data object ProductList: SearchDestinations()
    data object CategoryList: SearchDestinations()
    data object ShopList: SearchDestinations()
    data object ProducerList: SearchDestinations()
}

/**
 * Data representing [SearchScreen] state
 */
internal data class SearchScreenState(
    val navController: NavController<SearchDestinations> = navController(startDestination = SearchDestinations.Start)
)

@PreviewLightDark
@PreviewExpanded
@Composable
fun SearchScreenPreview() {
    ArrugarqTheme {
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
