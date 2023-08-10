package com.kssidll.arrugarq.ui.addproductvariant

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.*

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