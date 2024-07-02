package com.kssidll.arru.ui.screen.search


import androidx.compose.runtime.Composable
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun SearchRoute(
    navigateBack: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateProductEdit: (productId: Long) -> Unit,
    navigateCategoryEdit: (categoryId: Long) -> Unit,
    navigateProducerEdit: (producerId: Long) -> Unit,
    navigateShopEdit: (shopId: Long) -> Unit,
) {
    val viewModel: SearchViewModel = hiltViewModel()

    SearchScreen(
        onBack = navigateBack,
        state = viewModel.screenState,
        onProductClick = navigateProduct,
        onCategoryClick = navigateCategory,
        onProducerClick = navigateProducer,
        onShopClick = navigateShop,
        onProductLongClick = navigateProductEdit,
        onCategoryLongClick = navigateCategoryEdit,
        onProducerLongClick = navigateProducerEdit,
        onShopLongClick = navigateShopEdit,
    )
}
