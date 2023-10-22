package com.kssidll.arrugarq.ui.screen.variant


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
import com.kssidll.arrugarq.ui.screen.shared.*
import com.kssidll.arrugarq.ui.theme.*

private val ItemHorizontalPadding: Dp = 20.dp

@Composable
fun EditVariantScreenImpl(
    onBack: () -> Unit,
    state: EditVariantScreenState,
    onSubmit: () -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    EditScreen(
        onBack = onBack,
        title = stringResource(id = R.string.item_product_variant_full),
        onDelete = onDelete,
        onSubmit = onSubmit,
        submitButtonText = stringResource(id = R.string.item_product_variant_add),
        showDeleteWarning = state.showDeleteWarning,
        deleteWarningConfirmed = state.deleteWarningConfirmed,
        deleteWarningMessage = stringResource(id = R.string.item_product_variant_delete_warning_text),
    ) {
        StyledOutlinedTextField(
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
                    text = stringResource(R.string.item_product_variant),
                )
            },
            isError = if (state.attemptedToSubmit.value) state.nameError.value else false,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ItemHorizontalPadding)
        )
    }
}

data class EditVariantScreenState(
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
fun EditVariantScreenState.validateName(): Boolean {
    return !(name.value.isBlank()).also { nameError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
fun EditVariantScreenState.validate(): Boolean {
    return validateName()
}

/**
 * performs data validation and tries to extract embedded data
 * @param productId Id of the product that the variant is being created for
 * @param variantId Optional Id of the variant
 * @return Null if validation sets error flags, extracted data otherwise
 */
fun EditVariantScreenState.extractVariantOrNull(
    productId: Long,
    variantId: Long = 0
): ProductVariant? {
    if (!validate()) return null

    return ProductVariant(
        id = variantId,
        productId = productId,
        name = name.value.trim(),
    )
}

@Preview(
    group = "EditVariantScreenImpl",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "EditVariantScreenImpl",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun EditVariantScreenImplPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            EditVariantScreenImpl(
                onBack = {},
                state = EditVariantScreenState(),
                onSubmit = {},
            )
        }
    }
}
