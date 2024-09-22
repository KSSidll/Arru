package com.kssidll.arru.ui.screen.modify.producer.addproducer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.producer.ModifyProducerScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddProducerRoute(
    defaultName: String?,
    navigateBack: (producerId: Long?) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddProducerViewModel = hiltViewModel()

    LaunchedEffect(Unit) {
        viewModel.screenState.name.value = Field.Loaded(defaultName)
    }

    ModifyProducerScreenImpl(
        onBack = {
            navigateBack(null)
        },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addProducer()
                if (result.isNotError()) {
                    navigateBack(result.id)
                }
            }
        }
    )
}