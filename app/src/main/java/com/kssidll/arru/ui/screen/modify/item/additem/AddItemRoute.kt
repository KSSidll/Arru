package com.kssidll.arru.ui.screen.modify.item.additem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ProvidedLongId
import com.kssidll.arru.ui.screen.modify.item.ModifyItemEvent
import com.kssidll.arru.ui.screen.modify.item.ModifyItemEventResult
import com.kssidll.arru.ui.screen.modify.item.ModifyItemScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun AddItemRoute(
    transactionId: Long,
    navigateBack: () -> Unit,
    navigateAddProduct: (query: String?) -> Unit,
    navigateAddProductVariant: (productId: Long, query: String?) -> Unit,
    navigateEditProduct: (productId: Long) -> Unit,
    navigateEditProductVariant: (variantId: Long) -> Unit,
    providedProductId: ProvidedLongId,
    providedProductVariantId: ProvidedLongId,
    viewModel: AddItemViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(transactionId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack()
            }
        }
    }

    LaunchedEffect(providedProductId, providedProductVariantId) {
        if (
            providedProductId is ProvidedLongId.Some &&
                providedProductVariantId is ProvidedLongId.Some
        ) {
            viewModel.handleEvent(ModifyItemEvent.SelectProductVariant(providedProductVariantId.id))
            viewModel.handleEvent(
                ModifyItemEvent.SelectProduct(providedProductId.id, providedProductVariantId.id)
            )
        } else {
            if (providedProductVariantId is ProvidedLongId.Some) {
                viewModel.handleEvent(
                    ModifyItemEvent.SelectProductVariant(providedProductVariantId.id)
                )
            }

            if (providedProductId is ProvidedLongId.Some) {
                viewModel.handleEvent(ModifyItemEvent.SelectProduct(providedProductId.id))
            }
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
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyItemEventResult.SuccessDelete &&
                                !navigateBackLock.isLocked
                        ) {
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
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyItemEventResult.SuccessInsert &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.lock()
                            navigateBack()
                        }
                    }
                }
            }
        },
    )
}
