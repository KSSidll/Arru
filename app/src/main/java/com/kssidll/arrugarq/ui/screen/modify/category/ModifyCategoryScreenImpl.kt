package com.kssidll.arrugarq.ui.screen.modify.category


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.component.field.*
import com.kssidll.arrugarq.ui.screen.modify.*
import com.kssidll.arrugarq.ui.theme.*

private val ItemHorizontalPadding: Dp = 20.dp

/**
 * [ModifyScreen] implementation for [ProductCategory]
 * @param onBack Called to request a back navigation, isn't triggered by other events like submission or deletion
 * @param state [ModifyCategoryScreenState] instance representing the screen state
 * @param onSubmit Callback called when the submit action is triggered
 * @param onDelete Callback called when the delete action is triggered, in case of very destructive actions, should check if delete warning is confirmed, and if not, trigger a delete warning dialog via showDeleteWarning parameter as none of those are handled internally by the component, setting to null removes the delete option
 * @param submitButtonText Text displayed in the submit button, defaults to product add string resource
 */
@Composable
fun ModifyCategoryScreenImpl(
    onBack: () -> Unit,
    state: ModifyCategoryScreenState,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    submitButtonText: String = stringResource(id = R.string.item_product_category_add),
) {
    ModifyScreen(
        onBack = onBack,
        title = stringResource(id = R.string.item_product_category),
        onSubmit = onSubmit,
        onDelete = onDelete,
        submitButtonText = submitButtonText,
        showDeleteWarning = state.showDeleteWarning,
        deleteWarningConfirmed = state.deleteWarningConfirmed,
        deleteWarningMessage = stringResource(id = R.string.item_product_category_delete_warning_text),
    ) {
        StyledOutlinedTextField(
            enabled = state.name.value.isEnabled(),
            singleLine = true,
            value = state.name.value.data ?: String(),
            onValueChange = {
                state.name.value = Field.Loaded(it)
                state.validateName()
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
                    text = stringResource(R.string.item_product_category),
                )
            },
            supportingText = {
                if (state.attemptedToSubmit.value) {
                    state.name.value.error?.errorText()
                }
            },
            isError = if (state.attemptedToSubmit.value) state.name.value.isError() else false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding)
        )
    }
}

@Preview(
    group = "ModifyCategoryScreenImpl",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ModifyCategoryScreenImpl",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ModifyCategoryScreenImplPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyCategoryScreenImpl(
                onBack = {},
                state = ModifyCategoryScreenState(),
                onSubmit = {},
            )
        }
    }
}