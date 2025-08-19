package com.kssidll.arru.ui.screen.modify.productcategory.editproductcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProductCategoryRoute(
    categoryId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: () -> Unit,
    viewModel: EditProductCategoryViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(categoryId) {
        if (!viewModel.updateState(categoryId)) {
            navigateBack()
        }
    }

    ModifyProductCategoryScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                if (viewModel.updateCategory(categoryId).isNotError()) {
                    navigateBack()
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteCategory(categoryId).isNotError()) {
                    navigateBackDelete()
                }
            }
        },
        onMerge = {
            scope.launch {
                if (viewModel.mergeWith(it).isNotError()) {
                    navigateBackDelete()
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
