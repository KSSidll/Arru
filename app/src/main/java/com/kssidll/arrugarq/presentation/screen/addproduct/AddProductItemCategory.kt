package com.kssidll.arrugarq.presentation.screen.addproduct

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.presentation.theme.*

@Composable
fun AddProductItemCategory(
    item: ProductCategory,
    onItemClick: (ProductCategory) -> Unit,
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
    group = "AddProductScreenItemCategory",
    name = "Add Product Item Category Screen Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "AddProductScreenItemCategory",
    name = "Add Product Item Category Screen Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddProductScreenItemCategoryPreview() {
    ArrugarqTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            AddProductItemCategory(
                item = ProductCategory(
                    0,
                    "test"
                ),
                onItemClick = {},
            )
        }
    }
}
