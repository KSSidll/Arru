package com.kssidll.arrugarq.ui.screen.addproductproducer

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.shared.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductProducerRoute(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addProductProducerViewModel: AddProductProducerViewModel = hiltViewModel()

    EditProductProducerScreenImpl(
        onBack = onBack,
        state = addProductProducerViewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = addProductProducerViewModel.addProducer()
                if (result != null) onBack()
            }
        }
    )
}