package com.kssidll.arrugarq.presentation.component.field

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

data class StyledTextFieldColors (
    val colors: TextFieldColors,

    val optionalFocusedTextColor: Color,
    val optionalUnfocusedTextColor: Color,
    val optionalFocusedIndicatorColor: Color,
    val optionalUnfocusedIndicatorColor: Color,
    val optionalFocusedTrailingIconColor: Color,
    val optionalUnfocusedTrailingIconColor: Color,
    val optionalFocusedLabelIcon: Color,
    val optionalUnfocusedLabelIcon: Color,
)

@Composable
fun StyledTextFieldColors.toTextFieldColors(optional: Boolean = false): TextFieldColors =
    TextFieldDefaults.colors(
        focusedTextColor = if (optional) optionalFocusedTextColor else colors.focusedTextColor,
        unfocusedTextColor = if (optional) optionalUnfocusedTextColor else colors.unfocusedTextColor,
        disabledTextColor = colors.disabledTextColor,
        errorTextColor = colors.errorTextColor,

        focusedIndicatorColor = if (optional) optionalFocusedIndicatorColor else colors.focusedIndicatorColor,
        unfocusedIndicatorColor = if (optional) optionalUnfocusedIndicatorColor else colors.unfocusedIndicatorColor,
        disabledIndicatorColor = colors.disabledIndicatorColor,
        errorIndicatorColor = colors.errorIndicatorColor,

        focusedTrailingIconColor = if (optional) optionalFocusedTrailingIconColor else colors.focusedTrailingIconColor,
        unfocusedTrailingIconColor = if (optional) optionalUnfocusedTrailingIconColor else colors.unfocusedTrailingIconColor,
        disabledTrailingIconColor = colors.disabledTrailingIconColor,
        errorTrailingIconColor = colors.errorTrailingIconColor,

        focusedLabelColor = if (optional) optionalFocusedLabelIcon else colors.focusedLabelColor,
        unfocusedLabelColor = if (optional) optionalUnfocusedLabelIcon else colors.unfocusedLabelColor,
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
fun styledTextFieldColorDefaults(): StyledTextFieldColors {
    with (MaterialTheme.colorScheme) {
        return StyledTextFieldColors(
            colors = TextFieldDefaults.colors(
                focusedTextColor = onSurface,
                unfocusedTextColor = onSurface,
                disabledTextColor = onSurface.copy(0.3F),
                errorTextColor = this.error,

                focusedIndicatorColor = tertiary,
                unfocusedIndicatorColor = tertiary,
                disabledIndicatorColor = onSurface.copy(0.3F),
                errorIndicatorColor = this.error,

                focusedTrailingIconColor = onSurface,
                unfocusedTrailingIconColor = onSurface,
                disabledTrailingIconColor = onSurface.copy(0.3F),
                errorTrailingIconColor = onSurface,

                focusedLabelColor = tertiary,
                unfocusedLabelColor = tertiary,
                disabledLabelColor = onSurface.copy(0.3F),
                errorLabelColor = this.error,

                focusedContainerColor = surface,
                unfocusedContainerColor = surface,
                disabledContainerColor = surface,
                errorContainerColor = surface,

                cursorColor = tertiary,
                errorCursorColor = this.error,
                selectionColors = TextSelectionColors(
                    handleColor = tertiary,
                    backgroundColor = tertiaryContainer,
                ),
            ),
            optionalFocusedTextColor = onSurface.copy(0.4F),
            optionalUnfocusedTextColor = onSurface.copy(0.4F),
            optionalFocusedIndicatorColor = tertiary.copy(0.4F),
            optionalUnfocusedIndicatorColor = tertiary.copy(0.4F),
            optionalFocusedTrailingIconColor = onSurface.copy(0.4F),
            optionalUnfocusedTrailingIconColor = onSurface.copy(0.4F),
            optionalFocusedLabelIcon = tertiary.copy(0.6F),
            optionalUnfocusedLabelIcon = tertiary.copy(0.6F),
        )
    }
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
    minLines: Int = Int.MIN_VALUE,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    shape: Shape = OutlinedTextFieldDefaults.shape,
) {
    val colors = styledTextFieldColorDefaults().toTextFieldColors(optional)

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
        colors = colors,
    )
}