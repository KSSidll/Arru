package com.kssidll.arru.ui.screen.modify.product.editproduct


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.modify.product.ModifyProductScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProductRoute(
    productId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: () -> Unit,
    navigateAddProductCategory: (query: String?) -> Unit,
    navigateAddProductProducer: (query: String?) -> Unit,
    navigateEditProductCategory: (categoryId: Long) -> Unit,
    navigateEditProductProducer: (producerId: Long) -> Unit,
    providedProducerId: Long?,
    providedCategoryId: Long?,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditProductViewModel = hiltViewModel()

    LaunchedEffect(productId) {
        if (!viewModel.updateState(productId)) {
            navigateBack()
        }
    }

    LaunchedEffect(providedProducerId) {
        viewModel.setSelectedProducer(providedProducerId)
    }

    LaunchedEffect(providedCategoryId) {
        viewModel.setSelectedCategory(providedCategoryId)
    }

    ModifyProductScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        categories = viewModel.allCategories().collectAsState(initial = emptyImmutableList()).value,
        producers = viewModel.allProducers().collectAsState(initial = emptyImmutableList()).value,
        onNewProducerSelected = {
            viewModel.onNewProducerSelected(it)
        },
        onNewCategorySelected = {
            viewModel.onNewCategorySelected(it)
        },
        onSubmit = {
            scope.launch {
                if (viewModel.updateProduct(productId)
                        .isNotError()
                ) {
                    navigateBack()
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteProduct(productId)
                        .isNotError()
                ) {
                    navigateBackDelete()
                }
            }
        },
        onMerge = {
            scope.launch {
                if (viewModel.mergeWith(it)
                        .isNotError()
                ) {
                    navigateBackDelete()
                }
            }
        },
        mergeCandidates = viewModel.allMergeCandidates(productId),
        mergeConfirmMessageTemplate = stringResource(id = R.string.merge_action_message_template)
            .replace(
                "{value_1}",
                viewModel.mergeMessageProductName
            ),

        chosenMergeCandidate = viewModel.chosenMergeCandidate.value,
        onChosenMergeCandidateChange = {
            viewModel.chosenMergeCandidate.apply { value = it }
        },
        showMergeConfirmDialog = viewModel.showMergeConfirmDialog.value,
        onShowMergeConfirmDialogChange = {
            viewModel.showMergeConfirmDialog.apply { value = it }
        },
        submitButtonText = stringResource(id = R.string.item_product_edit),
        onCategoryAddButtonClick = navigateAddProductCategory,
        onProducerAddButtonClick = navigateAddProductProducer,
        onItemCategoryLongClick = navigateEditProductCategory,
        onItemProducerLongClick = navigateEditProductProducer,
    )
}
