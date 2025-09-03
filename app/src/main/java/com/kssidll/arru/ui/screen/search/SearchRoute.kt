package com.kssidll.arru.ui.screen.search

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.navController
import kotlinx.parcelize.Parcelize

/** Possible internal navigation destinations for [SearchScreen] */
@Parcelize
sealed class SearchDestinations : Parcelable {
    data object Start : SearchDestinations()

    data object ProductList : SearchDestinations()

    data object CategoryList : SearchDestinations()

    data object ShopList : SearchDestinations()

    data object ProducerList : SearchDestinations()
}

@Composable
fun SearchRoute(
    navigateBack: () -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditProduct: (productId: Long) -> Unit,
    navigateEditProductCategory: (categoryId: Long) -> Unit,
    navigateEditProductProducer: (producerId: Long) -> Unit,
    navigateEditShop: (shopId: Long) -> Unit,
    navController: NavController<SearchDestinations> = rememberSaveable {
        navController(startDestination = SearchDestinations.Start)
    },
) {
    SearchScreen(
        onBack = navigateBack,
        onProductClick = navigateDisplayProduct,
        onCategoryClick = navigateDisplayProductCategory,
        onProducerClick = navigateDisplayProductProducer,
        onShopClick = navigateDisplayShop,
        onProductLongClick = navigateEditProduct,
        onCategoryLongClick = navigateEditProductCategory,
        onProducerLongClick = navigateEditProductProducer,
        onShopLongClick = navigateEditShop,
        navController = navController,
    )
}
