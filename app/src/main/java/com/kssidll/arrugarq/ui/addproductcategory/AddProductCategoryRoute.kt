package com.kssidll.arrugarq.ui.addproductcategory

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddProductCategoryRoute(
    onBack: () -> Unit,
) {
    val addProductCategoryViewModel: AddProductCategoryViewModel = hiltViewModel()

    AddProductCategoryScreen(
        onBack = onBack,
        onCategoryAdd = {
            addProductCategoryViewModel.addProductCategory(it)
        },
    )
}

