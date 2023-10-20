package com.kssidll.arrugarq.ui.screen.addproduct

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.shared.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductRoute(
    onBack: () -> Unit,
    onProductCategoryAdd: () -> Unit,
    onProductProducerAdd: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addProductViewModel: AddProductViewModel = hiltViewModel()

    EditProductScreen(
        onBack = onBack,
        state = addProductViewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = addProductViewModel.addProduct()
                if (result != null) onBack()
            }
        },
        onProducerAdd = onProductProducerAdd,
        onCategoryAdd = onProductCategoryAdd,
    )
}
