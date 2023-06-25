package com.kssidll.arrugarq.ui.additem

import android.content.res.Configuration
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kssidll.arrugarq.data.data.Product
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme

@Composable
fun AddItemItemProduct(
    item: Product,
    onItemClick: (Product) -> Unit,
) {
    Row (
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

@Preview(group = "AddItemItemProduct", name = "Add Item Item Product Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(group = "AddItemItemProduct", name = "Add Item Item Product Screen Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun AddItemItemProductPreview() {
    ArrugarqTheme {
        Surface (
            color = MaterialTheme.colorScheme.background,
        ) {
            AddItemItemProduct(
                item = Product(0, "test"),
                onItemClick = {},
            )
        }
    }
}