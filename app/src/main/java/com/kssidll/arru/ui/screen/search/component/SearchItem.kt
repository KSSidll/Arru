package com.kssidll.arru.ui.screen.search.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.R
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography

private val DefaultItemHeight: Dp = 80.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun SearchItem(
    text: String,
    onItemClick: () -> Unit,
    onItemLongClick: (() -> Unit)? = null,
    itemHeight: Dp = DefaultItemHeight,
) {
    val containerModifier =
        if (onItemLongClick == null)
            Modifier.clickable(
                role = Role.Button,
                onClickLabel = stringResource(id = R.string.select),
            ) {
                onItemClick()
            }
        else
            Modifier.combinedClickable(
                role = Role.Button,
                onClick = { onItemClick() },
                onClickLabel = stringResource(id = R.string.select),
                onLongClick = { onItemLongClick() },
                onLongClickLabel = stringResource(id = R.string.edit),
            )

    Box(modifier = containerModifier.fillMaxWidth().height(itemHeight)) {
        Text(
            text = text,
            style = Typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@PreviewLightDark
@Composable
private fun SearchItemPreview() {
    ArrugarqTheme { Surface { SearchItem(text = "test", onItemClick = {}, onItemLongClick = {}) } }
}
