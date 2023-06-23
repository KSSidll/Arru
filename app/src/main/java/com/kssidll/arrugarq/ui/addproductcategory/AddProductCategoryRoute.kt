package com.kssidll.arrugarq.ui.addproductcategory

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddProductCategoryRoute(
    onBack: () -> Unit,
    onProductCategoryTypeAdd: () -> Unit,
) {
    val addProductCategoryViewModel: AddProductCategoryViewModel = hiltViewModel()

    AddProductCategoryScreen(
        onBack = onBack,
        onCategoryTypeAdd = onProductCategoryTypeAdd,
        onCategoryAdd = {
            addProductCategoryViewModel.addProductCategory(it)
        },
        types = addProductCategoryViewModel.getProductCategoryTypesFlow(),
        state = addProductCategoryViewModel.addProductCategoryState
    )
}

