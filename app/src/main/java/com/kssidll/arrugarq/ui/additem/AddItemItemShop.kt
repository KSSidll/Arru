package com.kssidll.arrugarq.ui.additem

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.ui.theme.*

@Composable
fun AddItemItemShop(
    item: Shop,
    onItemClick: (Shop) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onItemClick(item)
            },
    ) {
        Box(
            modifier = Modifier.padding(vertical = 16.dp),
        ) {
            Text(
                text = item.name,
                fontSize = 20.sp
            )
        }
    }
}

@Preview(
    group = "AddItemItemShop",
    name = "Add Item Item Shop Screen Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "AddItemItemShop",
    name = "Add Item Item Shop Screen Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddItemItemShopPreview() {
    ArrugarqTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            AddItemItemShop(
                item = Shop(
                    0,
                    "test"
                ),
                onItemClick = {},
            )
        }
    }
}
