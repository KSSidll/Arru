package com.kssidll.arrugarq.ui.component.field

import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.text.*
import androidx.compose.foundation.text.selection.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.ui.theme.*

data class StyledTextFieldColors(
    val colors: TextFieldColors,

    val optionalFocusedText: Color,
    val optionalUnfocusedText: Color,
    val optionalFocusedIndicator: Color,
    val optionalUnfocusedIndicator: Color,
    val optionalFocusedTrailingIcon: Color,
    val optionalUnfocusedTrailingIcon: Color,
    val optionalFocusedLabel: Color,
    val optionalUnfocusedLabel: Color,
)

@Composable
fun StyledTextFieldColors.toTextFieldColors(optional: Boolean = false): TextFieldColors =
    TextFieldDefaults.colors(
        focusedTextColor = if (optional) optionalFocusedText else colors.focusedTextColor,
        unfocusedTextColor = if (optional) optionalUnfocusedText else colors.unfocusedTextColor,
        disabledTextColor = colors.disabledTextColor,
        errorTextColor = colors.errorTextColor,

        focusedIndicatorColor = if (optional) optionalFocusedIndicator else colors.focusedIndicatorColor,
        unfocusedIndicatorColor = if (optional) optionalUnfocusedIndicator else colors.unfocusedIndicatorColor,
        disabledIndicatorColor = colors.disabledIndicatorColor,
        errorIndicatorColor = colors.errorIndicatorColor,

        focusedTrailingIconColor = if (optional) optionalFocusedTrailingIcon else colors.focusedTrailingIconColor,
        unfocusedTrailingIconColor = if (optional) optionalUnfocusedTrailingIcon else colors.unfocusedTrailingIconColor,
        disabledTrailingIconColor = colors.disabledTrailingIconColor,
        errorTrailingIconColor = colors.errorTrailingIconColor,

        focusedLabelColor = if (optional) optionalFocusedLabel else colors.focusedLabelColor,
        unfocusedLabelColor = if (optional) optionalUnfocusedLabel else colors.unfocusedLabelColor,
        disabledLabelColor = colors.disabledLabelColor,
        errorLabelColor = colors.errorLabelColor,

        focusedContainerColor = colors.focusedContainerColor,
        unfocusedContainerColor = colors.unfocusedContainerColor,
        disabledContainerColor = colors.disabledContainerColor,
        errorContainerColor = colors.errorContainerColor,

        cursorColor = colors.cursorColor,
        errorCursorColor = colors.errorCursorColor,
        selectionColors = colors.textSelectionColors,
    )

@Composable
fun styledTextFieldColorDefaults(
    focusedText: Color = MaterialTheme.colorScheme.onSurface,
    unfocusedText: Color = focusedText,
    disabledText: Color = with(unfocusedText) { if (this == Color.Transparent) this else copy(disabledAlpha) },
    errorText: Color = MaterialTheme.colorScheme.error,
    focusedIndicator: Color = MaterialTheme.colorScheme.tertiary,
    unfocusedIndicator: Color = focusedIndicator,
    disabledIndicator: Color = with(unfocusedIndicator) { if (this == Color.Transparent) this else copy(disabledAlpha) },
    errorIndicator: Color = MaterialTheme.colorScheme.error,
    focusedTrailingIcon: Color = MaterialTheme.colorScheme.primary,
    unfocusedTrailingIcon: Color = focusedTrailingIcon,
    disabledTrailingIcon: Color = with(unfocusedTrailingIcon) { if (this == Color.Transparent) this else copy(disabledAlpha) },
    errorTrailingIcon: Color = focusedTrailingIcon,
    focusedLabel: Color = MaterialTheme.colorScheme.tertiary,
    unfocusedLabel: Color = focusedLabel,
    disabledLabel: Color = with(unfocusedLabel) { if (this == Color.Transparent) this else copy(disabledAlpha) },
    errorLabel: Color = MaterialTheme.colorScheme.error,
    focusedContainer: Color = Color.Transparent,
    unfocusedContainer: Color = focusedContainer,
    disabledContainer: Color = with(unfocusedContainer) { if (this == Color.Transparent) this else copy(disabledAlpha) },
    errorContainer: Color = focusedContainer,
    cursor: Color = MaterialTheme.colorScheme.tertiary,
    errorCursor: Color = MaterialTheme.colorScheme.error,
    textSelection: TextSelectionColors = TextSelectionColors(
        handleColor = cursor,
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
    ),
    optionalFocusedText: Color = with(focusedText) { if (this == Color.Transparent) this else copy(optionalAlpha) },
    optionalUnfocusedText: Color = with(unfocusedText) { if (this == Color.Transparent) this else copy(optionalAlpha) },
    optionalFocusedIndicator: Color = with(focusedIndicator) { if (this == Color.Transparent) this else copy(optionalAlpha) },
    optionalUnfocusedIndicator: Color = with(unfocusedIndicator) { if (this == Color.Transparent) this else copy(optionalAlpha) },
    optionalFocusedTrailingIcon: Color = with(focusedTrailingIcon) { if (this == Color.Transparent) this else copy(optionalAlpha) },
    optionalUnfocusedTrailingIcon: Color = with(unfocusedTrailingIcon) { if (this == Color.Transparent) this else copy(optionalAlpha) },
    optionalFocusedLabel: Color = with(focusedLabel) { if (this == Color.Transparent) this else copy(optionalAlpha) },
    optionalUnfocusedLabel: Color = with(unfocusedLabel) { if (this == Color.Transparent) this else copy(optionalAlpha) },
): StyledTextFieldColors {
    return StyledTextFieldColors(
        colors = TextFieldDefaults.colors(
            focusedTextColor = focusedText,
            unfocusedTextColor = unfocusedText,
            disabledTextColor = disabledText,
            errorTextColor = errorText,
            focusedIndicatorColor = focusedIndicator,
            unfocusedIndicatorColor = unfocusedIndicator,
            disabledIndicatorColor = disabledIndicator,
            errorIndicatorColor = errorIndicator,
            focusedTrailingIconColor = focusedTrailingIcon,
            unfocusedTrailingIconColor = unfocusedTrailingIcon,
            disabledTrailingIconColor = disabledTrailingIcon,
            errorTrailingIconColor = errorTrailingIcon,
            focusedLabelColor = focusedLabel,
            unfocusedLabelColor = unfocusedLabel,
            disabledLabelColor = disabledLabel,
            errorLabelColor = errorLabel,
            focusedContainerColor = focusedContainer,
            unfocusedContainerColor = unfocusedContainer,
            disabledContainerColor = disabledContainer,
            errorContainerColor = errorContainer,
            cursorColor = cursor,
            errorCursorColor = errorCursor,
            selectionColors = textSelection,
        ),
        optionalFocusedText = optionalFocusedText,
        optionalUnfocusedText = optionalUnfocusedText,
        optionalFocusedIndicator = optionalFocusedIndicator,
        optionalUnfocusedIndicator = optionalUnfocusedIndicator,
        optionalFocusedTrailingIcon = optionalFocusedTrailingIcon,
        optionalUnfocusedTrailingIcon = optionalUnfocusedTrailingIcon,
        optionalFocusedLabel = optionalFocusedLabel,
        optionalUnfocusedLabel = optionalUnfocusedLabel,
    )
}


@Composable
fun StyledOutlinedTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit = {},
    enabled: Boolean = true,
    optional: Boolean = false,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle.Default.copy(fontSize = 16.sp),
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: StyledTextFieldColors = styledTextFieldColorDefaults(),
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors.toTextFieldColors(optional),
    )
}