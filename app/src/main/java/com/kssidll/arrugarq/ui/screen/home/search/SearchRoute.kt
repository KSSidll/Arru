package com.kssidll.arrugarq.ui.screen.home.search


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun SearchRoute(
    navBackHandlerEnabled: Boolean,
    onProductSelect: (productId: Long) -> Unit,
    onProductEdit: (productId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onCategoryEdit: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onProducerEdit: (producerId: Long) -> Unit,
) {
    val viewModel: SearchViewModel = hiltViewModel()

    SearchScreen(
        state = viewModel.screenState,
        navBackHandlerEnabled = navBackHandlerEnabled,
        onProductSelect = onProductSelect,
        onProductEdit = onProductEdit,
        onShopSelect = onShopSelect,
        onShopEdit = onShopEdit,
        onCategorySelect = onCategorySelect,
        onCategoryEdit = onCategoryEdit,
        onProducerSelect = onProducerSelect,
        onProducerEdit = onProducerEdit,
    )
}
