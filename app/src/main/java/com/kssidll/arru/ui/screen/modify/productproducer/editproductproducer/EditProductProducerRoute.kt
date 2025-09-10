package com.kssidll.arru.ui.screen.modify.productproducer.editproductproducer

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerEvent
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerEventResult
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun EditProductProducerRoute(
    producerId: Long,
    provideBack: (producerId: Long?) -> Unit,
    navigateBack: () -> Unit,
    viewModel: EditProductProducerViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    BackHandler {
        if (!navigateBackLock.isLocked) {
            navigateBackLock.tryLock()
            provideBack(producerId)
            navigateBack()
        }
    }

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(producerId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                provideBack(null)
                navigateBack()
            }
        }
    }

    LaunchedEffect(producerId) { viewModel.updateState(producerId) }

    ModifyProductProducerScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyProductProducerEvent.NavigateBack -> {
                        if (!navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            provideBack(producerId)
                            navigateBack()
                        }
                    }
                    is ModifyProductProducerEvent.DeleteProductProducer -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductProducerEventResult.SuccessDelete &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            provideBack(null)
                            navigateBack()
                        }
                    }
                    is ModifyProductProducerEvent.MergeProductProducer -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductProducerEventResult.SuccessMerge &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            provideBack(result.id)
                            navigateBack()
                        }
                    }
                    is ModifyProductProducerEvent.SelectMergeCandidate ->
                        viewModel.handleEvent(event)
                    is ModifyProductProducerEvent.SetDangerousDeleteDialogConfirmation ->
                        viewModel.handleEvent(event)
                    is ModifyProductProducerEvent.SetDangerousDeleteDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductProducerEvent.SetMergeConfirmationDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductProducerEvent.SetMergeSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductProducerEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyProductProducerEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductProducerEventResult.SuccessUpdate &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            provideBack(producerId)
                            navigateBack()
                        }
                    }
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_producer_edit),
    )
}
