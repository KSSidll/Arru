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

data class StyledTextFieldColors(
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
fun styledTextFieldColorDefaults(
    focusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor: Color = MaterialTheme.colorScheme.onSurface,
    disabledTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.3F),
    errorTextColor: Color = MaterialTheme.colorScheme.error,
    focusedIndicatorColor: Color = MaterialTheme.colorScheme.tertiary,
    unfocusedIndicatorColor: Color = MaterialTheme.colorScheme.tertiary,
    disabledIndicatorColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.3F),
    errorIndicatorColor: Color = MaterialTheme.colorScheme.error,
    focusedTrailingIconColor: Color = MaterialTheme.colorScheme.primary,
    unfocusedTrailingIconColor: Color = MaterialTheme.colorScheme.primary,
    disabledTrailingIconColor: Color = MaterialTheme.colorScheme.primary.copy(0.3F),
    errorTrailingIconColor: Color = MaterialTheme.colorScheme.primary,
    focusedLabelColor: Color = MaterialTheme.colorScheme.tertiary,
    unfocusedLabelColor: Color = MaterialTheme.colorScheme.tertiary,
    disabledLabelColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.3F),
    errorLabelColor: Color = MaterialTheme.colorScheme.error,
    focusedContainerColor: Color = Color.Transparent,
    unfocusedContainerColor: Color = Color.Transparent,
    disabledContainerColor: Color = Color.Transparent,
    errorContainerColor: Color = Color.Transparent,
    cursorColor: Color = MaterialTheme.colorScheme.tertiary,
    errorCursorColor: Color = MaterialTheme.colorScheme.error,
    selectionColors: TextSelectionColors = TextSelectionColors(
        handleColor = MaterialTheme.colorScheme.tertiary,
        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
    ),
    optionalFocusedTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.4F),
    optionalUnfocusedTextColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.4F),
    optionalFocusedIndicatorColor: Color = MaterialTheme.colorScheme.tertiary.copy(0.4F),
    optionalUnfocusedIndicatorColor: Color = MaterialTheme.colorScheme.tertiary.copy(0.4F),
    optionalFocusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.4F),
    optionalUnfocusedTrailingIconColor: Color = MaterialTheme.colorScheme.onSurface.copy(0.4F),
    optionalFocusedLabelIcon: Color = MaterialTheme.colorScheme.tertiary.copy(0.6F),
    optionalUnfocusedLabelIcon: Color = MaterialTheme.colorScheme.tertiary.copy(0.6F),
): StyledTextFieldColors {
    return StyledTextFieldColors(
        colors = TextFieldDefaults.colors(
            focusedTextColor = focusedTextColor,
            unfocusedTextColor = unfocusedTextColor,
            disabledTextColor = disabledTextColor,
            errorTextColor = errorTextColor,
            focusedIndicatorColor = focusedIndicatorColor,
            unfocusedIndicatorColor = unfocusedIndicatorColor,
            disabledIndicatorColor = disabledIndicatorColor,
            errorIndicatorColor = errorIndicatorColor,
            focusedTrailingIconColor = focusedTrailingIconColor,
            unfocusedTrailingIconColor = unfocusedTrailingIconColor,
            disabledTrailingIconColor = disabledTrailingIconColor,
            errorTrailingIconColor = errorTrailingIconColor,
            focusedLabelColor = focusedLabelColor,
            unfocusedLabelColor = unfocusedLabelColor,
            disabledLabelColor = disabledLabelColor,
            errorLabelColor = errorLabelColor,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            disabledContainerColor = disabledContainerColor,
            errorContainerColor = errorContainerColor,
            cursorColor = cursorColor,
            errorCursorColor = errorCursorColor,
            selectionColors = selectionColors
        ),
        optionalFocusedTextColor = optionalFocusedTextColor,
        optionalUnfocusedTextColor = optionalUnfocusedTextColor,
        optionalFocusedIndicatorColor = optionalFocusedIndicatorColor,
        optionalUnfocusedIndicatorColor = optionalUnfocusedIndicatorColor,
        optionalFocusedTrailingIconColor = optionalFocusedTrailingIconColor,
        optionalUnfocusedTrailingIconColor = optionalUnfocusedTrailingIconColor,
        optionalFocusedLabelIcon = optionalFocusedLabelIcon,
        optionalUnfocusedLabelIcon = optionalUnfocusedLabelIcon,
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
    minLines: Int = Int.MIN_VALUE,
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