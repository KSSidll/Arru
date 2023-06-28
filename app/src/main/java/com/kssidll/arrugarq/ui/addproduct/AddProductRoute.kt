package com.kssidll.arrugarq.ui.addproduct

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddProductRoute(
    onBack: () -> Unit,
    onProductCategoryAdd: () -> Unit,
    onProductProducerAdd: () -> Unit,
) {
    val addProductViewModel: AddProductViewModel = hiltViewModel()

    AddProductScreen (
        onBack = onBack,
        onCategoryAdd = onProductCategoryAdd,
        onProducerAdd = onProductProducerAdd,
        onProductAdd = {
            addProductViewModel.addProduct(it)
        },
        categoriesWithAltNames = addProductViewModel.getProductCategoriesWithAltNamesFlow(),
        producers = addProductViewModel.getProductProducersFlow(),
        state = addProductViewModel.addProductState
    )
}
