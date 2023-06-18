package com.kssidll.arrugarq

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kssidll.arrugarq.NavigationDestinations.ADD_ITEM_ROUTE
import com.kssidll.arrugarq.NavigationDestinations.HOME_ROUTE
import com.kssidll.arrugarq.NavigationDestinations.INITIAL_ROUTE
import com.kssidll.arrugarq.ui.additem.AddItemRoute
import com.kssidll.arrugarq.ui.home.HomeRoute

object NavigationDestinations {
    const val HOME_ROUTE = "home"
    const val ADD_ITEM_ROUTE = "additem"

    /**
     * the start route, shouldn't be used outside of NavHost startDestination
     *
     * shouldn't be any route that requires an argument
     *
     * use [navigateBase()] to navigate to actual main app screen
    */
    const val INITIAL_ROUTE = HOME_ROUTE
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController()
) {
    fun navigateHome() {
        navController.navigate(HOME_ROUTE)
    }

    fun navigateAddItem() {
        navController.navigate(ADD_ITEM_ROUTE)
    }

    /**
     * Use to navigate to main app screen
     */
    fun navigateBase() {
        navController.navigate(HOME_ROUTE)
    }

    NavHost(
        navController = navController,
        startDestination = INITIAL_ROUTE
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
                }
            )
        }
    }
}

