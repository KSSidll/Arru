package com.kssidll.arrugarq.ui.screen.addproductvariant

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.shared.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductVariantRoute(
    productId: Long,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addProductVariantViewModel: AddProductVariantViewModel = hiltViewModel()

    EditProductVariantScreen(
        onBack = onBack,
        state = addProductVariantViewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = addProductVariantViewModel.addVariant(productId)
                if (result != null) onBack()
            }
        }
    )
}