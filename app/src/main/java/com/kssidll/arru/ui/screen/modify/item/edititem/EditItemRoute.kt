package com.kssidll.arru.ui.screen.modify.item.edititem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.item.ModifyItemEvent
import com.kssidll.arru.ui.screen.modify.item.ModifyItemScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun EditItemRoute(
    itemId: Long,
    navigateBack: () -> Unit,
    navigateAddProduct: (query: String?) -> Unit,
    navigateAddProductVariant: (productId: Long, query: String?) -> Unit,
    navigateEditProduct: (productId: Long) -> Unit,
    navigateEditProductVariant: (variantId: Long) -> Unit,
    providedProductId: Long?,
    providedProductVariantId: Long?,
    viewModel: EditItemViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(itemId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack()
            }
        }
    }

    LaunchedEffect(itemId) { viewModel.updateState(itemId) }

    LaunchedEffect(providedProductId, providedProductVariantId) {
        providedProductId?.let { viewModel.handleEvent(ModifyItemEvent.SelectProduct(it)) }

        providedProductVariantId?.let {
            viewModel.handleEvent(ModifyItemEvent.SelectProductVariant(it))
        }
    }

    ModifyItemScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyItemEvent.DecrementPrice -> viewModel.handleEvent(event)
                    is ModifyItemEvent.DecrementQuantity -> viewModel.handleEvent(event)
                    is ModifyItemEvent.DeleteItem -> {
                        viewModel.handleEvent(event)
                        if (!navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            navigateBack()
                        }
                    }
                    is ModifyItemEvent.IncrementPrice -> viewModel.handleEvent(event)
                    is ModifyItemEvent.IncrementQuantity -> viewModel.handleEvent(event)
                    is ModifyItemEvent.NavigateAddProduct -> navigateAddProduct(event.name)
                    is ModifyItemEvent.NavigateAddProductVariant ->
                        navigateAddProductVariant(event.productVariantId, event.name)
                    is ModifyItemEvent.NavigateBack -> {
                        if (!navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            navigateBack()
                        }
                    }
                    is ModifyItemEvent.NavigateEditProduct -> navigateEditProduct(event.productId)
                    is ModifyItemEvent.NavigateEditProductVariant ->
                        navigateEditProductVariant(event.productVariantId)
                    is ModifyItemEvent.SelectProduct -> viewModel.handleEvent(event)
                    is ModifyItemEvent.SelectProductVariant -> viewModel.handleEvent(event)
                    is ModifyItemEvent.SetPrice -> viewModel.handleEvent(event)
                    is ModifyItemEvent.SetProductSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyItemEvent.SetProductVariantSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyItemEvent.SetQuantity -> viewModel.handleEvent(event)
                    is ModifyItemEvent.Submit -> {
                        if (viewModel.handleEvent(event) && !navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            navigateBack()
                        }
                    }
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_edit),
    )
}
