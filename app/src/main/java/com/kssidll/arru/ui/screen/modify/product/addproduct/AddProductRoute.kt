package com.kssidll.arru.ui.screen.modify.product.addproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.modify.product.ModifyProductEvent
import com.kssidll.arru.ui.screen.modify.product.ModifyProductEventResult
import com.kssidll.arru.ui.screen.modify.product.ModifyProductScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddProductRoute(
    defaultName: String?,
    provideBack: (productId: Long?) -> Unit,
    navigateBack: () -> Unit,
    navigateAddProductCategory: (query: String?) -> Unit,
    navigateAddProductProducer: (query: String?) -> Unit,
    navigateEditProductCategory: (categoryId: Long) -> Unit,
    navigateEditProductProducer: (producerId: Long) -> Unit,
    providedProducerId: Long?,
    providedCategoryId: Long?,
    viewModel: AddProductViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.handleEvent(ModifyProductEvent.SetName(defaultName ?: String()))
    }

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
                    is ModifyProductEvent.DeleteProduct -> {}
                    is ModifyProductEvent.MergeProduct -> {}
                    is ModifyProductEvent.NavigateAddProductCategory ->
                        navigateAddProductCategory(event.name)
                    is ModifyProductEvent.NavigateAddProductProducer ->
                        navigateAddProductProducer(event.name)
                    is ModifyProductEvent.NavigateBack -> navigateBack()
                    is ModifyProductEvent.NavigateEditProductCategory ->
                        navigateEditProductCategory(event.productCategoryId)
                    is ModifyProductEvent.NavigateEditProductProducer ->
                        navigateEditProductProducer(event.productProducerId)
                    is ModifyProductEvent.SelectMergeCandidate -> {}
                    is ModifyProductEvent.SelectProductCategory -> viewModel.handleEvent(event)
                    is ModifyProductEvent.SelectProductProducer -> viewModel.handleEvent(event)
                    is ModifyProductEvent.SetDangerousDeleteDialogConfirmation -> {}
                    is ModifyProductEvent.SetDangerousDeleteDialogVisibility -> {}
                    is ModifyProductEvent.SetMergeConfirmationDialogVisibility -> {}
                    is ModifyProductEvent.SetMergeSearchDialogVisibility -> {}
                    is ModifyProductEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyProductEvent.SetProductCategorySearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductEvent.SetProductProducerSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (result is ModifyProductEventResult.SuccessInsert) {
                            provideBack(result.id)
                            navigateBack()
                        }
                    }
                }
            }
        },
    )
}
