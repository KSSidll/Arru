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
fun AddProductItemProducer(
    item: ProductProducer,
    onItemClick: (ProductProducer) -> Unit,
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
    group = "AddProductScreenItemProducer",
    name = "Add Product Item Producer Screen Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "AddProductScreenItemProducer",
    name = "Add Product Item Producer Screen Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun AddProductScreenItemProducerPreview() {
    ArrugarqTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            AddProductItemProducer(
                item = ProductProducer(
                    0,
                    "test"
                ),
                onItemClick = {},
            )
        }
    }
}
