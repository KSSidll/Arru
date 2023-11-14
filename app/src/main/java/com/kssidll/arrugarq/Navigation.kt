package com.kssidll.arrugarq

import android.os.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import com.kssidll.arrugarq.ui.screen.category.addcategory.*
import com.kssidll.arrugarq.ui.screen.category.category.*
import com.kssidll.arrugarq.ui.screen.category.categoryranking.*
import com.kssidll.arrugarq.ui.screen.category.editcategory.*
import com.kssidll.arrugarq.ui.screen.home.*
import com.kssidll.arrugarq.ui.screen.item.additem.*
import com.kssidll.arrugarq.ui.screen.item.edititem.*
import com.kssidll.arrugarq.ui.screen.producer.addproducer.*
import com.kssidll.arrugarq.ui.screen.producer.editproducer.*
import com.kssidll.arrugarq.ui.screen.producer.producer.*
import com.kssidll.arrugarq.ui.screen.product.addproduct.*
import com.kssidll.arrugarq.ui.screen.product.editproduct.*
import com.kssidll.arrugarq.ui.screen.product.product.*
import com.kssidll.arrugarq.ui.screen.settings.*
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
    data class EditItem(val itemId: Long): Screen()
    data object AddProduct: Screen()
    data class EditProduct(val productId: Long): Screen()
    data class AddVariant(val productId: Long): Screen()
    data class EditVariant(val variantId: Long): Screen()
    data object AddCategory: Screen()
    data class EditCategory(val categoryId: Long): Screen()
    data object AddProducer: Screen()
    data class EditProducer(val producerId: Long): Screen()
    data object AddShop: Screen()
    data class EditShop(val shopId: Long): Screen()
    data object CategoryRanking: Screen()
    data object ShopRanking: Screen()
    data class Product(val productId: Long): Screen()
    data class Category(val categoryId: Long): Screen()
    data class Producer(val producerId: Long): Screen()
    data class Shop(val shopId: Long): Screen()
    data object Settings: Screen()
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

fun defaultNavigateContentTransformation(
    screenWidth: Int,
): ContentTransform {
    val easing = CubicBezierEasing(
        0.48f,
        0.19f,
        0.05f,
        1.03f
    )

    return slideInHorizontally(
        animationSpec = tween(
            500,
            easing = easing
        ),
        initialOffsetX = { screenWidth }) + fadeIn(
        tween(
            250,
            50
        )
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(
            500,
            easing = easing
        ),
        targetOffsetX = { -screenWidth }) + fadeOut(
        tween(
            250,
            50
        )
    )
}

fun defaultPopContentTransformation(
    screenWidth: Int,
): ContentTransform {
    val easing = CubicBezierEasing(
        0.48f,
        0.19f,
        0.05f,
        1.03f
    )

    return slideInHorizontally(
        animationSpec = tween(
            500,
            easing = easing
        ),
        initialOffsetX = { -screenWidth }) + fadeIn(
        tween(
            250,
            50
        )
    ) togetherWith slideOutHorizontally(
        animationSpec = tween(
            500,
            easing = easing
        ),
        targetOffsetX = { screenWidth }) + fadeOut(
        tween(
            250,
            50
        )
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

    val onItemEdit: (itemId: Long) -> Unit = {
        navController.navigate(Screen.EditItem(it))
    }

    val onVariantEdit: (variantId: Long) -> Unit = {
        navController.navigate(Screen.EditVariant(it))
    }


    val onProductSelect: (productId: Long) -> Unit = {
        navController.navigate(Screen.Product(it))
    }

    val onProductEdit: (productId: Long) -> Unit = {
        navController.navigate(Screen.EditProduct(it))
    }

    val onShopSelect: (shopId: Long) -> Unit = {
        navController.navigate(Screen.Shop(it))
    }

    val onShopEdit: (shopId: Long) -> Unit = {
        navController.navigate(Screen.EditShop(it))
    }

    val onCategorySelect: (categoryId: Long) -> Unit = {
        navController.navigate(Screen.Category(it))
    }

    val onCategoryEdit: (categoryId: Long) -> Unit = {
        navController.navigate(Screen.EditCategory(it))
    }

    val onProducerSelect: (producerId: Long) -> Unit = {
        navController.navigate(Screen.Producer(it))
    }

    val onProducerEdit: (producerId: Long) -> Unit = {
        navController.navigate(Screen.EditProducer(it))
    }

    val onBackDeleteShop: (shopId: Long) -> Unit = { shopId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditShop(shopId) && it != Screen.Shop(shopId)
        }
    }

    val onBackDeleteVariant: (variantId: Long) -> Unit = { variantId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditVariant(variantId)
        }
    }

    val onBackDeleteProduct: (productId: Long) -> Unit = { productId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditProduct(productId) && it != Screen.Product(productId)
        }
    }

    val onBackDeleteCategory: (categoryId: Long) -> Unit = { categoryId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditCategory(categoryId) && it != Screen.Category(categoryId)
        }
    }

    val onBackDeleteProducer: (producerId: Long) -> Unit = { producerId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditProducer(producerId) && it != Screen.Producer(producerId)
        }
    }

    val onBackDeleteItem: (itemId: Long) -> Unit = { itemId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditItem(itemId)
        }
    }

    val screenWidth = LocalConfiguration.current.screenWidthDp

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
            is Screen.Home -> {
                HomeRoute(
                    navigateSettings = {
                        navController.navigate(Screen.Settings)
                    },
                    onAddItem = {
                        navController.navigate(Screen.AddItem)
                    },
                    onDashboardCategoryCardClick = {
                        navController.navigate(Screen.CategoryRanking)
                    },
                    onDashboardShopCardClick = {
                        navController.navigate(Screen.ShopRanking)
                    },
                    onItemEdit = onItemEdit,
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

            is Screen.AddItem -> {
                AddItemRoute(
                    onBack = onBack,
                    onProductAdd = {
                        navController.navigate(Screen.AddProduct)
                    },
                    onVariantAdd = { productId ->
                        navController.navigate(Screen.AddVariant(productId))
                    },
                    onShopAdd = {
                        navController.navigate(Screen.AddShop)
                    },
                    onProductEdit = onProductEdit,
                    onVariantEdit = onVariantEdit,
                    onShopEdit = onShopEdit,
                )
            }

            is Screen.AddProduct -> {
                AddProductRoute(
                    onBack = onBack,
                    onCategoryAdd = {
                        navController.navigate(Screen.AddCategory)
                    },
                    onProducerAdd = {
                        navController.navigate(Screen.AddProducer)
                    },
                    onCategoryEdit = onCategoryEdit,
                    onProducerEdit = onProducerEdit,
                )
            }

            is Screen.AddVariant -> {
                AddVariantRoute(
                    productId = screen.productId,
                    onBack = onBack,
                )
            }

            is Screen.AddCategory -> {
                AddCategoryRoute(
                    onBack = onBack,
                )
            }

            is Screen.AddProducer -> {
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
                    onCategorySelect = onCategorySelect,
                    onCategoryEdit = onCategoryEdit,
                )
            }

            is Screen.ShopRanking -> {
                ShopRankingRoute(
                    onBack = onBack,
                    onShopSelect = onShopSelect,
                    onShopEdit = onShopEdit,
                )
            }

            is Screen.Category -> {
                CategoryRoute(
                    categoryId = screen.categoryId,
                    onBack = onBack,
                    onCategoryEdit = {
                        onCategoryEdit(screen.categoryId)
                    },
                    onProductSelect = onProductSelect,
                    onItemEdit = onItemEdit,
                    onProducerSelect = onProducerSelect,
                    onShopSelect = onShopSelect,
                )
            }

            is Screen.Producer -> {
                ProducerRoute(
                    producerId = screen.producerId,
                    onBack = onBack,
                    onProducerEdit = {
                        onProducerEdit(screen.producerId)
                    },
                    onProductSelect = onProductSelect,
                    onItemEdit = onItemEdit,
                    onCategorySelect = onCategorySelect,
                    onShopSelect = onShopSelect,
                )
            }

            is Screen.Product -> {
                ProductRoute(
                    productId = screen.productId,
                    onBack = onBack,
                    onProductEdit = {
                        onProductEdit(screen.productId)
                    },
                    onCategorySelect = onCategorySelect,
                    onProducerSelect = onProducerSelect,
                    onShopSelect = onShopSelect,
                    onItemEdit = onItemEdit,
                )
            }

            is Screen.Shop -> {
                ShopRoute(
                    shopId = screen.shopId,
                    onBack = onBack,
                    onShopEdit = {
                        onShopEdit(screen.shopId)
                    },
                    onProductSelect = onProductSelect,
                    onItemEdit = onItemEdit,
                    onCategorySelect = onCategorySelect,
                    onProducerSelect = onProducerSelect,
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

            is Screen.EditVariant -> {
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
                        navController.navigate(Screen.AddProducer)
                    },
                    onCategoryAdd = {
                        navController.navigate(Screen.AddCategory)
                    },
                    onProducerEdit = onProducerEdit,
                    onCategoryEdit = onCategoryEdit,
                )
            }

            is Screen.EditCategory -> {
                EditCategoryRoute(
                    categoryId = screen.categoryId,
                    onBack = {
                        onBack()
                    },
                    onBackDelete = {
                        onBackDeleteCategory(screen.categoryId)
                    }
                )
            }

            is Screen.EditProducer -> {
                EditProducerRoute(
                    producerId = screen.producerId,
                    onBack = {
                        onBack()
                    },
                    onBackDelete = {
                        onBackDeleteProducer(screen.producerId)
                    }
                )
            }

            is Screen.EditItem -> {
                EditItemRoute(
                    itemId = screen.itemId,
                    onBack = {
                        onBack()
                    },
                    onBackDelete = {
                        onBackDeleteItem(screen.itemId)
                    },
                    onShopAdd = {
                        navController.navigate(Screen.AddShop)
                    },
                    onProductAdd = {
                        navController.navigate(Screen.AddProduct)
                    },
                    onVariantAdd = {
                        navController.navigate(Screen.AddVariant(it))
                    },
                    onShopEdit = onShopEdit,
                    onProductEdit = onProductEdit,
                    onVariantEdit = onVariantEdit,
                )
            }

            Screen.Settings -> {
                SettingsRoute(
                    onBack = onBack,
                )
            }
        }
    }
}

