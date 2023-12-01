package com.kssidll.arrugarq.ui.screen.modify.shop


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
import com.kssidll.arrugarq.ui.component.field.*
import com.kssidll.arrugarq.ui.screen.modify.*
import com.kssidll.arrugarq.ui.theme.*

private val ItemHorizontalPadding: Dp = 20.dp

/**
 * [ModifyScreen] implementation for [Shop]
 * @param onBack Called to request a back navigation, isn't triggered by other events like submission or deletion
 * @param state [ModifyShopScreenState] instance representing the screen state
 * @param onSubmit Callback called when the submit action is triggered
 * @param onDelete Callback called when the delete action is triggered, in case of very destructive actions, should check if delete warning is confirmed, and if not, trigger a delete warning dialog via showDeleteWarning parameter as none of those are handled internally by the component, setting to null removes the delete option
 * @param submitButtonText Text displayed in the submit button, defaults to shop add string resource
 */
@Composable
fun ModifyShopScreenImpl(
    onBack: () -> Unit,
    state: ModifyShopScreenState,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
    submitButtonText: String = stringResource(id = R.string.item_shop_add),
) {
    ModifyScreen(
        onBack = onBack,
        title = stringResource(id = R.string.item_shop),
        onSubmit = onSubmit,
        onDelete = onDelete,
        submitButtonText = submitButtonText,
        showDeleteWarning = state.showDeleteWarning,
        deleteWarningConfirmed = state.deleteWarningConfirmed,
        deleteWarningMessage = stringResource(id = R.string.item_shop_delete_warning_text)
    ) {
        StyledOutlinedTextField(
            enabled = !state.loadingName.value,
            singleLine = true,
            value = state.name.value,
            onValueChange = {
                state.name.value = it
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
                    text = stringResource(R.string.item_shop),
                )
            },
            isError = if (state.attemptedToSubmit.value) state.nameError.value else false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding)
        )
    }

}

/**
 * Data representing [ModifyShopScreenImpl] screen state
 */
data class ModifyShopScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val name: MutableState<String> = mutableStateOf(String()),
    val nameError: MutableState<Boolean> = mutableStateOf(false),

    val loadingName: MutableState<Boolean> = mutableStateOf(false),

    val showDeleteWarning: MutableState<Boolean> = mutableStateOf(false),
    val deleteWarningConfirmed: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Validates name field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
fun ModifyShopScreenState.validateName(): Boolean {
    return !(name.value.isBlank()).also { nameError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
fun ModifyShopScreenState.validate(): Boolean {
    return validateName()
}

/**
 * performs data validation and tries to extract embedded data
 * @return Null if validation sets error flags, extracted data otherwise
 */
fun ModifyShopScreenState.extractShopOrNull(shopId: Long = 0): Shop? {
    if (!validate()) return null

    return Shop(
        id = shopId,
        name = name.value.trim(),
    )
}

@Preview(
    group = "ModifyShopScreenImpl",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ModifyShopScreenImpl",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ModifyShopScreenImplPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ModifyShopScreenImpl(
                onBack = {},
                state = ModifyShopScreenState(),
                onSubmit = {},
            )
        }
    }
}