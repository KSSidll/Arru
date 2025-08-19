package com.kssidll.arru.ui.component.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.Typography

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BaseClickableListItem(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onClickLabel: String? = null,
    onLongClick: (() -> Unit)? = null,
    onLongClickLabel: String? = null,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .minimumInteractiveComponentSize()
                .combinedClickable(
                    role = Role.Button,
                    onClick = { onClick?.invoke() },
                    onClickLabel = onClickLabel,
                    onLongClick = { onLongClick?.invoke() },
                    onLongClickLabel = onLongClickLabel,
                )
    ) {
        Text(
            text = text,
            style = Typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp, horizontal = 4.dp).align(Alignment.Center),
        )
    }
}

@PreviewLightDark
@Composable
private fun BaseClickableListItemPreview() {
    ArruTheme { Surface { BaseClickableListItem(text = "test") } }
}
