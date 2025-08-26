package com.kssidll.arru.ui.screen.modify.productcategory.editproductcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
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
        onBack = {
            if (!navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack(categoryId)
            }
        },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                if (viewModel.updateCategory(categoryId) && !navigateBackLock.isLocked) {
                    navigateBackLock.tryLock()
                    navigateBack(categoryId)
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteCategory(categoryId) && !navigateBackLock.isLocked) {
                    navigateBackLock.tryLock()
                    navigateBack(null)
                }
            }
        },
        onMerge = {
            scope.launch {
                val new = viewModel.mergeWith(it)
                if (!navigateBackLock.isLocked) {
                    navigateBackLock.tryLock()
                    navigateBack(new?.id)
                }
            }
        },
        mergeCandidates = viewModel.allMergeCandidates(categoryId),
        mergeConfirmMessageTemplate =
            stringResource(id = R.string.merge_action_message_template)
                .replace("{value_1}", viewModel.mergeMessageCategoryName),
        chosenMergeCandidate = viewModel.chosenMergeCandidate.value,
        onChosenMergeCandidateChange = { viewModel.chosenMergeCandidate.apply { value = it } },
        showMergeConfirmDialog = viewModel.showMergeConfirmDialog.value,
        onShowMergeConfirmDialogChange = { viewModel.showMergeConfirmDialog.apply { value = it } },
        submitButtonText = stringResource(id = R.string.item_product_category_edit),
    )
}
