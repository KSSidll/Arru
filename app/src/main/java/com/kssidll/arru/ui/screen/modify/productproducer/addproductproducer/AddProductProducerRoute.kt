package com.kssidll.arru.ui.screen.modify.productproducer.addproductproducer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerEvent
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerEventResult
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddProductProducerRoute(
    defaultName: String?,
    provideBack: (productProducerId: Long?) -> Unit,
    navigateBack: () -> Unit,
    viewModel: AddProductProducerViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(ModifyProductProducerEvent.SetName(defaultName ?: String()))
    }

    ModifyProductProducerScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyProductProducerEvent.NavigateBack -> navigateBack()
                    is ModifyProductProducerEvent.DeleteProductProducer -> {}
                    is ModifyProductProducerEvent.MergeProductProducer -> {}
                    is ModifyProductProducerEvent.SelectMergeCandidate -> {}
                    is ModifyProductProducerEvent.SetDangerousDeleteDialogConfirmation -> {}
                    is ModifyProductProducerEvent.SetDangerousDeleteDialogVisibility -> {}
                    is ModifyProductProducerEvent.SetMergeConfirmationDialogVisibility -> {}
                    is ModifyProductProducerEvent.SetMergeSearchDialogVisibility -> {}
                    is ModifyProductProducerEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyProductProducerEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (result is ModifyProductProducerEventResult.SuccessInsert) {
                            provideBack(result.id)
                            navigateBack()
                        }
                    }
                }
            }
        },
    )
}
