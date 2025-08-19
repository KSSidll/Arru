package com.kssidll.arru.ui.screen.modify.productvariant

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
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.interfaces.FuzzySearchSource
import com.kssidll.arru.ui.component.field.StyledOutlinedTextField
import com.kssidll.arru.ui.screen.modify.ModifyScreen
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.optionalAlpha

private val ItemHorizontalPadding: Dp = 20.dp

/**
 * [ModifyScreen] implementation for [ProductVariantEntity]
 *
 * @param onBack Called to request a back navigation, isn't triggered by other events like
 *   submission or deletion
 * @param state [ModifyProductVariantScreenState] instance representing the screen state
 * @param onSubmit Callback called when the submit action is triggered
 * @param onDelete Callback called when the delete action is triggered, in case of very destructive
 *   actions, should check if delete warning is confirmed, and if not, trigger a delete warning
 *   dialog via showDeleteWarning parameter as none of those are handled internally by the
 *   component, setting to null removes the delete option
 * @param submitButtonText Text displayed in the submit button, defaults to variant add string
 *   resource
 */
@Composable
fun ModifyProductVariantScreenImpl(
    onBack: () -> Unit,
    state: ModifyProductVariantScreenState,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
    onDelete: (() -> Unit)? = null,
    submitButtonText: String = stringResource(id = R.string.item_product_variant_add),
) {
    val isGlobalVariantInteractionSource = remember { MutableInteractionSource() }

    ModifyScreen<FuzzySearchSource>(
        onBack = onBack,
        title = stringResource(id = R.string.item_product_variant_full),
        onSubmit = onSubmit,
        onDelete = onDelete,
        submitButtonText = submitButtonText,
        showDeleteWarning = state.showDeleteWarning,
        deleteWarningConfirmed = state.deleteWarningConfirmed,
        deleteWarningMessage =
            stringResource(id = R.string.item_product_variant_delete_warning_text),
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 500.dp),
        ) {
            StyledOutlinedTextField(
                enabled = state.name.value.isEnabled(),
                singleLine = true,
                value = state.name.value.data ?: String(),
                onValueChange = { state.name.value = Field.Loaded(it) },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                label = { Text(text = stringResource(R.string.item_product_variant)) },
                supportingText = {
                    if (state.attemptedToSubmit.value) {
                        state.name.value.error?.ErrorText()
                    }
                },
                isError = if (state.attemptedToSubmit.value) state.name.value.isError() else false,
                modifier = Modifier.fillMaxWidth().padding(horizontal = ItemHorizontalPadding),
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier.fillMaxWidth().padding(16.dp).clickable(
                        enabled =
                            state.isVariantGlobal.value.isEnabled() &&
                                state.isVariantGlobal.value.data != null,
                        indication = null,
                        interactionSource = isGlobalVariantInteractionSource,
                    ) {
                        state.isVariantGlobal.value.data?.let {
                            state.isVariantGlobal.value = Field.Loaded(!it)
                        }
                    },
            ) {
                Checkbox(
                    enabled =
                        state.isVariantGlobal.value.isEnabled() &&
                            state.isVariantGlobal.value.data != null,
                    checked = state.isVariantGlobal.value.data ?: false,
                    onCheckedChange = { state.isVariantGlobal.value = Field.Loaded(it) },
                    interactionSource = isGlobalVariantInteractionSource,
                )

                Text(
                    text = stringResource(R.string.variant_use_as_global),
                    style = MaterialTheme.typography.bodyLarge,
                    color =
                        if (
                            state.isVariantGlobal.value.isEnabled() &&
                                state.isVariantGlobal.value.data != null
                        ) {
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
            ModifyProductVariantScreenImpl(
                onBack = {},
                state = ModifyProductVariantScreenState(),
                onSubmit = {},
            )
        }
    }
}
