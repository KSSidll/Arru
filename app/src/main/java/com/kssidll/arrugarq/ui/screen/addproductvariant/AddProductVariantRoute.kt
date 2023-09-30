package com.kssidll.arrugarq.ui.screen.addproductvariant

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

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