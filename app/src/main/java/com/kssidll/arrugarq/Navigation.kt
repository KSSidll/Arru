package com.kssidll.arrugarq

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kssidll.arrugarq.NavigationDestinations.ADD_ITEM_ROUTE
import com.kssidll.arrugarq.NavigationDestinations.ADD_PRODUCT_ROUTE
import com.kssidll.arrugarq.NavigationDestinations.ADD_SHOP_ROUTE
import com.kssidll.arrugarq.NavigationDestinations.HOME_ROUTE
import com.kssidll.arrugarq.ui.additem.AddItemRoute
import com.kssidll.arrugarq.ui.addproduct.AddProductRoute
import com.kssidll.arrugarq.ui.addshop.AddShopRoute
import com.kssidll.arrugarq.ui.home.HomeRoute

object NavigationDestinations {
    const val HOME_ROUTE = "home"
    const val ADD_ITEM_ROUTE = "additem"
    const val ADD_PRODUCT_ROUTE = "addproduct"
    const val ADD_SHOP_ROUTE = "addshop"
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
) {
    // the navigation functions are abstracted to keep consistency
    // as we prefer having functions for routes that require arguments
    // so might as well have functions for routes that don't
    fun navigateHome() {
        navController.navigate(HOME_ROUTE)
    }

    fun navigateAddItem() {
        navController.navigate(ADD_ITEM_ROUTE)
    }

    fun navigateAddProduct() {
        navController.navigate(ADD_PRODUCT_ROUTE)
    }

    fun navigateAddShop() {
        navController.navigate(ADD_SHOP_ROUTE)
    }

    /**
     * Use to navigate to main app screen
     */
    fun navigateBase() {
        navController.navigate(HOME_ROUTE)
    }

    NavHost(
        navController = navController,
        startDestination = HOME_ROUTE
    ) {
        composable(HOME_ROUTE) {
            HomeRoute(
                onAddItem = {
                    navigateAddItem()
                }
            )
        }

        composable(ADD_ITEM_ROUTE) {
            AddItemRoute (
                onBack = {
                    navController.popBackStack()
                },
                onProductAdd = {
                    navigateAddProduct()
                },
                onShopAdd = {
                    navigateAddShop()
                },
            )
        }

        composable(ADD_PRODUCT_ROUTE) {
            AddProductRoute (
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(ADD_SHOP_ROUTE) {
            AddShopRoute (
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}

