package com.kssidll.arru.ui.screen.modify.shop.editshop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun EditShopRoute(
    shopId: Long,
    navigateBack: (shopId: Long?) -> Unit,
    viewModel: EditShopViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(shopId)) {
                navigateBack(null)
            }
        }
    }

    LaunchedEffect(shopId) { viewModel.updateState(shopId) }

    ModifyShopScreenImpl(
        onBack = { navigateBack(shopId) },
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                if (viewModel.updateShop(shopId).isNotError()) {
                    navigateBack(shopId)
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteShop(shopId).isNotError()) {
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
        mergeCandidates = viewModel.allMergeCandidates(shopId),
        mergeConfirmMessageTemplate =
            stringResource(id = R.string.merge_action_message_template)
                .replace("{value_1}", viewModel.mergeMessageShopName),
        chosenMergeCandidate = viewModel.chosenMergeCandidate.value,
        onChosenMergeCandidateChange = { viewModel.chosenMergeCandidate.apply { value = it } },
        showMergeConfirmDialog = viewModel.showMergeConfirmDialog.value,
        onShowMergeConfirmDialogChange = { viewModel.showMergeConfirmDialog.apply { value = it } },
        submitButtonText = stringResource(id = R.string.item_shop_edit),
    )
}
