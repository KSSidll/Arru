package com.kssidll.arru.ui.screen.modify.productproducer

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
fun ModifyProductProducerScreenImpl(
    uiState: ModifyProductProducerUiState,
    onEvent: (event: ModifyProductProducerEvent) -> Unit,
    modifier: Modifier = Modifier,
    submitButtonText: String = stringResource(id = R.string.item_product_producer_add),
) {
    val mergeCandidates =
        uiState.allProductProducers
            .filterNot { it.id == uiState.currentProductProducer?.id }
            .toImmutableList()

    ModifyScreen(
        onBack = { onEvent(ModifyProductProducerEvent.NavigateBack) },
        title = stringResource(id = R.string.item_product_producer),
        onSubmit = { onEvent(ModifyProductProducerEvent.Submit) },
        submitButtonText = submitButtonText,
        onDelete = { onEvent(ModifyProductProducerEvent.DeleteProductProducer) },
        isDeleteVisible = uiState.isDeleteEnabled,
        isDeleteWarningMessageVisible = uiState.isDangerousDeleteDialogVisible,
        onDeleteWarningMessageVisibleChange = {
            onEvent(ModifyProductProducerEvent.SetDangerousDeleteDialogVisibility(it))
        },
        deleteWarningMessage =
            stringResource(id = R.string.item_product_producer_delete_warning_text),
        isDeleteWarningConfirmed = uiState.isDangerousDeleteDialogConfirmed,
        onDeleteWarningConfirmedChange = {
            onEvent(ModifyProductProducerEvent.SetDangerousDeleteDialogConfirmation(it))
        },
        onMerge = {
            onEvent(ModifyProductProducerEvent.MergeProductProducer(uiState.selectedMergeCandidate))
        },
        isMergeVisible = uiState.isMergeEnabled && mergeCandidates.isNotEmpty(),
        isMergeSearchDialogVisible = uiState.isMergeSearchDialogVisible,
        onMergeSearchDialogVisibleChange = {
            onEvent(ModifyProductProducerEvent.SetMergeSearchDialogVisibility(it))
        },
        mergeSearchDialogCandidateTextTransformation = { it.name },
        isMergeConfirmVisible = uiState.isMergeConfirmationDialogVisible,
        onMergeConfirmVisibleChange = {
            onEvent(ModifyProductProducerEvent.SetMergeConfirmationDialogVisibility(it))
        },
        mergeConfirmMessage =
            stringResource(R.string.merge_action_message_template)
                .replace("{value_2}", uiState.selectedMergeCandidate?.name ?: "???")
                .replace("{value_1}", uiState.currentProductProducer?.name ?: "???"),
        mergeCandidates = mergeCandidates,
        onChosenMergeCandidateChange = {
            onEvent(ModifyProductProducerEvent.SelectMergeCandidate(it))
        },
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
                onValueChange = { onEvent(ModifyProductProducerEvent.SetName(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions =
                    KeyboardActions(onDone = { onEvent(ModifyProductProducerEvent.Submit) }),
                label = { Text(text = stringResource(R.string.item_product_producer)) },
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
private fun ModifyProductProducerScreenImplPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyProductProducerScreenImpl(uiState = ModifyProductProducerUiState(), onEvent = {})
        }
    }
}
