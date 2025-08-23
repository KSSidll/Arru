package com.kssidll.arru.ui.screen.modify.productproducer.editproductproducer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun EditProductProducerRoute(
    producerId: Long,
    navigateBack: (producerId: Long?) -> Unit,
    viewModel: EditProducerViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(producerId)) {
                navigateBack(null)
            }
        }
    }

    LaunchedEffect(producerId) { viewModel.updateState(producerId) }

    ModifyProductProducerScreenImpl(
        onBack = { navigateBack(producerId) },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                if (viewModel.updateProducer(producerId).isNotError()) {
                    navigateBack(producerId)
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteProducer(producerId).isNotError()) {
                    navigateBack(null)
                }
            }
        },
        onMerge = {
            scope.launch {
                if (viewModel.mergeWith(it).isNotError()) {
                    navigateBack(it.id)
                }
            }
        },
        mergeCandidates = viewModel.allMergeCandidates(producerId),
        mergeConfirmMessageTemplate =
            stringResource(id = R.string.merge_action_message_template)
                .replace("{value_1}", viewModel.mergeMessageProducerName),
        chosenMergeCandidate = viewModel.chosenMergeCandidate.value,
        onChosenMergeCandidateChange = { viewModel.chosenMergeCandidate.apply { value = it } },
        showMergeConfirmDialog = viewModel.showMergeConfirmDialog.value,
        onShowMergeConfirmDialogChange = { viewModel.showMergeConfirmDialog.apply { value = it } },
        submitButtonText = stringResource(id = R.string.item_product_producer_edit),
    )
}
