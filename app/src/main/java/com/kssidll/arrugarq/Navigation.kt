package com.kssidll.arrugarq

import android.os.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import com.kssidll.arrugarq.ui.screen.display.category.*
import com.kssidll.arrugarq.ui.screen.display.producer.*
import com.kssidll.arrugarq.ui.screen.display.product.*
import com.kssidll.arrugarq.ui.screen.display.shop.*
import com.kssidll.arrugarq.ui.screen.home.*
import com.kssidll.arrugarq.ui.screen.modify.category.addcategory.*
import com.kssidll.arrugarq.ui.screen.modify.category.editcategory.*
import com.kssidll.arrugarq.ui.screen.modify.item.additem.*
import com.kssidll.arrugarq.ui.screen.modify.item.edititem.*
import com.kssidll.arrugarq.ui.screen.modify.producer.addproducer.*
import com.kssidll.arrugarq.ui.screen.modify.producer.editproducer.*
import com.kssidll.arrugarq.ui.screen.modify.product.addproduct.*
import com.kssidll.arrugarq.ui.screen.modify.product.editproduct.*
import com.kssidll.arrugarq.ui.screen.modify.shop.addshop.*
import com.kssidll.arrugarq.ui.screen.modify.shop.editshop.*
import com.kssidll.arrugarq.ui.screen.modify.variant.addvariant.*
import com.kssidll.arrugarq.ui.screen.modify.variant.editvariant.*
import com.kssidll.arrugarq.ui.screen.ranking.categoryranking.*
import com.kssidll.arrugarq.ui.screen.ranking.shopranking.*
import com.kssidll.arrugarq.ui.screen.search.*
import com.kssidll.arrugarq.ui.screen.settings.*
import com.kssidll.arrugarq.ui.screen.spendingcomparison.categoryspendingcomparison.*
import com.kssidll.arrugarq.ui.screen.spendingcomparison.shopspendingcomparison.*
import dev.olshevski.navigation.reimagined.*
import kotlinx.parcelize.*

@Parcelize
sealed class Screen: Parcelable {
    data object Home: Screen()

    data object Settings: Screen()
    data object Search: Screen()

    data class Product(val productId: Long): Screen()
    data class Category(val categoryId: Long): Screen()
    data class Producer(val producerId: Long): Screen()
    data class Shop(val shopId: Long): Screen()

    data object ItemAdd: Screen()
    data class ProductAdd(val defaultName: String? = null): Screen()
    data class VariantAdd(
        val productId: Long,
        val defaultName: String? = null
    ): Screen()

    data class CategoryAdd(val defaultName: String? = null): Screen()
    data class ProducerAdd(val defaultName: String? = null): Screen()
    data class ShopAdd(val defaultName: String? = null): Screen()

    data class ItemEdit(val itemId: Long): Screen()
    data class ProductEdit(val productId: Long): Screen()
    data class VariantEdit(val variantId: Long): Screen()
    data class CategoryEdit(val categoryId: Long): Screen()
    data class ProducerEdit(val producerId: Long): Screen()
    data class ShopEdit(val shopId: Long): Screen()

    data object CategoryRanking: Screen()
    data object ShopRanking: Screen()

    data class CategorySpendingComparison(
        val year: Int,
        val month: Int
    ): Screen()

    data class ShopSpendingComparison(
        val year: Int,
        val month: Int
    ): Screen()
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

    val navigateBack: () -> Unit = {
        navController.apply {
            if (backstack.entries.size > 1) pop()
        }
    }

    val navigateBackDeleteShop: (shopId: Long) -> Unit = { shopId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.ShopEdit(shopId) && it != Screen.Shop(shopId)
        }
    }

    val navigateBackDeleteVariant: (variantId: Long) -> Unit = { variantId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.VariantEdit(variantId)
        }
    }

    val navigateBackDeleteProduct: (productId: Long) -> Unit = { productId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.ProductEdit(productId) && it != Screen.Product(productId)
        }
    }

    val navigateBackDeleteCategory: (categoryId: Long) -> Unit = { categoryId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.CategoryEdit(categoryId) && it != Screen.Category(categoryId)
        }
    }

    val navigateBackDeleteProducer: (producerId: Long) -> Unit = { producerId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.ProducerEdit(producerId) && it != Screen.Producer(producerId)
        }
    }

    val navigateBackDeleteItem: (itemId: Long) -> Unit = { itemId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.ItemEdit(itemId)
        }
    }

    val navigateSettings: () -> Unit = {
        navController.navigate(Screen.Settings)
    }

    val navigateSearch: () -> Unit = {
        navController.navigate(Screen.Search)
    }


    val navigateProduct: (productId: Long) -> Unit = {
        navController.navigate(Screen.Product(it))
    }

    val navigateCategory: (categoryId: Long) -> Unit = {
        navController.navigate(Screen.Category(it))
    }

    val navigateProducer: (producerId: Long) -> Unit = {
        navController.navigate(Screen.Producer(it))
    }

    val navigateShop: (shopId: Long) -> Unit = {
        navController.navigate(Screen.Shop(it))
    }


    val navigateTransactionAdd: () -> Unit = {
        // TODO add navigation to transaction add screen once implemented
//        navController.navigate(Screen.TransactionAdd)
    }

    val navigateItemAdd: () -> Unit = {
        navController.navigate(Screen.ItemAdd)
    }

    val navigateProductAdd: (query: String?) -> Unit = {
        navController.navigate(Screen.ProductAdd(it))
    }

    val navigateVariantAdd: (productId: Long, query: String?) -> Unit = { productId, query ->
        navController.navigate(
            Screen.VariantAdd(
                productId,
                query
            )
        )
    }

    val navigateCategoryAdd: (query: String?) -> Unit = {
        navController.navigate(Screen.CategoryAdd(it))
    }

    val navigateProducerAdd: (query: String?) -> Unit = {
        navController.navigate(Screen.ProducerAdd(it))
    }

    val navigateShopAdd: (query: String?) -> Unit = {
        navController.navigate(Screen.ShopAdd(it))
    }


    val navigateItemEdit: (itemId: Long) -> Unit = {
        navController.navigate(Screen.ItemEdit(it))
    }

    val navigateProductEdit: (productId: Long) -> Unit = {
        navController.navigate(Screen.ProductEdit(it))
    }

    val navigateVariantEdit: (variantId: Long) -> Unit = {
        navController.navigate(Screen.VariantEdit(it))
    }

    val navigateCategoryEdit: (categoryId: Long) -> Unit = {
        navController.navigate(Screen.CategoryEdit(it))
    }

    val navigateProducerEdit: (producerId: Long) -> Unit = {
        navController.navigate(Screen.ProducerEdit(it))
    }

    val navigateShopEdit: (shopId: Long) -> Unit = {
        navController.navigate(Screen.ShopEdit(it))
    }


    val navigateCategoryRanking: () -> Unit = {
        navController.navigate(Screen.CategoryRanking)
    }

    val navigateShopRanking: () -> Unit = {
        navController.navigate(Screen.ShopRanking)
    }

    val navigateCategorySpendingComparison: (year: Int, month: Int) -> Unit = { year, month ->
        navController.navigate(
            Screen.CategorySpendingComparison(
                year,
                month
            )
        )
    }

    val navigateShopSpendingComparison: (year: Int, month: Int) -> Unit = { year, month ->
        navController.navigate(
            Screen.ShopSpendingComparison(
                year,
                month
            )
        )
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
                    navigateSettings = navigateSettings,
                    navigateSearch = navigateSearch,
                    navigateProduct = navigateProduct,
                    navigateCategory = navigateCategory,
                    navigateProducer = navigateProducer,
                    navigateShop = navigateShop,
                    navigateTransactionAdd = navigateTransactionAdd,
                    navigateItemEdit = navigateItemEdit,
                    navigateCategoryRanking = navigateCategoryRanking,
                    navigateShopRanking = navigateShopRanking,
                    navigateCategorySpendingComparison = navigateCategorySpendingComparison,
                    navigateShopSpendingComparison = navigateShopSpendingComparison,
                )
            }

            is Screen.ItemAdd -> {
                AddItemRoute(
                    navigateBack = navigateBack,
                    navigateProductAdd = navigateProductAdd,
                    navigateVariantAdd = navigateVariantAdd,
                    navigateShopAdd = navigateShopAdd,
                    navigateProductEdit = navigateProductEdit,
                    navigateVariantEdit = navigateVariantEdit,
                    navigateShopEdit = navigateShopEdit,
                )
            }

            is Screen.ProductAdd -> {
                AddProductRoute(
                    defaultName = screen.defaultName,
                    navigateBack = navigateBack,
                    navigateCategoryAdd = navigateCategoryAdd,
                    navigateProducerAdd = navigateProducerAdd,
                    navigateCategoryEdit = navigateCategoryEdit,
                    navigateProducerEdit = navigateProducerEdit,
                )
            }

            is Screen.VariantAdd -> {
                AddVariantRoute(
                    productId = screen.productId,
                    defaultName = screen.defaultName,
                    navigateBack = navigateBack,
                )
            }

            is Screen.CategoryAdd -> {
                AddCategoryRoute(
                    defaultName = screen.defaultName,
                    navigateBack = navigateBack,
                )
            }

            is Screen.ProducerAdd -> {
                AddProducerRoute(
                    defaultName = screen.defaultName,
                    navigateBack = navigateBack,
                )
            }

            is Screen.ShopAdd -> {
                AddShopRoute(
                    defaultName = screen.defaultName,
                    navigateBack = navigateBack,
                )
            }

            is Screen.CategoryRanking -> {
                CategoryRankingRoute(
                    navigateBack = navigateBack,
                    navigateCategory = navigateCategory,
                    navigateCategoryEdit = navigateCategoryEdit,
                )
            }

            is Screen.ShopRanking -> {
                ShopRankingRoute(
                    navigateBack = navigateBack,
                    navigateShop = navigateShop,
                    navigateShopEdit = navigateShopEdit,
                )
            }

            is Screen.Category -> {
                CategoryRoute(
                    categoryId = screen.categoryId,
                    navigateBack = navigateBack,
                    navigateProduct = navigateProduct,
                    navigateProducer = navigateProducer,
                    navigateShop = navigateShop,
                    navigateItemEdit = navigateItemEdit,
                    navigateCategoryEdit = {
                        navigateCategoryEdit(screen.categoryId)
                    },
                )
            }

            is Screen.Producer -> {
                ProducerRoute(
                    producerId = screen.producerId,
                    navigateBack = navigateBack,
                    navigateProduct = navigateProduct,
                    navigateCategory = navigateCategory,
                    navigateShop = navigateShop,
                    navigateItemEdit = navigateItemEdit,
                    navigateProducerEdit = {
                        navigateProducerEdit(screen.producerId)
                    },
                )
            }

            is Screen.Product -> {
                ProductRoute(
                    productId = screen.productId,
                    navigateBack = navigateBack,
                    navigateCategory = navigateCategory,
                    navigateProducer = navigateProducer,
                    navigateShop = navigateShop,
                    navigateItemEdit = navigateItemEdit,
                    navigateProductEdit = {
                        navigateProductEdit(screen.productId)
                    },
                )
            }

            is Screen.Shop -> {
                ShopRoute(
                    shopId = screen.shopId,
                    navigateBack = navigateBack,
                    navigateProduct = navigateProduct,
                    navigateCategory = navigateCategory,
                    navigateProducer = navigateProducer,
                    navigateItemEdit = navigateItemEdit,
                    navigateShopEdit = {
                        navigateShopEdit(screen.shopId)
                    },
                )
            }

            is Screen.ShopEdit -> {
                EditShopRoute(
                    shopId = screen.shopId,
                    navigateBack = {
                        navigateBack()
                    },
                    navigateBackDelete = {
                        navigateBackDeleteShop(screen.shopId)
                    }
                )
            }

            is Screen.VariantEdit -> {
                EditVariantRoute(
                    variantId = screen.variantId,
                    navigateBack = {
                        navigateBack()
                    },
                    navigateBackDelete = {
                        navigateBackDeleteVariant(screen.variantId)
                    }
                )
            }

            is Screen.ProductEdit -> {
                EditProductRoute(
                    productId = screen.productId,
                    navigateBack = navigateBack,
                    navigateBackDelete = {
                        navigateBackDeleteProduct(screen.productId)
                    },
                    navigateCategoryAdd = navigateCategoryAdd,
                    navigateProducerAdd = navigateProducerAdd,
                    navigateCategoryEdit = navigateCategoryEdit,
                    navigateProducerEdit = navigateProducerEdit,
                )
            }

            is Screen.CategoryEdit -> {
                EditCategoryRoute(
                    categoryId = screen.categoryId,
                    navigateBack = navigateBack,
                    navigateBackDelete = {
                        navigateBackDeleteCategory(screen.categoryId)
                    }
                )
            }

            is Screen.ProducerEdit -> {
                EditProducerRoute(
                    producerId = screen.producerId,
                    navigateBack = navigateBack,
                    navigateBackDelete = {
                        navigateBackDeleteProducer(screen.producerId)
                    }
                )
            }

            is Screen.ItemEdit -> {
                EditItemRoute(
                    itemId = screen.itemId,
                    navigateBack = navigateBack,
                    navigateBackDelete = {
                        navigateBackDeleteItem(screen.itemId)
                    },
                    navigateProductAdd = navigateProductAdd,
                    navigateVariantAdd = navigateVariantAdd,
                    navigateProductEdit = navigateProductEdit,
                    navigateVariantEdit = navigateVariantEdit,
                )
            }

            is Screen.Settings -> {
                SettingsRoute(
                    navigateBack = navigateBack,
                )
            }

            is Screen.Search -> {
                SearchRoute(
                    navigateBack = navigateBack,
                    navigateProduct = navigateProduct,
                    navigateCategory = navigateCategory,
                    navigateProducer = navigateProducer,
                    navigateShop = navigateShop,
                    navigateProductEdit = navigateProductEdit,
                    navigateCategoryEdit = navigateCategoryEdit,
                    navigateProducerEdit = navigateProducerEdit,
                    navigateShopEdit = navigateShopEdit,
                )
            }

            is Screen.CategorySpendingComparison -> {
                CategorySpendingComparisonRoute(
                    navigateBack = navigateBack,
                    year = screen.year,
                    month = screen.month,
                )
            }

            is Screen.ShopSpendingComparison -> {
                ShopSpendingComparisonRoute(
                    navigateBack = navigateBack,
                    year = screen.year,
                    month = screen.month,
                )
            }
        }
    }
}

