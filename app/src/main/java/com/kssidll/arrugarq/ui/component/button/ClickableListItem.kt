package com.kssidll.arrugarq.ui.component.button

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.ui.theme.*

@Composable
fun ClickableListItem(
    text: String,
    onClick: (() -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick?.invoke()
            },
    ) {
        Box(
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            Text(
                text = text,
                fontSize = 20.sp
            )
        }
    }
}

@Preview(
    group = "Clickable List Item",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Clickable List Item",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun ClickableListItemPreview() {
    ArrugarqTheme {
        Surface {
            ClickableListItem(
                text = "test"
            )
        }
    }
}
