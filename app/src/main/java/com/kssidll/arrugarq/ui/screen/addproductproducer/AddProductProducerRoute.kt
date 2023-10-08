package com.kssidll.arrugarq.ui.screen.addproductproducer

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProductProducerRoute(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addProductProducerViewModel: AddProductProducerViewModel = hiltViewModel()

    AddProductProducerScreen(
        onBack = onBack,
        state = addProductProducerViewModel.addProductProducerScreenState,
        onProducerAdd = {
            scope.launch {
                val result = addProductProducerViewModel.addProducer()
                if (result != null) onBack()
            }
        }
    )
}