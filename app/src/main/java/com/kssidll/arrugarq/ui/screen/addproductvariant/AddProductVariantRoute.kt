package com.kssidll.arrugarq.ui.screen.addproductvariant

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductVariantRoute(
    productId: Long,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addProductVariantViewModel: AddProductVariantViewModel = hiltViewModel()

    AddProductVariantScreen(
        onBack = onBack,
        state = addProductVariantViewModel.addProductVariantScreenState,
        onVariantAdd = {
            scope.launch {
                val result = addProductVariantViewModel.addVariant(productId)
                if (result != null) onBack()
            }
        }
    )
}