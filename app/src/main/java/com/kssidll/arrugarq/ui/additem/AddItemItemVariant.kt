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
fun AddItemItemVariant(
    item: ProductVariant,
    onItemClick: (ProductVariant) -> Unit,
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
    group = "AddItemItemVariant",
    name = "Add Item Item Variant Screen Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "AddItemItemVariant",
    name = "Add Item Item Variant Screen Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddItemItemVariantPreview() {
    ArrugarqTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            AddItemItemVariant(
                item = ProductVariant(
                    0,
                    "test"
                ),
                onItemClick = {},
            )
        }
    }
}