package com.kssidll.arrugarq.ui.addproduct

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddProductRoute(
    onBack: () -> Unit,
) {
    val addProductViewModel: AddProductViewModel = hiltViewModel()

    AddProductScreen (
        onBack = onBack,
        onProductAdd = {
            addProductViewModel.addProduct(it)
        },
    )
}