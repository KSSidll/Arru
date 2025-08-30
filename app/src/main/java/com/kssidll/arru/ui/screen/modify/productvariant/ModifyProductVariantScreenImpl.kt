package com.kssidll.arru.ui.screen.modify.productvariant

import android.R.attr.data
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.kssidll.arru.ui.theme.optionalAlpha
import kotlinx.collections.immutable.toImmutableList

private val ItemHorizontalPadding: Dp = 20.dp

@Composable
fun ModifyProductVariantScreenImpl(
    uiState: ModifyProductVariantUiState,
    onEvent: (event: ModifyProductVariantEvent) -> Unit,
    modifier: Modifier = Modifier,
    submitButtonText: String = stringResource(id = R.string.item_product_variant_add),
) {
    val isGlobalVariantInteractionSource = remember { MutableInteractionSource() }

    ModifyScreen(
        onBack = { onEvent(ModifyProductVariantEvent.NavigateBack) },
        title = stringResource(id = R.string.item_product_variant),
        onSubmit = { onEvent(ModifyProductVariantEvent.Submit) },
        submitButtonText = submitButtonText,
        onDelete = { onEvent(ModifyProductVariantEvent.DeleteProductVariant) },
        isDeleteVisible = uiState.isDeleteVisible,
        isDeleteWarningMessageVisible = uiState.isDangerousDeleteDialogVisible,
        onDeleteWarningMessageVisibleChange = {
            onEvent(ModifyProductVariantEvent.SetDangerousDeleteDialogVisibility(it))
        },
        deleteWarningMessage =
            stringResource(id = R.string.item_product_variant_delete_warning_text),
        isDeleteWarningConfirmed = uiState.isDangerousDeleteDialogConfirmed,
        onDeleteWarningConfirmedChange = {
            onEvent(ModifyProductVariantEvent.SetDangerousDeleteDialogConfirmation(it))
        },
        onMerge = {
            onEvent(ModifyProductVariantEvent.MergeProductVariant(uiState.selectedMergeCandidate))
        },
        isMergeVisible = uiState.isMergeVisible,
        isMergeSearchDialogVisible = uiState.isMergeSearchDialogVisible,
        onMergeSearchDialogVisibleChange = {
            onEvent(ModifyProductVariantEvent.SetMergeSearchDialogVisibility(it))
        },
        mergeSearchDialogCandidateTextTransformation = { it.name },
        isMergeConfirmVisible = uiState.isMergeConfirmationDialogVisible,
        onMergeConfirmVisibleChange = {
            onEvent(ModifyProductVariantEvent.SetMergeConfirmationDialogVisibility(it))
        },
        mergeConfirmMessage =
            stringResource(R.string.merge_action_message_template)
                .replace("{value_2}", uiState.selectedMergeCandidate?.name ?: "???")
                .replace("{value_1}", uiState.currentProductVariant?.name ?: "???"),
        mergeCandidates =
            uiState.allProductVariants
                .filterNot { it.id == uiState.currentProductVariant?.id }
                .toImmutableList(),
        onChosenMergeCandidateChange = {
            onEvent(ModifyProductVariantEvent.SelectMergeCandidate(it))
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
                onValueChange = { onEvent(ModifyProductVariantEvent.SetName(it)) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions =
                    KeyboardActions(onDone = { onEvent(ModifyProductVariantEvent.Submit) }),
                label = { Text(text = stringResource(R.string.item_product_variant)) },
                supportingText = { uiState.name.error?.ErrorText() },
                isError = uiState.name.isError(),
                modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier.fillMaxWidth().padding(16.dp).clickable(
                        enabled = uiState.isVariantGlobalChangeEnabled,
                        indication = null,
                        interactionSource = isGlobalVariantInteractionSource,
                    ) {
                        uiState.isVariantGlobal.data?.let {
                            onEvent(ModifyProductVariantEvent.SetIsVariantGlobal(!it))
                        }
                    },
            ) {
                Checkbox(
                    enabled = uiState.isVariantGlobalChangeEnabled,
                    checked = uiState.isVariantGlobal.data ?: false,
                    onCheckedChange = { onEvent(ModifyProductVariantEvent.SetIsVariantGlobal(it)) },
                    interactionSource = isGlobalVariantInteractionSource,
                )

                Text(
                    text = stringResource(R.string.variant_use_as_global),
                    style = MaterialTheme.typography.bodyLarge,
                    color =
                        if (uiState.isVariantGlobalChangeEnabled) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = optionalAlpha)
                        },
                )
            }
        }
    }
}

@PreviewLightDark
@ExpandedPreviews
@Composable
private fun ModifyProductVariantScreenImplPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyProductVariantScreenImpl(uiState = ModifyProductVariantUiState(), onEvent = {})
        }
    }
}
