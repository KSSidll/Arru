package com.kssidll.arrugarq.ui.additem

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddItemRoute(
    onBack: () -> Unit,
    onProductAdd: () -> Unit,
    onShopAdd: () -> Unit,
) {
    val addItemViewModel: AddItemViewModel = hiltViewModel()

    AddItemScreen (
        onBack = onBack,
        onItemAdd = {
            addItemViewModel.addItem(it)
        },
        onProductAdd = onProductAdd,
        onShopAdd = onShopAdd,
        products = addItemViewModel.getProductsFlow(),
        shops = addItemViewModel.getShopsFlow(),
        state = addItemViewModel.addItemState,
    )
}