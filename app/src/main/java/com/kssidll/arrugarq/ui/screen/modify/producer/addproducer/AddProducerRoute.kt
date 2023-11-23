package com.kssidll.arrugarq.ui.screen.modify.producer.addproducer

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.modify.producer.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProducerRoute(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddProducerViewModel = hiltViewModel()

    ModifyProducerScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addProducer()
                if (result != null) onBack()
            }
        }
    )
}