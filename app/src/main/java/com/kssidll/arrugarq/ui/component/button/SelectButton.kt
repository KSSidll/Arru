package com.kssidll.arrugarq.ui.component.button

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*

data class SelectButtonColors(
    val selectedContainer: Color,
    val selectedContent: Color,
    val unselectedContainer: Color,
    val unselectedContent: Color,
    val disabledContainerColor: Color,
    val disabledContentColor: Color,
)

fun SelectButtonColors.toButtonColors(selected: Boolean): ButtonColors {
    return ButtonColors(
        containerColor = if (selected) selectedContainer else unselectedContainer,
        contentColor = if (selected) selectedContent else unselectedContent,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )
}

@Composable
fun selectButtonDefaultColors(
    selectedContainer: Color = MaterialTheme.colorScheme.tertiary,
    selectedContent: Color = MaterialTheme.colorScheme.onTertiary,
    unselectedContainer: Color = MaterialTheme.colorScheme.tertiaryContainer,
    unselectedContent: Color = MaterialTheme.colorScheme.onTertiaryContainer,
    disabledContainerColor: Color = ButtonDefaults.buttonColors().disabledContainerColor,
    disabledContentColor: Color = ButtonDefaults.buttonColors().disabledContentColor,
): SelectButtonColors {
    return SelectButtonColors(
        selectedContainer = selectedContainer,
        selectedContent = selectedContent,
        unselectedContainer = unselectedContainer,
        unselectedContent = unselectedContent,
        disabledContainerColor = disabledContainerColor,
        disabledContentColor = disabledContentColor,
    )
}

/**
 * A Button with `selected` possible state
 */
@Composable
fun SelectButton(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    colors: SelectButtonColors = selectButtonDefaultColors(),
    content: @Composable RowScope.() -> Unit
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        colors = colors.toButtonColors(selected),
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interactionSource,
        content = content
    )
}