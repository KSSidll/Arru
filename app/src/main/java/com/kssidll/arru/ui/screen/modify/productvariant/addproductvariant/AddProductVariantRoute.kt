package com.kssidll.arru.ui.screen.modify.productvariant.addproductvariant

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantEvent
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantEventResult
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun AddProductVariantRoute(
    productId: Long,
    defaultName: String?,
    navigateBack: (productVariantId: Long?) -> Unit,
    viewModel: AddProductVariantViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    SideEffect {
        scope.launch {
            if (!viewModel.setAndCheckProduct(productId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack(null)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.handleEvent(ModifyProductVariantEvent.SetName(defaultName ?: String()))
    }

    ModifyProductVariantScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyProductVariantEvent.NavigateBack -> {
                        if (!navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            navigateBack(null)
                        }
                    }
                    is ModifyProductVariantEvent.DeleteProductVariant -> {}
                    is ModifyProductVariantEvent.MergeProductVariant -> {}
                    is ModifyProductVariantEvent.SelectMergeCandidate -> {}
                    is ModifyProductVariantEvent.SetDangerousDeleteDialogConfirmation -> {}
                    is ModifyProductVariantEvent.SetDangerousDeleteDialogVisibility -> {}
                    is ModifyProductVariantEvent.SetMergeConfirmationDialogVisibility -> {}
                    is ModifyProductVariantEvent.SetMergeSearchDialogVisibility -> {}
                    is ModifyProductVariantEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyProductVariantEvent.SetIsVariantGlobal -> viewModel.handleEvent(event)
                    is ModifyProductVariantEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (result is ModifyProductVariantEventResult.SuccessInsert) {
                            navigateBack(result.id)
                        }
                    }
                }
            }
        },
    )
}
