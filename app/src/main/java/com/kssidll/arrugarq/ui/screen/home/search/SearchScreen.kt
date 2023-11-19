package com.kssidll.arrugarq.ui.screen.home.search


import android.content.res.Configuration.*
import android.os.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.screen.home.search.categorylist.*
import com.kssidll.arrugarq.ui.screen.home.search.producerlist.*
import com.kssidll.arrugarq.ui.screen.home.search.productlist.*
import com.kssidll.arrugarq.ui.screen.home.search.shoplist.*
import com.kssidll.arrugarq.ui.screen.home.search.start.*
import com.kssidll.arrugarq.ui.theme.*
import dev.olshevski.navigation.reimagined.*
import kotlinx.parcelize.*

/**
 * @param state [SearchScreenState] instance representing the screen state
 * @param navBackHandlerEnabled Whether the internal nav back handler for this screen is enabled
 * @param onProductSelect Callback called as request to navigate to product, Provides product id as parameter
 * @param onProductEdit Callback called as request to navigate to product edition, Provides product id as parameter
 * @param onShopSelect Callback called as request to navigate to shop, Provides shop id as parameter
 * @param onShopEdit Callback called as request to navigate to shop edition, Provides shop id as parameter
 * @param onCategorySelect Callback called as request to navigate to category, Provides category id as parameter
 * @param onCategoryEdit Callback called as request to navigate to category edition, Provides category id as parameter
 * @param onProducerSelect Callback called as request to navigate to producer, Provides producer id as parameter
 * @param onProducerEdit Callback called as request to navigate to producer edition, Provides producer id as parameter
 */
@Composable
internal fun SearchScreen(
    state: SearchScreenState,
    navBackHandlerEnabled: Boolean,
    onProductSelect: (productId: Long) -> Unit,
    onProductEdit: (productId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onCategoryEdit: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onProducerEdit: (producerId: Long) -> Unit,
) {
    SearchScreenContent(
        state = state,
        navBackHandlerEnabled = navBackHandlerEnabled,
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
 * [SearchScreen] content
 * @param state [SearchScreenState] instance representing the screen state
 * @param navBackHandlerEnabled Whether the internal nav back handler for this screen is enabled
 * @param onProductSelect Callback called as request to navigate to product, Provides product id as parameter
 * @param onProductEdit Callback called as request to navigate to product edition, Provides product id as parameter
 * @param onShopSelect Callback called as request to navigate to shop, Provides shop id as parameter
 * @param onShopEdit Callback called as request to navigate to shop edition, Provides shop id as parameter
 * @param onCategorySelect Callback called as request to navigate to category, Provides category id as parameter
 * @param onCategoryEdit Callback called as request to navigate to category edition, Provides category id as parameter
 * @param onProducerSelect Callback called as request to navigate to producer, Provides producer id as parameter
 * @param onProducerEdit Callback called as request to navigate to producer edition, Provides producer id as parameter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchScreenContent(
    state: SearchScreenState,
    navBackHandlerEnabled: Boolean,
    onProductSelect: (productId: Long) -> Unit,
    onProductEdit: (productId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onCategoryEdit: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onProducerEdit: (producerId: Long) -> Unit,
) {
    with(state) {
        NavBackHandler(
            controller = state.navController,
            enabled = navBackHandlerEnabled,
        )

        val screenWidth = LocalConfiguration.current.screenWidthDp

        Scaffold(
            topBar = {
                AnimatedVisibility(
                    visible = state.navController.backstack.entries.size > 1,
                    enter = slideInVertically(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = EaseOut
                        ),
                        initialOffsetY = { -it }
                    ) + fadeIn(),
                    exit = slideOutVertically(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = EaseIn
                        ),
                        targetOffsetY = { -it }
                    ) + fadeOut(),
                ) {
                    SecondaryAppBar(
                        onBack = {
                            state.navController.apply {
                                if (backstack.entries.size > 1) pop()
                            }
                        },
                        title = {},
                    )
                }
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
                                onProductSelect = {
                                    onProductSelect(it)
                                },
                                onProductEdit = {
                                    onProductEdit(it)
                                }
                            )
                        }
                    }

                    is SearchDestinations.CategoryList -> {
                        Box(modifier = Modifier.padding(it)) {
                            CategoryListRoute(
                                onCategorySelect = {
                                    onCategorySelect(it)
                                },
                                onCategoryEdit = {
                                    onCategoryEdit(it)
                                }
                            )
                        }
                    }

                    is SearchDestinations.ProducerList -> {
                        Box(modifier = Modifier.padding(it)) {
                            ProducerListRoute(
                                onProducerSelect = {
                                    onProducerSelect(it)
                                },
                                onProducerEdit = {
                                    onProducerEdit(it)
                                }
                            )
                        }
                    }

                    is SearchDestinations.ShopList -> {
                        Box(modifier = Modifier.padding(it)) {
                            ShopListRoute(
                                onShopSelect = {
                                    onShopSelect(it)
                                },
                                onShopEdit = {
                                    onShopEdit(it)
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
 * Data representing [SearchScreen] state
 */
internal data class SearchScreenState(
    val navController: NavController<SearchDestinations> = navController(startDestination = SearchDestinations.Start)
)

@Preview(
    group = "SearchScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "SearchScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun SearchScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            SearchScreenContent(
                state = SearchScreenState(),
                navBackHandlerEnabled = false,
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
