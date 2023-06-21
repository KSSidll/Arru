package com.kssidll.arrugarq.ui.addproduct

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kssidll.arrugarq.ui.shared.SecondaryAppBar
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme

@Composable
fun AddProductScreen(
    onBack: () -> Unit,
    onProductAdd: (AddProductData) -> Unit,
) {
    Column {
        SecondaryAppBar(onBack = onBack) {}

        Spacer(modifier = Modifier.height(12.dp))

        Box (
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            AddProductScreenContent(
                onProductAdd = onProductAdd,
            )
        }
    }
}

@Composable
fun AddProductScreenContent(
    onProductAdd: (AddProductData) -> Unit,
) {

}

@Preview(group = "AddProductScreen", name = "Add Product Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(group = "AddProductScreen", name = "Add Product Screen Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun AddProductScreenPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddProductScreen(
                onProductAdd = {},
                onBack = {},
            )
        }
    }
}
