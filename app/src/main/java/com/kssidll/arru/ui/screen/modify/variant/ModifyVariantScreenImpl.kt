package com.kssidll.arru.ui.screen.modify.variant


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.field.*
import com.kssidll.arru.ui.screen.modify.*
import com.kssidll.arru.ui.theme.*

private val ItemHorizontalPadding: Dp = 20.dp

/**
 * [ModifyScreen] implementation for [ProductVariant]
 * @param onBack Called to request a back navigation, isn't triggered by other events like submission or deletion
 * @param state [ModifyVariantScreenState] instance representing the screen state
 * @param onSubmit Callback called when the submit action is triggered
 * @param onDelete Callback called when the delete action is triggered, in case of very destructive actions, should check if delete warning is confirmed, and if not, trigger a delete warning dialog via showDeleteWarning parameter as none of those are handled internally by the component, setting to null removes the delete option
 * @param submitButtonText Text displayed in the submit button, defaults to variant add string resource
 */
@Composable
fun ModifyVariantScreenImpl(
    onBack: () -> Unit,
    state: ModifyVariantScreenState,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    submitButtonText: String = stringResource(id = R.string.item_product_variant_add),
) {
    ModifyScreen<FuzzySearchSource>(
        onBack = onBack,
        title = stringResource(id = R.string.item_product_variant_full),
        onSubmit = onSubmit,
        onDelete = onDelete,
        submitButtonText = submitButtonText,
        showDeleteWarning = state.showDeleteWarning,
        deleteWarningConfirmed = state.deleteWarningConfirmed,
        deleteWarningMessage = stringResource(id = R.string.item_product_variant_delete_warning_text),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 500.dp)
        ) {
            StyledOutlinedTextField(
                enabled = state.name.value.isEnabled(),
                singleLine = true,
                value = state.name.value.data ?: String(),
                onValueChange = {
                    state.name.value = Field.Loaded(it)
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        onSubmit()
                    }
                ),
                label = {
                    Text(
                        text = stringResource(R.string.item_product_variant),
                    )
                },
                supportingText = {
                    if (state.attemptedToSubmit.value) {
                        state.name.value.error?.ErrorText()
                    }
                },
                isError = if (state.attemptedToSubmit.value) state.name.value.isError() else false,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = ItemHorizontalPadding)
            )
        }
    }
}

@PreviewLightDark
@PreviewExpanded
@Composable
private fun ModifyVariantScreenImplPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyVariantScreenImpl(
                onBack = {},
                state = ModifyVariantScreenState(),
                onSubmit = {},
            )
        }
    }
}
