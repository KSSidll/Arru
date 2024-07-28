package com.kssidll.arru

import android.os.Parcelable
import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalConfiguration
import com.kssidll.arru.ui.screen.backups.BackupsRoute
import com.kssidll.arru.ui.screen.home.HomeRoute
import dev.olshevski.navigation.reimagined.*
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

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

    @Stable
    data class Transaction(val transactionId: Long): Screen()

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

    @Immutable
    data object Backups: Screen()
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


    val navigateTransaction: (transactionId: Long) -> Unit = {
        if (navController.currentDestination() !is Screen.Transaction) {
            navController.navigate(Screen.Transaction(it))
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


    val navigateBackups: () -> Unit = {
        if (navController.currentDestination() !is Screen.Backups) {
            navController.navigate(Screen.Backups)
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
                    navigateTransactionAdd = navigateTransactionAdd,
                    navigateTransactionEdit = navigateTransactionEdit,
                    navigateItemAdd = {
                        navigateTransaction(it)
                        navigateItemAdd(it)
                    },
                    navigateItemEdit = navigateItemEdit,
                )
            }

            is Screen.ItemAdd -> {
                //                AddItemRoute(
                //                    transactionId = screen.transactionId,
                //                    navigateBack = navigateBack,
                //                    navigateProductAdd = navigateProductAdd,
                //                    navigateVariantAdd = navigateVariantAdd,
                //                    navigateProductEdit = navigateProductEdit,
                //                    navigateVariantEdit = navigateVariantEdit,
                //                    providedProductId = screen.providedProductId.value,
                //                    providedVariantId = screen.providedVariantId.value,
                //                )
            }


            is Screen.ItemEdit -> {
                //                EditItemRoute(
                //                    itemId = screen.itemId,
                //                    navigateBack = navigateBack,
                //                    navigateBackDelete = {
                //                        navigateBackDeleteItem(screen.itemId)
                //                    },
                //                    navigateProductAdd = navigateProductAdd,
                //                    navigateVariantAdd = navigateVariantAdd,
                //                    navigateProductEdit = navigateProductEdit,
                //                    navigateVariantEdit = navigateVariantEdit,
                //                    providedProductId = screen.providedProductId.value,
                //                    providedVariantId = screen.providedVariantId.value,
                //                )
            }

            is Screen.Settings -> {
                //                SettingsRoute(
                //                    navigateBack = navigateBack,
                //                    navigateBackups = navigateBackups,
                //                )
            }

            is Screen.TransactionAdd -> {
                //                AddTransactionRoute(
                //                    isExpandedScreen = isExpandedScreen,
                //                    navigateBack = navigateBack,
                //                    navigateTransaction = navigateTransaction,
                //                    navigateShopAdd = navigateShopAdd,
                //                    navigateShopEdit = navigateShopEdit,
                //                    providedShopId = screen.providedShopId.value,
                //                )
            }

            is Screen.TransactionEdit -> {
                //                EditTransactionRoute(
                //                    isExpandedScreen = isExpandedScreen,
                //                    transactionId = screen.transactionId,
                //                    navigateBack = navigateBack,
                //                    navigateBackDelete = navigateBackDeleteTransaction,
                //                    navigateShopAdd = navigateShopAdd,
                //                    navigateShopEdit = navigateShopEdit,
                //                    providedShopId = screen.providedShopId.value,
                //                )
            }

            is Screen.Transaction -> {
                //                TransactionRoute(
                //                    transactionId = screen.transactionId,
                //                    navigateBack = navigateBack,
                //                    navigateTransactionEdit = navigateTransactionEdit,
                //                    navigateItemAdd = navigateItemAdd,
                //                    navigateProduct = navigateProduct,
                //                    navigateItemEdit = navigateItemEdit,
                //                    navigateCategory = navigateCategory,
                //                    navigateProducer = navigateProducer,
                //                    navigateShop = navigateShop,
                //                )
            }

            is Screen.Backups -> {
                BackupsRoute(
                    navigateBack = navigateBack,
                )
            }
        }
    }
}

