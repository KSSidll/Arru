package com.kssidll.arrugarq.ui.component.field

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.ui.theme.*

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    optional: Boolean = false,
    value: String = String(),
    onClick: (() -> Unit)? = null,
    readOnly: Boolean = true,
    label: String = String(),
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
            else if (optional) optionalUnfocusedIndicatorColor
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

    StyledOutlinedTextField(
        modifier = modifier
            .onFocusEvent {
                if (it.isFocused) {
                    onClick?.invoke()
                    focusControllerModifier = Modifier.focusProperties { canFocus = false }
                    focusDisabled = true
                }
            }
            .then(focusControllerModifier),
        enabled = enabled,
        optional = optional,
        readOnly = readOnly,
        singleLine = true,
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
        isError = error,
        trailingIcon = {
            if (showAddButton) {
                Box(
                    modifier = Modifier
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


@Preview(
    group = "Search Field",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Search Field",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun SearchFieldPreview() {
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

@Preview(
    group = "Search Field",
    name = "Optional Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Search Field",
    name = "Optional Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun OptionalSearchFieldPreview() {
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

@Preview(
    group = "Search Field",
    name = "Disabled Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Search Field",
    name = "Disabled Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun DisabledSearchFieldPreview() {
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

@Preview(
    group = "Search Field",
    name = "Error Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Search Field",
    name = "Error Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ErrorSearchFieldPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
        ) {
            SearchField(
                value = "Test",
                error = true,
            )
        }
    }
}
