package com.kssidll.arru.ui.screen.search.component


import android.content.res.Configuration.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.semantics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.R
import com.kssidll.arru.ui.theme.*

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
                onClickLabel = stringResource(id = R.string.select)
            ) {
                onItemClick()
            }
        else Modifier
            .combinedClickable(
                role = Role.Button,
                onClick = {
                    onItemClick()
                },
                onClickLabel = stringResource(id = R.string.select),
                onLongClick = {
                    onItemLongClick()
                },
                onLongClickLabel = stringResource(id = R.string.edit)
            )

    Box(
        modifier = containerModifier
            .fillMaxWidth()
            .height(itemHeight)
    ) {
        Text(
            text = text,
            style = Typography.titleLarge,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Preview(
    group = "SearchItem",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "SearchItem",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun SearchItemPreview() {
    ArrugarqTheme {
        Surface {
            SearchItem(
                text = "test",
                onItemClick = {},
                onItemLongClick = {},
            )
        }
    }
}
