package com.kssidll.arrugarq.ui.screen.modify.producer.editproducer


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.modify.producer.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditProducerRoute(
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

    ModifyProducerScreenImpl(
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
