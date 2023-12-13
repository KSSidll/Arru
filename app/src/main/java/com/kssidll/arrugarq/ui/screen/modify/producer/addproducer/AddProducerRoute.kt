package com.kssidll.arrugarq.ui.screen.modify.producer.addproducer

import androidx.compose.runtime.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.producer.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddProducerRoute(
    defaultName: String?,
    navigateBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddProducerViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.screenState.name.value = Field.Loaded(defaultName)
    }

    ModifyProducerScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                if (viewModel.addProducer() != null) {
                    navigateBack()
                }
            }
        }
    )
}