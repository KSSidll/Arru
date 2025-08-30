package com.kssidll.arru.ui.screen.modify.shop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.R
import com.kssidll.arru.ui.component.field.StyledOutlinedTextField
import com.kssidll.arru.ui.screen.modify.ModifyScreen
import com.kssidll.arru.ui.theme.ArruTheme
import kotlinx.collections.immutable.toImmutableList

private val ItemHorizontalPadding: Dp = 20.dp

@Composable
fun ModifyShopScreenImpl(
    uiState: ModifyShopUiState,
    onEvent: (event: ModifyShopEvent) -> Unit,
    modifier: Modifier = Modifier,
    submitButtonText: String = stringResource(id = R.string.item_shop_add),
) {
    val mergeCandidates =
        uiState.allShops.filterNot { it.id == uiState.currentShop?.id }.toImmutableList()

    ModifyScreen(
        onBack = { onEvent(ModifyShopEvent.NavigateBack) },
        title = stringResource(id = R.string.item_shop),
        onSubmit = { onEvent(ModifyShopEvent.Submit) },
        submitButtonText = submitButtonText,
        onDelete = { onEvent(ModifyShopEvent.DeleteShop) },
        isDeleteVisible = uiState.isDeleteEnabled,
        isDeleteWarningMessageVisible = uiState.isDangerousDeleteDialogVisible,
        onDeleteWarningMessageVisibleChange = {
            onEvent(ModifyShopEvent.SetDangerousDeleteDialogVisibility(it))
        },
        deleteWarningMessage = stringResource(id = R.string.item_shop_delete_warning_text),
        isDeleteWarningConfirmed = uiState.isDangerousDeleteDialogConfirmed,
        onDeleteWarningConfirmedChange = {
            onEvent(ModifyShopEvent.SetDangerousDeleteDialogConfirmation(it))
        },
        onMerge = { onEvent(ModifyShopEvent.MergeShop(uiState.selectedMergeCandidate)) },
        isMergeVisible = uiState.isMergeEnabled && mergeCandidates.isNotEmpty(),
        isMergeSearchDialogVisible = uiState.isMergeSearchDialogVisible,
        onMergeSearchDialogVisibleChange = {
            onEvent(ModifyShopEvent.SetMergeSearchDialogVisibility(it))
        },
        mergeSearchDialogCandidateTextTransformation = { it.name },
        isMergeConfirmVisible = uiState.isMergeConfirmationDialogVisible,
        onMergeConfirmVisibleChange = {
            onEvent(ModifyShopEvent.SetMergeConfirmationDialogVisibility(it))
        },
        mergeConfirmMessage =
            stringResource(R.string.merge_action_message_template)
                .replace("{value_2}", uiState.selectedMergeCandidate?.name ?: "???")
                .replace("{value_1}", uiState.currentShop?.name ?: "???"),
        mergeCandidates = mergeCandidates,
        onChosenMergeCandidateChange = { onEvent(ModifyShopEvent.SelectMergeCandidate(it)) },
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 500.dp),
        ) {
            StyledOutlinedTextField(
                enabled = uiState.name.isLoading(),
                singleLine = true,
                value = uiState.name.data,
                onValueChange = { onEvent(ModifyShopEvent.SetName(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onEvent(ModifyShopEvent.Submit) }),
                label = { Text(text = stringResource(R.string.item_shop)) },
                supportingText = { uiState.name.error?.ErrorText() },
                isError = uiState.name.isError(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
            )
        }
    }
}

@PreviewLightDark
@ExpandedPreviews
@Composable
private fun ModifyShopScreenImplPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyShopScreenImpl(uiState = ModifyShopUiState(), onEvent = {})
        }
    }
}
