package com.kssidll.arru.ui.component.list

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.ui.theme.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseClickableListItem(
    text: String,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .combinedClickable(
                role = Role.Button,
                onClick = {
                    onClick?.invoke()
                },
                onClickLabel = onClickLabel,
                onLongClick = {
                    onLongClick?.invoke()
                },
                onLongClickLabel = onLongClickLabel,
            )
    ) {
        Text(
            text = text,
            style = Typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(
                    vertical = 16.dp,
                    horizontal = 4.dp
                )
                .align(Alignment.Center)
        )
    }
}

@PreviewLightDark
@Composable
private fun BaseClickableListItemPreview() {
    ArrugarqTheme {
        Surface {
            BaseClickableListItem(
                text = "test"
            )
        }
    }
}
