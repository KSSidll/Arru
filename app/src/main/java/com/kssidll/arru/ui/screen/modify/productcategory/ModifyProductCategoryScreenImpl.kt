package com.kssidll.arru.ui.screen.modify.productcategory

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
fun ModifyProductCategoryScreenImpl(
    uiState: ModifyProductCategoryUiState,
    onEvent: (event: ModifyProductCategoryEvent) -> Unit,
    modifier: Modifier = Modifier,
    submitButtonText: String = stringResource(id = R.string.item_product_category_add),
) {
    ModifyScreen(
        onBack = { onEvent(ModifyProductCategoryEvent.NavigateBack) },
        title = stringResource(id = R.string.item_product_category),
        onSubmit = { onEvent(ModifyProductCategoryEvent.Submit) },
        submitButtonText = submitButtonText,
        onDelete = { onEvent(ModifyProductCategoryEvent.DeleteProductCategory) },
        isDeleteVisible = uiState.isDeleteVisible,
        isDeleteWarningMessageVisible = uiState.isDangerousDeleteDialogVisible,
        onDeleteWarningMessageVisibleChange = {
            onEvent(ModifyProductCategoryEvent.SetDangerousDeleteDialogVisibility(it))
        },
        deleteWarningMessage =
            stringResource(id = R.string.item_product_category_delete_warning_text),
        isDeleteWarningConfirmed = uiState.isDangerousDeleteDialogConfirmed,
        onDeleteWarningConfirmedChange = {
            onEvent(ModifyProductCategoryEvent.SetDangerousDeleteDialogConfirmation(it))
        },
        onMerge = {
            onEvent(ModifyProductCategoryEvent.MergeProductCategory(uiState.selectedMergeCandidate))
        },
        isMergeVisible = uiState.isMergeVisible,
        isMergeSearchDialogVisible = uiState.isMergeSearchDialogVisible,
        onMergeSearchDialogVisibleChange = {
            onEvent(ModifyProductCategoryEvent.SetMergeSearchDialogVisibility(it))
        },
        mergeSearchDialogCandidateTextTransformation = { it.name },
        isMergeConfirmVisible = uiState.isMergeConfirmationDialogVisible,
        onMergeConfirmVisibleChange = {
            onEvent(ModifyProductCategoryEvent.SetMergeConfirmationDialogVisibility(it))
        },
        mergeConfirmMessage =
            stringResource(R.string.merge_action_message_template)
                .replace("{value_2", uiState.selectedMergeCandidate?.name ?: "???")
                .replace("{value_1", uiState.currentProductCategory?.name ?: "???"),
        mergeCandidates =
            uiState.allProductCategories
                .filterNot { it.id == uiState.currentProductCategory?.id }
                .toImmutableList(),
        onChosenMergeCandidateChange = {
            onEvent(ModifyProductCategoryEvent.SelectMergeCandidate(it))
        },
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 500.dp),
        ) {
            StyledOutlinedTextField(
                enabled = uiState.name.isEnabled(),
                singleLine = true,
                value = uiState.name.data ?: String(),
                onValueChange = { onEvent(ModifyProductCategoryEvent.SetName(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions =
                    KeyboardActions(onDone = { onEvent(ModifyProductCategoryEvent.Submit) }),
                label = { Text(text = stringResource(R.string.item_product_category)) },
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
private fun ModifyProductCategoryScreenImplPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyProductCategoryScreenImpl(uiState = ModifyProductCategoryUiState(), onEvent = {})
        }
    }
}
