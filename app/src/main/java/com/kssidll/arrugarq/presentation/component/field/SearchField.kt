package com.kssidll.arrugarq.presentation.component.field

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
import com.kssidll.arrugarq.presentation.theme.*

@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    optional: Boolean = false,
    value: String = String(),
    onFocus: (() -> Unit)? = null,
    label: String = String(),
    error: Boolean = false,
    showAddButton: Boolean = true,
    onAddButtonClick: (() -> Unit)? = null,
    addButtonDescription: String? = null,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
) {
    var lineColor: Color

    with (styledTextFieldColorDefaults()) {
        lineColor =
            if (error) colors.errorIndicatorColor
            else if (!enabled) colors.disabledIndicatorColor
            else if (optional) optionalUnfocusedIndicatorColor
            else colors.unfocusedIndicatorColor
    }

    val focusRequester = remember { FocusRequester() }
    StyledOutlinedTextField(
        enabled = enabled,
        optional = optional,
        readOnly = true,
        singleLine = true,
        value = value,
        onValueChange = {},
        modifier = Modifier
            .focusRequester(focusRequester)
            .onFocusEvent {
                if (it.isFocused) {
                    onFocus?.invoke()
                }
                focusRequester.freeFocus()
            }
            .then(modifier),
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
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        },
        interactionSource = interactionSource,
    )
}


@Preview(
    group = "SearchField",
    name = "Search Field Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "SearchField",
    name = "Search Field Light",
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
    group = "SearchField",
    name = "Optional Search Field Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "SearchField",
    name = "Optional Search Field Light",
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
    group = "SearchField",
    name = "Disabled Search Field Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "SearchField",
    name = "Disabled Search Field Light",
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
    group = "SearchField",
    name = "Error Search Field Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "SearchField",
    name = "Error Search Field Light",
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
