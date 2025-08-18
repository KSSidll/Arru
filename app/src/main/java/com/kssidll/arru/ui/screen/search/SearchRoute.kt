package com.kssidll.arru.ui.screen.search

import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

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
) {
    val viewModel: SearchViewModel = hiltViewModel()

    SearchScreen(
        onBack = navigateBack,
        state = viewModel.screenState,
        onProductClick = navigateDisplayProduct,
        onCategoryClick = navigateDisplayProductCategory,
        onProducerClick = navigateDisplayProductProducer,
        onShopClick = navigateDisplayShop,
        onProductLongClick = navigateEditProduct,
        onCategoryLongClick = navigateEditProductCategory,
        onProducerLongClick = navigateEditProductProducer,
        onShopLongClick = navigateEditShop,
    )
}
