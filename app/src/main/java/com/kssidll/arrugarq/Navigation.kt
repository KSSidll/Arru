package com.kssidll.arrugarq

import android.os.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import com.kssidll.arrugarq.ui.screen.category.addcategory.*
import com.kssidll.arrugarq.ui.screen.category.category.*
import com.kssidll.arrugarq.ui.screen.category.categoryranking.*
import com.kssidll.arrugarq.ui.screen.home.*
import com.kssidll.arrugarq.ui.screen.item.additem.*
import com.kssidll.arrugarq.ui.screen.producer.addproducer.*
import com.kssidll.arrugarq.ui.screen.producer.producer.*
import com.kssidll.arrugarq.ui.screen.product.addproduct.*
import com.kssidll.arrugarq.ui.screen.product.editproduct.*
import com.kssidll.arrugarq.ui.screen.product.product.*
import com.kssidll.arrugarq.ui.screen.shop.addshop.*
import com.kssidll.arrugarq.ui.screen.shop.editshop.*
import com.kssidll.arrugarq.ui.screen.shop.shop.*
import com.kssidll.arrugarq.ui.screen.shop.shopranking.*
import com.kssidll.arrugarq.ui.screen.variant.addvariant.*
import com.kssidll.arrugarq.ui.screen.variant.editvariant.*
import dev.olshevski.navigation.reimagined.*
import kotlinx.parcelize.*

@Parcelize
sealed class Screen: Parcelable {
    data object Home: Screen()
    data object AddItem: Screen()
    data object AddProduct: Screen()
    data class EditProduct(val productId: Long): Screen()
    data class AddProductVariant(val productId: Long): Screen()
    data class EditProductVariant(val variantId: Long): Screen()
    data object AddProductCategory: Screen()
    data object AddProductProducer: Screen()
    data object AddShop: Screen()
    data class EditShop(val shopId: Long): Screen()
    data object CategoryRanking: Screen()
    data object ShopRanking: Screen()
    data class Product(val productId: Long): Screen()
    data class Category(val categoryId: Long): Screen()
    data class Producer(val producerId: Long): Screen()
    data class Shop(val shopId: Long): Screen()
}

/**
 * Replaces the backstack with itself after filtering it to contain only destinations matching the given [predicate].
 */
fun <T> NavController<T>.replaceAllFilter(
    action: NavAction,
    predicate: (Screen) -> Boolean
) where T: Screen {
    setNewBackstack(
        entries = backstack.entries.map {
            it.destination
        }
            .filter {
                predicate(it)
            }
            .map {
                navEntry(it)
            },
        action = action,
    )
}

@Composable
fun Navigation(
    navController: NavController<Screen> = rememberNavController(startDestination = Screen.Home)
) {
    NavBackHandler(controller = navController)

    val onBack: () -> Unit = {
        navController.apply {
            if (backstack.entries.size > 1) pop()
        }
    }

    val onBackDeleteShop: (shopId: Long) -> Unit = { shopId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditShop(shopId) && it != Screen.Shop(shopId)
        }
    }

    val onBackDeleteVariant: (variantId: Long) -> Unit = { variantId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditProductVariant(variantId)
        }
    }

    val onBackDeleteProduct: (productId: Long) -> Unit = { productId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditProduct(productId) && it != Screen.Product(productId)
        }
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp
    val easing = CubicBezierEasing(
        0.48f,
        0.19f,
        0.05f,
        1.03f
    )

    AnimatedNavHost(
        controller = navController,
        transitionSpec = { action, _, _ ->
            if (action != NavAction.Pop) {
                slideInHorizontally(
                    animationSpec = tween(
                        600,
                        easing = easing
                    ),
                    initialOffsetX = { screenWidth }) + fadeIn(
                    tween(
                        300,
                        100
                    )
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(
                        600,
                        easing = easing
                    ),
                    targetOffsetX = { -screenWidth }) + fadeOut(
                    tween(
                        300,
                        100
                    )
                )
            } else {
                slideInHorizontally(
                    animationSpec = tween(
                        600,
                        easing = easing
                    ),
                    initialOffsetX = { -screenWidth }) + fadeIn(
                    tween(
                        300,
                        100
                    )
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(
                        600,
                        easing = easing
                    ),
                    targetOffsetX = { screenWidth }) + fadeOut(
                    tween(
                        300,
                        100
                    )
                )
            }
        }
    ) { screen ->
        when (screen) {
            is Screen.Home -> {
                HomeRoute(
                    onAddItem = {
                        navController.navigate(Screen.AddItem)
                    },
                    onDashboardCategoryCardClick = {
                        navController.navigate(Screen.CategoryRanking)
                    },
                    onDashboardShopCardClick = {
                        navController.navigate(Screen.ShopRanking)
                    },
                    onTransactionItemClick = {
                        navController.navigate(Screen.Product(it))
                    },
                    onTransactionCategoryClick = {
                        navController.navigate(Screen.Category(it))
                    },
                    onTransactionProducerClick = {
                        navController.navigate(Screen.Producer(it))
                    },
                    onTransactionShopClick = {
                        navController.navigate(Screen.Shop(it))
                    },
                )
            }

            is Screen.AddItem -> {
                AddItemRoute(
                    onBack = onBack,
                    onProductAdd = {
                        navController.navigate(Screen.AddProduct)
                    },
                    onProductEdit = {
                        navController.navigate(Screen.EditProduct(it))
                    },
                    onVariantAdd = { productId ->
                        navController.navigate(Screen.AddProductVariant(productId))
                    },
                    onVariantEdit = {
                        navController.navigate(Screen.EditProductVariant(it))
                    },
                    onShopAdd = {
                        navController.navigate(Screen.AddShop)
                    },
                    onShopEdit = {
                        navController.navigate(Screen.EditShop(it))
                    }
                )
            }

            is Screen.AddProduct -> {
                AddProductRoute(
                    onBack = onBack,
                    onProductCategoryAdd = {
                        navController.navigate(Screen.AddProductCategory)
                    },
                    onProductProducerAdd = {
                        navController.navigate(Screen.AddProductProducer)
                    }
                )
            }

            is Screen.AddProductVariant -> {
                AddVariantRoute(
                    productId = screen.productId,
                    onBack = onBack,
                )
            }

            is Screen.AddProductCategory -> {
                AddCategoryRoute(
                    onBack = onBack,
                )
            }

            is Screen.AddProductProducer -> {
                AddProducerRoute(
                    onBack = onBack,
                )
            }

            is Screen.AddShop -> {
                AddShopRoute(
                    onBack = onBack,
                )
            }

            is Screen.CategoryRanking -> {
                CategoryRankingRoute(
                    onBack = onBack,
                    onItemClick = {
                        navController.navigate(Screen.Category(it))
                    },
                )
            }

            is Screen.ShopRanking -> {
                ShopRankingRoute(
                    onBack = onBack,
                    onItemClick = {
                        navController.navigate(Screen.Shop(it))
                    },
                )
            }

            is Screen.Category -> {
                CategoryRoute(
                    categoryId = screen.categoryId,
                    onBack = onBack,
                    onItemClick = {
                        navController.navigate(Screen.Product(it))
                    },
                    onProducerClick = {
                        navController.navigate(Screen.Producer(it))
                    },
                    onShopClick = {
                        navController.navigate(Screen.Shop(it))
                    },
                )
            }

            is Screen.Producer -> {
                ProducerRoute(
                    producerId = screen.producerId,
                    onBack = onBack,
                    onItemClick = {
                        navController.navigate(Screen.Product(it))
                    },
                    onCategoryClick = {
                        navController.navigate(Screen.Category(it))
                    },
                    onShopClick = {
                        navController.navigate(Screen.Shop(it))
                    },
                )
            }

            is Screen.Product -> {
                ProductRoute(
                    productId = screen.productId,
                    onBack = onBack,
                    onEdit = {
                        navController.navigate(Screen.EditProduct(screen.productId))
                    },
                    onCategoryClick = {
                        navController.navigate(Screen.Category(it))
                    },
                    onProducerClick = {
                        navController.navigate(Screen.Producer(it))
                    },
                    onShopClick = {
                        navController.navigate(Screen.Shop(it))
                    },
                )
            }

            is Screen.Shop -> {
                ShopRoute(
                    shopId = screen.shopId,
                    onBack = onBack,
                    onEdit = {
                        navController.navigate(Screen.EditShop(screen.shopId))
                    },
                    onItemClick = {
                        navController.navigate(Screen.Product(it))
                    },
                    onCategoryClick = {
                        navController.navigate(Screen.Category(it))
                    },
                    onProducerClick = {
                        navController.navigate(Screen.Producer(it))
                    },
                )
            }

            is Screen.EditShop -> {
                EditShopRoute(
                    shopId = screen.shopId,
                    onBack = {
                        onBack()
                    },
                    onBackDelete = {
                        onBackDeleteShop(screen.shopId)
                    }
                )
            }

            is Screen.EditProductVariant -> {
                EditVariantRoute(
                    variantId = screen.variantId,
                    onBack = {
                        onBack()
                    },
                    onBackDelete = {
                        onBackDeleteVariant(screen.variantId)
                    }
                )
            }

            is Screen.EditProduct -> {
                EditProductRoute(
                    productId = screen.productId,
                    onBack = {
                        onBack()
                    },
                    onBackDelete = {
                        onBackDeleteProduct(screen.productId)
                    },
                    onProducerAdd = {
                        navController.navigate(Screen.AddProductProducer)
                    },
                    onCategoryAdd = {
                        navController.navigate(Screen.AddProductCategory)
                    }
                )
            }
        }
    }
}

