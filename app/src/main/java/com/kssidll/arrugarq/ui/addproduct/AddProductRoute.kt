package com.kssidll.arrugarq.ui.addproduct

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddProductRoute(
    onBack: () -> Unit,
    onProductCategoryAdd: () -> Unit,
) {
    val addProductViewModel: AddProductViewModel = hiltViewModel()

    AddProductScreen (
        onBack = onBack,
        onCategoryAdd = onProductCategoryAdd,
        onProductAdd = {
            addProductViewModel.addProduct(it)
        },
        categories = addProductViewModel.getProductCategoriesFlow(),
        state = addProductViewModel.addProductState
    )
}