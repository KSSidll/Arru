package com.kssidll.arru.ui.screen.modify.productproducer.editproductproducer


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProductProducerRoute(
    producerId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditProducerViewModel = hiltViewModel()

    LaunchedEffect(producerId) {
        if (!viewModel.updateState(producerId)) {
            navigateBack()
        }
    }

    ModifyProductProducerScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                if (viewModel.updateProducer(producerId)
                        .isNotError()
                ) {
                    navigateBack()
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteProducer(producerId)
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
        mergeCandidates = viewModel.allMergeCandidates(producerId),
        mergeConfirmMessageTemplate = stringResource(id = R.string.merge_action_message_template)
            .replace(
                "{value_1}",
                viewModel.mergeMessageProducerName
            ),

        chosenMergeCandidate = viewModel.chosenMergeCandidate.value,
        onChosenMergeCandidateChange = {
            viewModel.chosenMergeCandidate.apply { value = it }
        },
        showMergeConfirmDialog = viewModel.showMergeConfirmDialog.value,
        onShowMergeConfirmDialogChange = {
            viewModel.showMergeConfirmDialog.apply { value = it }
        },
        submitButtonText = stringResource(id = R.string.item_product_producer_edit),
    )
}
