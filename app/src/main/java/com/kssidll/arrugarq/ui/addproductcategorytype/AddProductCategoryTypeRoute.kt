package com.kssidll.arrugarq.ui.addproductcategorytype

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddProductCategoryTypeRoute(
    onBack: () -> Unit,
) {
    val addProductCategoryTypeViewModel: AddProductCategoryTypeViewModel = hiltViewModel()

    AddProductCategoryTypeScreen(
        onBack = onBack,
        onTypeAdd = {
            addProductCategoryTypeViewModel.addType(it)
        }
    )
}