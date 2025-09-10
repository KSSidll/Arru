package com.kssidll.arru.ui.screen.modify.product.editproduct

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.product.ModifyProductEvent
import com.kssidll.arru.ui.screen.modify.product.ModifyProductEventResult
import com.kssidll.arru.ui.screen.modify.product.ModifyProductScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun EditProductRoute(
    productId: Long,
    provideBack: (productId: Long?) -> Unit,
    navigateBack: () -> Unit,
    navigateAddProductCategory: (query: String?) -> Unit,
    navigateAddProductProducer: (query: String?) -> Unit,
    navigateEditProductCategory: (categoryId: Long) -> Unit,
    navigateEditProductProducer: (producerId: Long) -> Unit,
    providedProducerId: Long?,
    providedCategoryId: Long?,
    viewModel: EditProductViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    BackHandler {
        if (!navigateBackLock.isLocked) {
            navigateBackLock.tryLock()
            provideBack(productId)
            navigateBack()
        }
    }

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(productId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                provideBack(null)
                navigateBack()
            }
        }
    }

    LaunchedEffect(productId) { viewModel.updateState(productId) }

    LaunchedEffect(providedProducerId) {
        viewModel.handleEvent(ModifyProductEvent.SelectProductProducer(providedProducerId))
    }

    LaunchedEffect(providedCategoryId) {
        viewModel.handleEvent(ModifyProductEvent.SelectProductCategory(providedCategoryId))
    }

    ModifyProductScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyProductEvent.DeleteProduct -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductEventResult.SuccessDelete &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            provideBack(null)
                            navigateBack()
                        }
                    }
                    is ModifyProductEvent.MergeProduct -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductEventResult.SuccessMerge &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            provideBack(result.id)
                            navigateBack()
                        }
                    }
                    is ModifyProductEvent.NavigateAddProductCategory ->
                        navigateAddProductCategory(event.name)
                    is ModifyProductEvent.NavigateAddProductProducer ->
                        navigateAddProductProducer(event.name)
                    is ModifyProductEvent.NavigateBack -> {
                        if (!navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            provideBack(productId)
                            navigateBack()
                        }
                    }
                    is ModifyProductEvent.NavigateEditProductCategory ->
                        navigateEditProductCategory(event.productCategoryId)
                    is ModifyProductEvent.NavigateEditProductProducer ->
                        navigateEditProductProducer(event.productProducerId)
                    is ModifyProductEvent.SelectMergeCandidate -> viewModel.handleEvent(event)
                    is ModifyProductEvent.SelectProductCategory -> viewModel.handleEvent(event)
                    is ModifyProductEvent.SelectProductProducer -> viewModel.handleEvent(event)
                    is ModifyProductEvent.SetDangerousDeleteDialogConfirmation ->
                        viewModel.handleEvent(event)
                    is ModifyProductEvent.SetDangerousDeleteDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductEvent.SetMergeConfirmationDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductEvent.SetMergeSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyProductEvent.SetProductCategorySearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductEvent.SetProductProducerSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductEventResult.SuccessUpdate &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            provideBack(productId)
                            navigateBack()
                        }
                    }
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_edit),
    )
}
