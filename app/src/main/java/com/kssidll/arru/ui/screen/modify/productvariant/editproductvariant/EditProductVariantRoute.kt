package com.kssidll.arru.ui.screen.modify.productvariant.editproductvariant

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantEvent
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantEventResult
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun EditProductVariantRoute(
    variantId: Long,
    navigateBack: (variantId: Long?) -> Unit,
    viewModel: EditProductVariantViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    BackHandler {
        if (!navigateBackLock.isLocked) {
            navigateBackLock.tryLock()
            navigateBack(variantId)
        }
    }

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(variantId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack(null)
            }
        }
    }

    LaunchedEffect(variantId) { viewModel.updateState(variantId) }

    ModifyProductVariantScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyProductVariantEvent.NavigateBack -> {
                        if (!navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            navigateBack(variantId)
                        }
                    }
                    is ModifyProductVariantEvent.DeleteProductVariant -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductVariantEventResult.SuccessDelete &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            navigateBack(null)
                        }
                    }
                    is ModifyProductVariantEvent.MergeProductVariant -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductVariantEventResult.SuccessMerge &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            navigateBack(result.id)
                        }
                    }
                    is ModifyProductVariantEvent.SelectMergeCandidate ->
                        viewModel.handleEvent(event)
                    is ModifyProductVariantEvent.SetDangerousDeleteDialogConfirmation ->
                        viewModel.handleEvent(event)
                    is ModifyProductVariantEvent.SetDangerousDeleteDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductVariantEvent.SetMergeConfirmationDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductVariantEvent.SetMergeSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductVariantEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyProductVariantEvent.SetIsVariantGlobal -> viewModel.handleEvent(event)
                    is ModifyProductVariantEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductVariantEventResult.SuccessUpdate &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            navigateBack(variantId)
                        }
                    }
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_variant_edit),
    )
}
