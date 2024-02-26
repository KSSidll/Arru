package com.kssidll.arru

import android.os.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import com.kssidll.arru.ui.screen.display.category.*
import com.kssidll.arru.ui.screen.display.producer.*
import com.kssidll.arru.ui.screen.display.product.*
import com.kssidll.arru.ui.screen.display.shop.*
import com.kssidll.arru.ui.screen.display.transaction.*
import com.kssidll.arru.ui.screen.home.*
import com.kssidll.arru.ui.screen.modify.category.addcategory.*
import com.kssidll.arru.ui.screen.modify.category.editcategory.*
import com.kssidll.arru.ui.screen.modify.item.additem.*
import com.kssidll.arru.ui.screen.modify.item.edititem.*
import com.kssidll.arru.ui.screen.modify.producer.addproducer.*
import com.kssidll.arru.ui.screen.modify.producer.editproducer.*
import com.kssidll.arru.ui.screen.modify.product.addproduct.*
import com.kssidll.arru.ui.screen.modify.product.editproduct.*
import com.kssidll.arru.ui.screen.modify.shop.addshop.*
import com.kssidll.arru.ui.screen.modify.shop.editshop.*
import com.kssidll.arru.ui.screen.modify.transaction.addtransaction.*
import com.kssidll.arru.ui.screen.modify.transaction.edittransaction.*
import com.kssidll.arru.ui.screen.modify.variant.addvariant.*
import com.kssidll.arru.ui.screen.modify.variant.editvariant.*
import com.kssidll.arru.ui.screen.ranking.categoryranking.*
import com.kssidll.arru.ui.screen.ranking.shopranking.*
import com.kssidll.arru.ui.screen.search.*
import com.kssidll.arru.ui.screen.settings.*
import com.kssidll.arru.ui.screen.spendingcomparison.categoryspendingcomparison.*
import com.kssidll.arru.ui.screen.spendingcomparison.shopspendingcomparison.*
import dev.olshevski.navigation.reimagined.*
import kotlinx.parcelize.*

/**
 * Interface for navigation destinations that can accept shop id
 */
private interface AcceptsShopId {
    val providedShopId: MutableState<Long?>

    /**
     * Provides the [id] to the navigation destination
     * @param id id to provide
     * @param forceToNull whether to set the value to null if [id] is null, false by default
     */
    fun provideShop(
        id: Long? = null,
        forceToNull: Boolean = false
    ) {
        if (!forceToNull && id != null) {
            providedShopId.value = id
        }
    }
}

/**
 * Interface for navigation destinations that can accept product id with product variant id
 */
private interface AcceptsProductId {
    val providedProductId: MutableState<Long?>
    val providedVariantId: MutableState<Long?>

    /**
     * Provides the [productId] and [variantId] to the navigation destination
     *
     * Will forcefully set variant to null if only [productId] is provided
     * @param productId product id to provide
     * @param variantId variant id to provide
     * @param forceProductToNull whether to set the product value to null if [productId] is null, false by default
     * @param forceVariantToNull whether to set the variant value to null if [variantId] is null, false by default
     */
    fun provideProduct(
        productId: Long? = null,
        variantId: Long? = null,
        forceProductToNull: Boolean = false,
        forceVariantToNull: Boolean = false,
    ) {
        if (forceProductToNull || productId != null) {
            providedProductId.value = productId
        }

        if ((forceVariantToNull || productId != null) || variantId != null) {
            providedVariantId.value = variantId
        }
    }
}

/**
 * Interface for navigation destinations that can accept producer id
 */
private interface AcceptsProducerId {
    val providedProducerId: MutableState<Long?>

    /**
     * Provides the [id] to the navigation destination
     * @param id id to provide
     * @param forceToNull whether to set the value to null if [id] is null, false by default
     */
    fun provideProducer(
        id: Long? = null,
        forceToNull: Boolean = false
    ) {
        if (!forceToNull && id != null) {
            providedProducerId.value = id
        }
    }
}

/**
 * Interface for navigation destinations that can accept a category id
 */
private interface AcceptsCategoryId {
    val providedCategoryId: MutableState<Long?>

    /**
     * Provides the [id] to the navigation destination
     * @param id id to provide
     * @param forceToNull whether to set the value to null if [id] is null, false by default
     */
    fun provideCategory(
        id: Long? = null,
        forceToNull: Boolean = false
    ) {
        if (!forceToNull && id != null) {
            providedCategoryId.value = id
        }
    }
}

@Parcelize
sealed class Screen: Parcelable {
    @Immutable
    data object Home: Screen()

    @Immutable
    data object Settings: Screen()

    @Immutable
    data object Search: Screen()

    @Stable
    data class Transaction(val transactionId: Long): Screen()

    @Stable
    data class Product(val productId: Long): Screen()

    @Stable
    data class Category(val categoryId: Long): Screen()

    @Stable
    data class Producer(val producerId: Long): Screen()

    @Stable
    data class Shop(val shopId: Long): Screen()

    @Stable
    data class TransactionAdd(
        override val providedShopId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ): Screen(), AcceptsShopId

    @Stable
    data class ItemAdd(
        val transactionId: Long,
        override val providedProductId: @RawValue MutableState<Long?> = mutableStateOf(null),
        override val providedVariantId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ): Screen(), AcceptsProductId

    @Stable
    data class ProductAdd(
        val defaultName: String? = null,
        override val providedProducerId: @RawValue MutableState<Long?> = mutableStateOf(null),
        override val providedCategoryId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ): Screen(), AcceptsProducerId, AcceptsCategoryId

    @Stable
    data class VariantAdd(
        val productId: Long,
        val defaultName: String? = null,
    ): Screen()

    @Stable
    data class CategoryAdd(val defaultName: String? = null): Screen()

    @Stable
    data class ProducerAdd(val defaultName: String? = null): Screen()

    @Stable
    data class ShopAdd(val defaultName: String? = null): Screen()

    @Stable
    data class TransactionEdit(
        val transactionId: Long,
        override val providedShopId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ): Screen(), AcceptsShopId

    @Stable
    data class ItemEdit(
        val itemId: Long,
        override val providedProductId: @RawValue MutableState<Long?> = mutableStateOf(null),
        override val providedVariantId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ): Screen(), AcceptsProductId

    @Stable
    data class ProductEdit(
        val productId: Long,
        override val providedProducerId: @RawValue MutableState<Long?> = mutableStateOf(null),
        override val providedCategoryId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ): Screen(), AcceptsProducerId, AcceptsCategoryId

    @Stable
    data class VariantEdit(val variantId: Long): Screen()

    @Stable
    data class CategoryEdit(val categoryId: Long): Screen()

    @Stable
    data class ProducerEdit(val producerId: Long): Screen()

    @Stable
    data class ShopEdit(val shopId: Long): Screen()

    @Immutable
    data object CategoryRanking: Screen()

    @Immutable
    data object ShopRanking: Screen()

    @Stable
    data class CategorySpendingComparison(
        val year: Int,
        val month: Int
    ): Screen()

    @Stable
    data class ShopSpendingComparison(
        val year: Int,
        val month: Int
    ): Screen()
}

/**
 * @return previous destination from the backstack, null if none exists
 */
fun <T> NavController<T>.previousDestination(): T? {
    if (backstack.entries.size == 1) return null

    return backstack.entries.let {
        it[it.lastIndex - 1].destination
    }
}

/**
 * @return current destination from the backstack, null if none exists
 */
fun <T> NavController<T>.currentDestination(): T? {
    return backstack.entries.last().destination
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
    isExpandedScreen: Boolean,
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

    val navigateBackDeleteTransaction: (transactionId: Long) -> Unit = { transactionId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.TransactionEdit(transactionId) && it != Screen.Transaction(transactionId)
        }
    }

    val navigateSettings: () -> Unit = {
        if (navController.currentDestination() !is Screen.Settings) {
            navController.navigate(Screen.Settings)
        }
    }

    val navigateSearch: () -> Unit = {
        if (navController.currentDestination() !is Screen.Search) {
            navController.navigate(Screen.Search)
        }
    }


    val navigateTransaction: (transactionId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.Transaction) {
            navController.navigate(Screen.Transaction(it))
        }
    }

    val navigateProduct: (productId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.Product) {
            navController.navigate(Screen.Product(it))
        }
    }

    val navigateCategory: (categoryId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.Category) {
            navController.navigate(Screen.Category(it))
        }
    }

    val navigateProducer: (producerId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.Producer) {
            navController.navigate(Screen.Producer(it))
        }
    }

    val navigateShop: (shopId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.Shop) {
            navController.navigate(Screen.Shop(it))
        }
    }


    val navigateTransactionAdd: () -> Unit = {
        if (navController.currentDestination() !is Screen.TransactionAdd) {
            navController.navigate(Screen.TransactionAdd())
        }
    }

    val navigateItemAdd: (transactionId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.ItemAdd) {
            navController.navigate(Screen.ItemAdd(it))
        }
    }

    val navigateProductAdd: (query: String?) -> Unit = {
        if (navController.currentDestination() !is Screen.ProductAdd) {
            navController.navigate(Screen.ProductAdd(it))
        }
    }

    val navigateVariantAdd: (productId: Long, query: String?) -> Unit = { productId, query ->
        if (navController.currentDestination() !is Screen.VariantAdd) {
            navController.navigate(
                Screen.VariantAdd(
                    productId,
                    query
                )
            )
        }
    }

    val navigateCategoryAdd: (query: String?) -> Unit = {
        if (navController.currentDestination() !is Screen.CategoryAdd) {
            navController.navigate(Screen.CategoryAdd(it))
        }
    }

    val navigateProducerAdd: (query: String?) -> Unit = {
        if (navController.currentDestination() !is Screen.ProducerAdd) {
            navController.navigate(Screen.ProducerAdd(it))
        }
    }

    val navigateShopAdd: (query: String?) -> Unit = {
        if (navController.currentDestination() !is Screen.ShopAdd) {
            navController.navigate(Screen.ShopAdd(it))
        }
    }


    val navigateTransactionEdit: (transactionId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.TransactionEdit) {
            navController.navigate(Screen.TransactionEdit(it))
        }
    }

    val navigateItemEdit: (itemId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.ItemEdit) {
            navController.navigate(Screen.ItemEdit(it))
        }
    }

    val navigateProductEdit: (productId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.ProductEdit) {
            navController.navigate(Screen.ProductEdit(it))
        }
    }

    val navigateVariantEdit: (variantId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.VariantEdit) {
            navController.navigate(Screen.VariantEdit(it))
        }
    }

    val navigateCategoryEdit: (categoryId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.CategoryEdit) {
            navController.navigate(Screen.CategoryEdit(it))
        }
    }

    val navigateProducerEdit: (producerId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.ProducerEdit) {
            navController.navigate(Screen.ProducerEdit(it))
        }
    }

    val navigateShopEdit: (shopId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.ShopEdit) {
            navController.navigate(Screen.ShopEdit(it))
        }
    }


    val navigateCategoryRanking: () -> Unit = {
        if (navController.currentDestination() !is Screen.CategoryRanking) {
            navController.navigate(Screen.CategoryRanking)
        }
    }

    val navigateShopRanking: () -> Unit = {
        if (navController.currentDestination() !is Screen.ShopRanking) {
            navController.navigate(Screen.ShopRanking)
        }
    }

    val navigateCategorySpendingComparison: (year: Int, month: Int) -> Unit = { year, month ->
        if (navController.currentDestination() !is Screen.CategorySpendingComparison) {
            navController.navigate(
                Screen.CategorySpendingComparison(
                    year,
                    month
                )
            )
        }
    }

    val navigateShopSpendingComparison: (year: Int, month: Int) -> Unit = { year, month ->
        if (navController.currentDestination() !is Screen.ShopSpendingComparison) {
            navController.navigate(
                Screen.ShopSpendingComparison(
                    year,
                    month
                )
            )
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
                    isExpandedScreen = isExpandedScreen,
                    navigateSettings = navigateSettings,
                    navigateSearch = navigateSearch,
                    navigateProduct = navigateProduct,
                    navigateCategory = navigateCategory,
                    navigateProducer = navigateProducer,
                    navigateShop = navigateShop,
                    navigateTransactionAdd = navigateTransactionAdd,
                    navigateTransactionEdit = navigateTransactionEdit,
                    navigateItemAdd = {
                        navigateTransaction(it)
                        navigateItemAdd(it)
                    },
                    navigateItemEdit = navigateItemEdit,
                    navigateCategoryRanking = navigateCategoryRanking,
                    navigateShopRanking = navigateShopRanking,
                    navigateCategorySpendingComparison = navigateCategorySpendingComparison,
                    navigateShopSpendingComparison = navigateShopSpendingComparison,
                )
            }

            is Screen.ItemAdd -> {
                AddItemRoute(
                    transactionId = screen.transactionId,
                    navigateBack = navigateBack,
                    navigateProductAdd = navigateProductAdd,
                    navigateVariantAdd = navigateVariantAdd,
                    navigateProductEdit = navigateProductEdit,
                    navigateVariantEdit = navigateVariantEdit,
                    providedProductId = screen.providedProductId.value,
                    providedVariantId = screen.providedVariantId.value,
                )
            }

            is Screen.ProductAdd -> {
                AddProductRoute(
                    defaultName = screen.defaultName,
                    navigateBack = {
                        val previousDestination = navController.previousDestination()
                        if (previousDestination != null && previousDestination is AcceptsProductId) {
                            previousDestination.provideProduct(it)
                        }
                        navigateBack()
                    },
                    navigateCategoryAdd = navigateCategoryAdd,
                    navigateProducerAdd = navigateProducerAdd,
                    navigateCategoryEdit = navigateCategoryEdit,
                    navigateProducerEdit = navigateProducerEdit,
                    providedProducerId = screen.providedProducerId.value,
                    providedCategoryId = screen.providedCategoryId.value,
                )
            }

            is Screen.VariantAdd -> {
                AddVariantRoute(
                    productId = screen.productId,
                    defaultName = screen.defaultName,
                    navigateBack = {
                        val previousDestination = navController.previousDestination()
                        if (previousDestination != null && previousDestination is AcceptsProductId) {
                            previousDestination.provideProduct(
                                screen.productId,
                                it
                            )
                        }
                        navigateBack()
                    },
                )
            }

            is Screen.CategoryAdd -> {
                AddCategoryRoute(
                    defaultName = screen.defaultName,
                    navigateBack = {
                        val previousDestination = navController.previousDestination()
                        if (previousDestination != null && previousDestination is AcceptsCategoryId) {
                            previousDestination.provideCategory(it)
                        }
                        navigateBack()
                    },
                )
            }

            is Screen.ProducerAdd -> {
                AddProducerRoute(
                    defaultName = screen.defaultName,
                    navigateBack = {
                        val previousDestination = navController.previousDestination()
                        if (previousDestination != null && previousDestination is AcceptsProducerId) {
                            previousDestination.provideProducer(it)
                        }
                        navigateBack()
                    },
                )
            }

            is Screen.ShopAdd -> {
                AddShopRoute(
                    defaultName = screen.defaultName,
                    navigateBack = {
                        val previousDestination = navController.previousDestination()
                        if (previousDestination != null && previousDestination is AcceptsShopId) {
                            previousDestination.provideShop(it)
                        }
                        navigateBack()
                    },
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
                    providedProducerId = screen.providedProducerId.value,
                    providedCategoryId = screen.providedCategoryId.value,
                )
            }

            is Screen.CategoryEdit -> {
                EditCategoryRoute(
                    categoryId = screen.categoryId,
                    navigateBack = navigateBack,
                    navigateBackDelete = {
                        navigateBackDeleteCategory(screen.categoryId)
                    },
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
                    providedProductId = screen.providedProductId.value,
                    providedVariantId = screen.providedVariantId.value,
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

            is Screen.TransactionAdd -> {
                AddTransactionRoute(
                    isExpandedScreen = isExpandedScreen,
                    navigateBack = navigateBack,
                    navigateTransaction = navigateTransaction,
                    navigateShopAdd = navigateShopAdd,
                    navigateShopEdit = navigateShopEdit,
                    providedShopId = screen.providedShopId.value,
                )
            }

            is Screen.TransactionEdit -> {
                EditTransactionRoute(
                    isExpandedScreen = isExpandedScreen,
                    transactionId = screen.transactionId,
                    navigateBack = navigateBack,
                    navigateBackDelete = navigateBackDeleteTransaction,
                    navigateShopAdd = navigateShopAdd,
                    navigateShopEdit = navigateShopEdit,
                    providedShopId = screen.providedShopId.value,
                )
            }

            is Screen.Transaction -> {
                TransactionRoute(
                    transactionId = screen.transactionId,
                    navigateBack = navigateBack,
                    navigateTransactionEdit = navigateTransactionEdit,
                    navigateItemAdd = navigateItemAdd,
                    navigateProduct = navigateProduct,
                    navigateItemEdit = navigateItemEdit,
                    navigateCategory = navigateCategory,
                    navigateProducer = navigateProducer,
                    navigateShop = navigateShop,
                )
            }
        }
    }
}

