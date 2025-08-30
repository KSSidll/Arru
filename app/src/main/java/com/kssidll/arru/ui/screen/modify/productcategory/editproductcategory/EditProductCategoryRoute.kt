package com.kssidll.arru.ui.screen.modify.productcategory.editproductcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryEvent
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryEventResult
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun EditProductCategoryRoute(
    categoryId: Long,
    navigateBack: (categoryId: Long?) -> Unit,
    viewModel: EditProductCategoryViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(categoryId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack(null)
            }
        }
    }

    LaunchedEffect(categoryId) { viewModel.updateState(categoryId) }

    ModifyProductCategoryScreenImpl(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyProductCategoryEvent.NavigateBack -> {
                        if (!navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            navigateBack(categoryId)
                        }
                    }
                    is ModifyProductCategoryEvent.DeleteProductCategory -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductCategoryEventResult.SuccessDelete &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            navigateBack(null)
                        }
                    }
                    is ModifyProductCategoryEvent.MergeProductCategory -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductCategoryEventResult.SuccessMerge &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            navigateBack(result.id)
                        }
                    }
                    is ModifyProductCategoryEvent.SelectMergeCandidate ->
                        viewModel.handleEvent(event)
                    is ModifyProductCategoryEvent.SetDangerousDeleteDialogConfirmation ->
                        viewModel.handleEvent(event)
                    is ModifyProductCategoryEvent.SetDangerousDeleteDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductCategoryEvent.SetMergeConfirmationDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductCategoryEvent.SetMergeSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyProductCategoryEvent.SetName -> viewModel.handleEvent(event)
                    is ModifyProductCategoryEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyProductCategoryEventResult.SuccessUpdate &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            navigateBack(categoryId)
                        }
                    }
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_product_category_edit),
    )
}
