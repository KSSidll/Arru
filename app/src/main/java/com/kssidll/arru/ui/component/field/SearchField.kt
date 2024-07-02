package com.kssidll.arru.ui.component.field

import android.view.ViewConfiguration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.theme.ArrugarqTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    height: Dp = 60.dp,
    enabled: Boolean = true,
    optional: Boolean = false,
    value: String = String(),
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    singleLine: Boolean = true,
    label: String = String(),
    supportingText: @Composable (() -> Unit)? = null,
    error: Boolean = false,
    showAddButton: Boolean = true,
    onAddButtonClick: (() -> Unit)? = null,
    addButtonDescription: String? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    var lineColor: Color

    with(styledTextFieldColorDefaults()) {
        lineColor =
            if (error) colors.errorIndicatorColor
            else if (!enabled) colors.disabledIndicatorColor
            else if (optional) optionalUnfocusedIndicator
            else colors.unfocusedIndicatorColor
    }

    // a hack to force the text field to lose focus every time it's focused
    // setting canFocus to focusDisabled doesn't work so we make mutable modifier instead
    // i have no idea if there's a better way to do this
    // and i have no idea why we aren't just able to do that with compose api
    // but this is the only solution i could come up with that actually works
    var focusDisabled: Boolean by remember { mutableStateOf(false) }
    var focusControllerModifier: Modifier by remember { mutableStateOf(Modifier) }
    LaunchedEffect(focusDisabled) {
        if (focusDisabled) {
            focusControllerModifier = Modifier
            focusDisabled = false
        }
    }

    val isPressed by interactionSource.collectIsPressedAsState()
    var pressedLatch by remember { mutableStateOf(false) }
    var pressedJob: Job? = null

    LaunchedEffect(isPressed) {
        if (isPressed) {
            pressedLatch = true

            pressedJob?.cancel()
            pressedJob = launch {
                delay(
                    ViewConfiguration.getLongPressTimeout()
                        .toLong()
                )

                @Suppress("KotlinConstantConditions") // it is not always true kotlin, pls
                if (isPressed) {
                    onLongClick?.invoke()
                    pressedLatch = false

                }
            }
        } else if (pressedLatch) {
            onClick?.invoke()
            pressedLatch = false
        }
    }

    Modifier.pointerInput(Unit) {
        detectTapGestures()
    }

    Box(
        modifier = modifier
            .width(IntrinsicSize.Min)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.DropdownList,
                onClick = {}
            )
    ) {
        StyledOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .focusProperties { canFocus = false },
            enabled = enabled,
            optional = optional,
            readOnly = true,
            singleLine = singleLine,
            value = value,
            onValueChange = {},
            textStyle = TextStyle.Default.copy(
                fontSize = 16.sp
            ),
            label = {
                Text(
                    text = label,
                    fontSize = 16.sp,
                )
            },
            supportingText = supportingText,
            isError = error,
            trailingIcon = {
                if (showAddButton) {
                    Box(
                        modifier = Modifier
                            .height(height)
                            .aspectRatio(1F)
                            .clickable {
                                onAddButtonClick?.invoke()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawLine(
                                color = lineColor,
                                start = Offset(
                                    0F,
                                    0F
                                ),
                                end = Offset(
                                    0F,
                                    size.height
                                ),
                                strokeWidth = Dp.Hairline.value
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = addButtonDescription,
                            modifier = Modifier.fillMaxSize(0.75F)
                        )
                    }
                }
            },
            interactionSource = interactionSource,
        )
    }
}

@PreviewLightDark
@Composable
private fun SearchFieldPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            SearchField(
                value = "Test"
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun OptionalSearchFieldPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            SearchField(
                value = "Test",
                optional = true,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun DisabledSearchFieldPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            SearchField(
                value = "Test",
                enabled = false,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ErrorSearchFieldPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxWidth()) {
            SearchField(
                value = "Test",
                error = true,
                supportingText = {
                    FieldError.NoValueError.ErrorText()
                },
                label = "test",
            )
        }
    }
}
