package com.kssidll.arrugarq

import android.os.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.*
import com.kssidll.arrugarq.ui.screen.additem.*
import com.kssidll.arrugarq.ui.screen.addproduct.*
import com.kssidll.arrugarq.ui.screen.addproductcategory.*
import com.kssidll.arrugarq.ui.screen.addproductproducer.*
import com.kssidll.arrugarq.ui.screen.addproductvariant.*
import com.kssidll.arrugarq.ui.screen.addshop.*
import com.kssidll.arrugarq.ui.screen.home.*
import dev.olshevski.navigation.reimagined.*
import kotlinx.parcelize.*

@Parcelize
sealed class Screen: Parcelable {
    data object Home: Screen()
    data object AddItem: Screen()
    data object AddProduct: Screen()
    data class AddProductVariant(val productId: Long): Screen()
    data object AddProductCategory: Screen()
    data object AddProductProducer: Screen()
    data object AddShop: Screen()
    data object AddFilterGroup: Screen()
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
                    }
                )
            }

            is Screen.AddItem -> {
                AddItemRoute(
                    onBack = onBack,
                    onProductAdd = {
                        navController.navigate(Screen.AddProduct)
                    },
                    onVariantAdd = { productId ->
                        navController.navigate(Screen.AddProductVariant(productId = productId))
                    },
                    onShopAdd = {
                        navController.navigate(Screen.AddShop)
                    },
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
                AddProductVariantRoute(
                    productId = screen.productId,
                    onBack = onBack,
                )
            }

            is Screen.AddProductCategory -> {
                AddProductCategoryRoute(
                    onBack = onBack,
                )
            }

            is Screen.AddProductProducer -> {
                AddProductProducerRoute(
                    onBack = onBack,
                )
            }

            is Screen.AddShop -> {
                AddShopRoute(
                    onBack = onBack,
                )
            }

            is Screen.AddFilterGroup -> {

            }

        }
    }
}

