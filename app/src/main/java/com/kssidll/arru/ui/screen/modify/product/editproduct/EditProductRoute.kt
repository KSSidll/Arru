package com.kssidll.arru.ui.screen.modify.product.editproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
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
    navigateBack: (productId: Long?) -> Unit,
    navigateAddProductCategory: (query: String?) -> Unit,
    navigateAddProductProducer: (query: String?) -> Unit,
    navigateEditProductCategory: (categoryId: Long) -> Unit,
    navigateEditProductProducer: (producerId: Long) -> Unit,
    providedProducerId: Long?,
    providedCategoryId: Long?,
    viewModel: EditProductViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(productId)) {
                navigateBack(null)
            }
        }
    }

    LaunchedEffect(productId) { viewModel.updateState(productId) }

    LaunchedEffect(providedProducerId) { viewModel.setSelectedProducer(providedProducerId) }

    LaunchedEffect(providedCategoryId) { viewModel.setSelectedCategory(providedCategoryId) }

    ModifyProductScreenImpl(
        onBack = { navigateBack(productId) },
        state = viewModel.screenState,
        categories = viewModel.allCategories().collectAsState(initial = emptyImmutableList()).value,
        producers = viewModel.allProducers().collectAsState(initial = emptyImmutableList()).value,
        onNewProducerSelected = { viewModel.onNewProducerSelected(it) },
        onNewCategorySelected = { viewModel.onNewCategorySelected(it) },
        onSubmit = {
            scope.launch {
                if (viewModel.updateProduct(productId)) {
                    navigateBack(productId)
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteProduct(productId)) {
                    navigateBack(null)
                }
            }
        },
        onMerge = {
            scope.launch {
                val new = viewModel.mergeWith(it)
                navigateBack(new?.id)
            }
        },
        mergeCandidates = viewModel.allMergeCandidates(productId),
        mergeConfirmMessageTemplate =
            stringResource(id = R.string.merge_action_message_template)
                .replace("{value_1}", viewModel.mergeMessageProductName),
        chosenMergeCandidate = viewModel.chosenMergeCandidate.value,
        onChosenMergeCandidateChange = { viewModel.chosenMergeCandidate.apply { value = it } },
        showMergeConfirmDialog = viewModel.showMergeConfirmDialog.value,
        onShowMergeConfirmDialogChange = { viewModel.showMergeConfirmDialog.apply { value = it } },
        submitButtonText = stringResource(id = R.string.item_product_edit),
        onCategoryAddButtonClick = navigateAddProductCategory,
        onProducerAddButtonClick = navigateAddProductProducer,
        onItemCategoryLongClick = navigateEditProductCategory,
        onItemProducerLongClick = navigateEditProductProducer,
    )
}
