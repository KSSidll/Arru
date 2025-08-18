package com.kssidll.arru

import android.os.Parcelable
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalWindowInfo
import com.kssidll.arru.ui.screen.backups.BackupsRoute
import com.kssidll.arru.ui.screen.display.product.DisplayProductRoute
import com.kssidll.arru.ui.screen.display.productcategory.DisplayProductCategoryRoute
import com.kssidll.arru.ui.screen.display.productproducer.DisplayProductProducerRoute
import com.kssidll.arru.ui.screen.display.shop.DisplayShopRoute
import com.kssidll.arru.ui.screen.display.transaction.DisplayTransactionRoute
import com.kssidll.arru.ui.screen.home.HomeRoute
import com.kssidll.arru.ui.screen.modify.item.additem.AddItemRoute
import com.kssidll.arru.ui.screen.modify.item.edititem.EditItemRoute
import com.kssidll.arru.ui.screen.modify.product.addproduct.AddProductRoute
import com.kssidll.arru.ui.screen.modify.product.editproduct.EditProductRoute
import com.kssidll.arru.ui.screen.modify.productcategory.addproductcategory.AddProductCategoryRoute
import com.kssidll.arru.ui.screen.modify.productcategory.editproductcategory.EditProductCategoryRoute
import com.kssidll.arru.ui.screen.modify.productproducer.addproductproducer.AddProductProducerRoute
import com.kssidll.arru.ui.screen.modify.productproducer.editproductproducer.EditProductProducerRoute
import com.kssidll.arru.ui.screen.modify.productvariant.addproductvariant.AddProductVariantRoute
import com.kssidll.arru.ui.screen.modify.productvariant.editproductvariant.EditProductVariantRoute
import com.kssidll.arru.ui.screen.modify.shop.addshop.AddShopRoute
import com.kssidll.arru.ui.screen.modify.shop.editshop.EditShopRoute
import com.kssidll.arru.ui.screen.modify.transaction.addtransaction.AddTransactionRoute
import com.kssidll.arru.ui.screen.modify.transaction.edittransaction.EditTransactionRoute
import com.kssidll.arru.ui.screen.ranking.categoryranking.CategoryRankingRoute
import com.kssidll.arru.ui.screen.ranking.shopranking.ShopRankingRoute
import com.kssidll.arru.ui.screen.search.SearchRoute
import com.kssidll.arru.ui.screen.settings.SettingsRoute
import com.kssidll.arru.ui.screen.spendingcomparison.categoryspendingcomparison.CategorySpendingComparisonRoute
import com.kssidll.arru.ui.screen.spendingcomparison.shopspendingcomparison.ShopSpendingComparisonRoute
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navEntry
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

/** Interface for navigation destinations that can accept shop id */
private interface AcceptsShopId {
    val providedShopId: MutableState<Long?>

    /**
     * Provides the [id] to the navigation destination
     *
     * @param id id to provide
     * @param forceToNull whether to set the value to null if [id] is null, false by default
     */
    fun provideShop(id: Long? = null, forceToNull: Boolean = false) {
        if (!forceToNull && id != null) {
            providedShopId.value = id
        }
    }
}

/** Interface for navigation destinations that can accept product id with product variant id */
private interface AcceptsProductId {
    val providedProductId: MutableState<Long?>
    val providedVariantId: MutableState<Long?>

    /**
     * Provides the [productId] and [variantId] to the navigation destination
     *
     * Will forcefully set variant to null if only [productId] is provided
     *
     * @param productId product id to provide
     * @param variantId variant id to provide
     * @param forceProductToNull whether to set the product value to null if [productId] is null,
     *   false by default
     * @param forceVariantToNull whether to set the variant value to null if [variantId] is null,
     *   false by default
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

/** Interface for navigation destinations that can accept producer id */
private interface AcceptsProducerId {
    val providedProducerId: MutableState<Long?>

    /**
     * Provides the [id] to the navigation destination
     *
     * @param id id to provide
     * @param forceToNull whether to set the value to null if [id] is null, false by default
     */
    fun provideProducer(id: Long? = null, forceToNull: Boolean = false) {
        if (!forceToNull && id != null) {
            providedProducerId.value = id
        }
    }
}

/** Interface for navigation destinations that can accept a category id */
private interface AcceptsCategoryId {
    val providedCategoryId: MutableState<Long?>

    /**
     * Provides the [id] to the navigation destination
     *
     * @param id id to provide
     * @param forceToNull whether to set the value to null if [id] is null, false by default
     */
    fun provideCategory(id: Long? = null, forceToNull: Boolean = false) {
        if (!forceToNull && id != null) {
            providedCategoryId.value = id
        }
    }
}

@Parcelize
sealed class Screen : Parcelable {
    @Immutable data object Home : Screen()

    @Immutable data object Settings : Screen()

    @Immutable data object Search : Screen()

    @Stable data class DisplayTransaction(val transactionId: Long) : Screen()

    @Stable data class DisplayProduct(val productId: Long) : Screen()

    @Stable data class DisplayProductCategory(val categoryId: Long) : Screen()

    @Stable data class DisplayProductProducer(val producerId: Long) : Screen()

    @Stable data class DisplayShop(val shopId: Long) : Screen()

    @Stable
    data class AddTransaction(
        override val providedShopId: @RawValue MutableState<Long?> = mutableStateOf(null)
    ) : Screen(), AcceptsShopId

    @Stable
    data class AddItem(
        val transactionId: Long,
        override val providedProductId: @RawValue MutableState<Long?> = mutableStateOf(null),
        override val providedVariantId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ) : Screen(), AcceptsProductId

    @Stable
    data class AddProduct(
        val defaultName: String? = null,
        override val providedProducerId: @RawValue MutableState<Long?> = mutableStateOf(null),
        override val providedCategoryId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ) : Screen(), AcceptsProducerId, AcceptsCategoryId

    @Stable
    data class AddProductVariant(val productId: Long, val defaultName: String? = null) : Screen()

    @Stable data class AddProductCategory(val defaultName: String? = null) : Screen()

    @Stable data class AddProductProducer(val defaultName: String? = null) : Screen()

    @Stable data class AddShop(val defaultName: String? = null) : Screen()

    @Stable
    data class EditTransaction(
        val transactionId: Long,
        override val providedShopId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ) : Screen(), AcceptsShopId

    @Stable
    data class EditItem(
        val itemId: Long,
        override val providedProductId: @RawValue MutableState<Long?> = mutableStateOf(null),
        override val providedVariantId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ) : Screen(), AcceptsProductId

    @Stable
    data class EditProduct(
        val productId: Long,
        override val providedProducerId: @RawValue MutableState<Long?> = mutableStateOf(null),
        override val providedCategoryId: @RawValue MutableState<Long?> = mutableStateOf(null),
    ) : Screen(), AcceptsProducerId, AcceptsCategoryId

    @Stable data class EditProductVariant(val variantId: Long) : Screen()

    @Stable data class EditProductCategory(val categoryId: Long) : Screen()

    @Stable data class EditProductProducer(val producerId: Long) : Screen()

    @Stable data class EditShop(val shopId: Long) : Screen()

    @Immutable data object CategoryRanking : Screen()

    @Immutable data object ShopRanking : Screen()

    @Stable data class CategorySpendingComparison(val year: Int, val month: Int) : Screen()

    @Stable data class ShopSpendingComparison(val year: Int, val month: Int) : Screen()

    @Immutable data object Backups : Screen()
}

/** @return previous destination from the backstack, null if none exists */
fun <T> NavController<T>.previousDestination(): T? {
    if (backstack.entries.size == 1) return null

    return backstack.entries.let { it[it.lastIndex - 1].destination }
}

/** @return current destination from the backstack, null if none exists */
fun <T> NavController<T>.currentDestination(): T? {
    return backstack.entries.last().destination
}

/**
 * Replaces the backstack with itself after filtering it to contain only destinations matching the
 * given [predicate].
 */
fun <T> NavController<T>.replaceAllFilter(action: NavAction, predicate: (Screen) -> Boolean) where
T : Screen {
    setNewBackstack(
        entries =
            backstack.entries.map { it.destination }.filter { predicate(it) }.map { navEntry(it) },
        action = action,
    )
}

fun defaultNavigateContentTransformation(screenWidth: Int): ContentTransform {
    val easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)

    return slideInHorizontally(
        animationSpec = tween(500, easing = easing),
        initialOffsetX = { screenWidth },
    ) + fadeIn(tween(250, 50)) togetherWith
        slideOutHorizontally(
            animationSpec = tween(500, easing = easing),
            targetOffsetX = { -screenWidth },
        ) + fadeOut(tween(250, 50))
}

fun defaultPopContentTransformation(screenWidth: Int): ContentTransform {
    val easing = CubicBezierEasing(0.48f, 0.19f, 0.05f, 1.03f)

    return slideInHorizontally(
        animationSpec = tween(500, easing = easing),
        initialOffsetX = { -screenWidth },
    ) + fadeIn(tween(250, 50)) togetherWith
        slideOutHorizontally(
            animationSpec = tween(500, easing = easing),
            targetOffsetX = { screenWidth },
        ) + fadeOut(tween(250, 50))
}

@Composable
fun Navigation(isExpandedScreen: Boolean, navController: NavController<Screen>) {
    NavBackHandler(controller = navController)

    val navigateBack: () -> Unit = { navController.apply { if (backstack.entries.size > 1) pop() } }

    val navigateBackDeleteShop: (shopId: Long) -> Unit = { shopId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditShop(shopId) && it != Screen.DisplayShop(shopId)
        }
    }

    val navigateBackDeleteProductVariant: (variantId: Long) -> Unit = { variantId ->
        navController.replaceAllFilter(NavAction.Pop) { it != Screen.EditProductVariant(variantId) }
    }

    val navigateBackDeleteProduct: (productId: Long) -> Unit = { productId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditProduct(productId) && it != Screen.DisplayProduct(productId)
        }
    }

    val navigateBackDeleteProductCategory: (categoryId: Long) -> Unit = { categoryId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditProductCategory(categoryId) &&
                it != Screen.DisplayProductCategory(categoryId)
        }
    }

    val navigateBackDeleteProductProducer: (producerId: Long) -> Unit = { producerId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditProductProducer(producerId) &&
                it != Screen.DisplayProductProducer(producerId)
        }
    }

    val navigateBackDeleteItem: (itemId: Long) -> Unit = { itemId ->
        navController.replaceAllFilter(NavAction.Pop) { it != Screen.EditItem(itemId) }
    }

    val navigateBackDeleteTransaction: (transactionId: Long) -> Unit = { transactionId ->
        navController.replaceAllFilter(NavAction.Pop) {
            it != Screen.EditTransaction(transactionId) &&
                it != Screen.DisplayTransaction(transactionId)
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

    val navigateDisplayTransaction: (transactionId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.DisplayTransaction) {
            navController.navigate(Screen.DisplayTransaction(it))
        }
    }

    val navigateDisplayProduct: (productId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.DisplayProduct) {
            navController.navigate(Screen.DisplayProduct(it))
        }
    }

    val navigateDisplayProductCategory: (categoryId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.DisplayProductCategory) {
            navController.navigate(Screen.DisplayProductCategory(it))
        }
    }

    val navigateDisplayProductProducer: (producerId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.DisplayProductProducer) {
            navController.navigate(Screen.DisplayProductProducer(it))
        }
    }

    val navigateDisplayShop: (shopId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.DisplayShop) {
            navController.navigate(Screen.DisplayShop(it))
        }
    }

    val navigateAddTransaction: () -> Unit = {
        if (navController.currentDestination() !is Screen.AddTransaction) {
            navController.navigate(Screen.AddTransaction())
        }
    }

    val navigateAddItem: (transactionId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.AddItem) {
            navController.navigate(Screen.AddItem(it))
        }
    }

    val navigateAddProduct: (query: String?) -> Unit = {
        if (navController.currentDestination() !is Screen.AddProduct) {
            navController.navigate(Screen.AddProduct(it))
        }
    }

    val navigateAddProductVariant: (productId: Long, query: String?) -> Unit = { productId, query ->
        if (navController.currentDestination() !is Screen.AddProductVariant) {
            navController.navigate(Screen.AddProductVariant(productId, query))
        }
    }

    val navigateAddProductCategory: (query: String?) -> Unit = {
        if (navController.currentDestination() !is Screen.AddProductCategory) {
            navController.navigate(Screen.AddProductCategory(it))
        }
    }

    val navigateAddProductProducer: (query: String?) -> Unit = {
        if (navController.currentDestination() !is Screen.AddProductProducer) {
            navController.navigate(Screen.AddProductProducer(it))
        }
    }

    val navigateAddShop: (query: String?) -> Unit = {
        if (navController.currentDestination() !is Screen.AddShop) {
            navController.navigate(Screen.AddShop(it))
        }
    }

    val navigateEditTransaction: (transactionId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.EditTransaction) {
            navController.navigate(Screen.EditTransaction(it))
        }
    }

    val navigateEditItem: (itemId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.EditItem) {
            navController.navigate(Screen.EditItem(it))
        }
    }

    val navigateEditProduct: (productId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.EditProduct) {
            navController.navigate(Screen.EditProduct(it))
        }
    }

    val navigateEditProductVariant: (variantId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.EditProductVariant) {
            navController.navigate(Screen.EditProductVariant(it))
        }
    }

    val navigateEditProductCategory: (categoryId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.EditProductCategory) {
            navController.navigate(Screen.EditProductCategory(it))
        }
    }

    val navigateEditProductProducer: (producerId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.EditProductProducer) {
            navController.navigate(Screen.EditProductProducer(it))
        }
    }

    val navigateEditShop: (shopId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.EditShop) {
            navController.navigate(Screen.EditShop(it))
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
            navController.navigate(Screen.CategorySpendingComparison(year, month))
        }
    }

    val navigateShopSpendingComparison: (year: Int, month: Int) -> Unit = { year, month ->
        if (navController.currentDestination() !is Screen.ShopSpendingComparison) {
            navController.navigate(Screen.ShopSpendingComparison(year, month))
        }
    }

    val navigateBackups: () -> Unit = {
        if (navController.currentDestination() !is Screen.Backups) {
            navController.navigate(Screen.Backups)
        }
    }

    val screenWidth = LocalWindowInfo.current.containerSize.width

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
            is Screen.Home -> {
                HomeRoute(
                    navigateSettings = navigateSettings,
                    navigateSearch = navigateSearch,
                    navigateDisplayProduct = navigateDisplayProduct,
                    navigateDisplayProductCategory = navigateDisplayProductCategory,
                    navigateDisplayProductProducer = navigateDisplayProductProducer,
                    navigateDisplayShop = navigateDisplayShop,
                    navigateAddTransaction = navigateAddTransaction,
                    navigateEditTransaction = navigateEditTransaction,
                    navigateAddItem = {
                        navigateDisplayTransaction(it)
                        navigateAddItem(it)
                    },
                    navigateEditItem = navigateEditItem,
                    navigateCategoryRanking = navigateCategoryRanking,
                    navigateShopRanking = navigateShopRanking,
                    navigateCategorySpendingComparison = navigateCategorySpendingComparison,
                    navigateShopSpendingComparison = navigateShopSpendingComparison,
                )
            }

            is Screen.AddItem -> {
                AddItemRoute(
                    transactionId = screen.transactionId,
                    navigateBack = navigateBack,
                    navigateAddProduct = navigateAddProduct,
                    navigateAddProductVariant = navigateAddProductVariant,
                    navigateEditProduct = navigateEditProduct,
                    navigateEditProductVariant = navigateEditProductVariant,
                    providedProductId = screen.providedProductId.value,
                    providedVariantId = screen.providedVariantId.value,
                )
            }

            is Screen.AddProduct -> {
                AddProductRoute(
                    defaultName = screen.defaultName,
                    navigateBack = {
                        val previousDestination = navController.previousDestination()
                        if (
                            previousDestination != null && previousDestination is AcceptsProductId
                        ) {
                            previousDestination.provideProduct(it)
                        }
                        navigateBack()
                    },
                    navigateAddProductCategory = navigateAddProductCategory,
                    navigateAddProductProducer = navigateAddProductProducer,
                    navigateEditProductCategory = navigateEditProductCategory,
                    navigateEditProductProducer = navigateEditProductProducer,
                    providedProducerId = screen.providedProducerId.value,
                    providedCategoryId = screen.providedCategoryId.value,
                )
            }

            is Screen.AddProductVariant -> {
                AddProductVariantRoute(
                    productId = screen.productId,
                    defaultName = screen.defaultName,
                    navigateBack = {
                        val previousDestination = navController.previousDestination()
                        if (
                            previousDestination != null && previousDestination is AcceptsProductId
                        ) {
                            previousDestination.provideProduct(screen.productId, it)
                        }
                        navigateBack()
                    },
                )
            }

            is Screen.AddProductCategory -> {
                AddProductCategoryRoute(
                    defaultName = screen.defaultName,
                    navigateBack = {
                        val previousDestination = navController.previousDestination()
                        if (
                            previousDestination != null && previousDestination is AcceptsCategoryId
                        ) {
                            previousDestination.provideCategory(it)
                        }
                        navigateBack()
                    },
                )
            }

            is Screen.AddProductProducer -> {
                AddProductProducerRoute(
                    defaultName = screen.defaultName,
                    navigateBack = {
                        val previousDestination = navController.previousDestination()
                        if (
                            previousDestination != null && previousDestination is AcceptsProducerId
                        ) {
                            previousDestination.provideProducer(it)
                        }
                        navigateBack()
                    },
                )
            }

            is Screen.AddShop -> {
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
                    navigateDisplayProductCategory = navigateDisplayProductCategory,
                    navigateEditProductCategory = navigateEditProductCategory,
                )
            }

            is Screen.ShopRanking -> {
                ShopRankingRoute(
                    navigateBack = navigateBack,
                    navigateDisplayShop = navigateDisplayShop,
                    navigateEditShop = navigateEditShop,
                )
            }

            is Screen.DisplayProductCategory -> {
                DisplayProductCategoryRoute(
                    categoryId = screen.categoryId,
                    navigateBack = navigateBack,
                    navigateDisplayProduct = navigateDisplayProduct,
                    navigateDisplayProductProducer = navigateDisplayProductProducer,
                    navigateDisplayShop = navigateDisplayShop,
                    navigateEditItem = navigateEditItem,
                    navigateEditProductCategory = { navigateEditProductCategory(screen.categoryId) },
                )
            }

            is Screen.DisplayProductProducer -> {
                DisplayProductProducerRoute(
                    producerId = screen.producerId,
                    navigateBack = navigateBack,
                    navigateDisplayProduct = navigateDisplayProduct,
                    navigateDisplayProductCategory = navigateDisplayProductCategory,
                    navigateDisplayShop = navigateDisplayShop,
                    navigateEditItem = navigateEditItem,
                    navigateEditProductProducer = { navigateEditProductProducer(screen.producerId) },
                )
            }

            is Screen.DisplayProduct -> {
                DisplayProductRoute(
                    productId = screen.productId,
                    navigateBack = navigateBack,
                    navigateDisplayProductCategory = navigateDisplayProductCategory,
                    navigateDisplayProductProducer = navigateDisplayProductProducer,
                    navigateDisplayShop = navigateDisplayShop,
                    navigateEditItem = navigateEditItem,
                    navigateEditProduct = { navigateEditProduct(screen.productId) },
                )
            }

            is Screen.DisplayShop -> {
                DisplayShopRoute(
                    shopId = screen.shopId,
                    navigateBack = navigateBack,
                    navigateDisplayProduct = navigateDisplayProduct,
                    navigateDisplayProductCategory = navigateDisplayProductCategory,
                    navigateDisplayProductProducer = navigateDisplayProductProducer,
                    navigateEditItem = navigateEditItem,
                    navigateEditShop = { navigateEditShop(screen.shopId) },
                )
            }

            is Screen.EditShop -> {
                EditShopRoute(
                    shopId = screen.shopId,
                    navigateBack = { navigateBack() },
                    navigateBackDelete = { navigateBackDeleteShop(screen.shopId) },
                )
            }

            is Screen.EditProductVariant -> {
                EditProductVariantRoute(
                    variantId = screen.variantId,
                    navigateBack = { navigateBack() },
                    navigateBackDelete = { navigateBackDeleteProductVariant(screen.variantId) },
                )
            }

            is Screen.EditProduct -> {
                EditProductRoute(
                    productId = screen.productId,
                    navigateBack = navigateBack,
                    navigateBackDelete = { navigateBackDeleteProduct(screen.productId) },
                    navigateAddProductCategory = navigateAddProductCategory,
                    navigateAddProductProducer = navigateAddProductProducer,
                    navigateEditProductCategory = navigateEditProductCategory,
                    navigateEditProductProducer = navigateEditProductProducer,
                    providedProducerId = screen.providedProducerId.value,
                    providedCategoryId = screen.providedCategoryId.value,
                )
            }

            is Screen.EditProductCategory -> {
                EditProductCategoryRoute(
                    categoryId = screen.categoryId,
                    navigateBack = navigateBack,
                    navigateBackDelete = { navigateBackDeleteProductCategory(screen.categoryId) },
                )
            }

            is Screen.EditProductProducer -> {
                EditProductProducerRoute(
                    producerId = screen.producerId,
                    navigateBack = navigateBack,
                    navigateBackDelete = { navigateBackDeleteProductProducer(screen.producerId) },
                )
            }

            is Screen.EditItem -> {
                EditItemRoute(
                    itemId = screen.itemId,
                    navigateBack = navigateBack,
                    navigateBackDelete = { navigateBackDeleteItem(screen.itemId) },
                    navigateAddProduct = navigateAddProduct,
                    navigateAddProductVariant = navigateAddProductVariant,
                    navigateEditProduct = navigateEditProduct,
                    navigateEditProductVariant = navigateEditProductVariant,
                    providedProductId = screen.providedProductId.value,
                    providedVariantId = screen.providedVariantId.value,
                )
            }

            is Screen.Settings -> {
                SettingsRoute(navigateBack = navigateBack, navigateBackups = navigateBackups)
            }

            is Screen.Search -> {
                SearchRoute(
                    navigateBack = navigateBack,
                    navigateDisplayProduct = navigateDisplayProduct,
                    navigateDisplayProductCategory = navigateDisplayProductCategory,
                    navigateDisplayProductProducer = navigateDisplayProductProducer,
                    navigateDisplayShop = navigateDisplayShop,
                    navigateEditProduct = navigateEditProduct,
                    navigateEditProductCategory = navigateEditProductCategory,
                    navigateEditProductProducer = navigateEditProductProducer,
                    navigateEditShop = navigateEditShop,
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

            is Screen.AddTransaction -> {
                AddTransactionRoute(
                    isExpandedScreen = isExpandedScreen,
                    navigateBack = navigateBack,
                    navigateDisplayTransaction = navigateDisplayTransaction,
                    navigateAddShop = navigateAddShop,
                    navigateEditShop = navigateEditShop,
                    providedShopId = screen.providedShopId.value,
                )
            }

            is Screen.EditTransaction -> {
                EditTransactionRoute(
                    isExpandedScreen = isExpandedScreen,
                    transactionId = screen.transactionId,
                    navigateBack = navigateBack,
                    navigateBackDelete = navigateBackDeleteTransaction,
                    navigateAddShop = navigateAddShop,
                    navigateEditShop = navigateEditShop,
                    providedShopId = screen.providedShopId.value,
                )
            }

            is Screen.DisplayTransaction -> {
                DisplayTransactionRoute(
                    transactionId = screen.transactionId,
                    navigateBack = navigateBack,
                    navigateEditTransaction = { navigateEditTransaction(screen.transactionId) },
                    navigateAddItem = { navigateAddItem(screen.transactionId) },
                    navigateDisplayProduct = navigateDisplayProduct,
                    navigateEditItem = navigateEditItem,
                    navigateDisplayProductCategory = navigateDisplayProductCategory,
                    navigateDisplayProductProducer = navigateDisplayProductProducer,
                    navigateDisplayShop = navigateDisplayShop,
                )
            }

            is Screen.Backups -> {
                BackupsRoute(navigateBack = navigateBack)
            }
        }
    }
}
