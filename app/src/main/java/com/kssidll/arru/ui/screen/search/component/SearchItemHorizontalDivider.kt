package com.kssidll.arru.ui.screen.search.component


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.ui.theme.*

private val DividerHorizontalPadding: Dp = 16.dp

/**
 * [HorizontalDivider] with [DividerHorizontalPadding] padding and tertiary color
 */
@Composable
internal fun SearchItemHorizontalDivider(

) {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = DividerHorizontalPadding),
        color = MaterialTheme.colorScheme.tertiaryContainer,
    )
}

@Preview(
    group = "SearchItemHorizontalDivider",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "SearchItemHorizontalDivider",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun SearchItemHorizontalDividerPreview() {
    ArrugarqTheme {
        Surface {
            SearchItemHorizontalDivider(

            )
        }
    }
}
