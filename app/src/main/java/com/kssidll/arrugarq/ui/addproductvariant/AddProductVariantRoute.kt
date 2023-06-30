package com.kssidll.arrugarq.ui.addproductvariant

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddProductVariantRoute(
    productId: Long,
    onBack: () -> Unit,
) {
    val addProductVariantViewModel: AddProductVariantViewModel = hiltViewModel()

    AddProductVariantScreen(
        productId = productId,
        onBack = onBack,
        onVariantAdd = {
            addProductVariantViewModel.addVariant(it)
        }
    )
}