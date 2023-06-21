package com.kssidll.arrugarq.ui.addshop

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
fun AddShopScreen(
    onBack: () -> Unit,
    onShopAdd: (AddShopData) -> Unit,
) {
    Column {
        SecondaryAppBar(onBack = onBack) {}

        Spacer(modifier = Modifier.height(12.dp))

        Box (
            modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            AddShopScreenContent(
                onShopAdd = onShopAdd,
            )
        }
    }
}

@Composable
fun AddShopScreenContent(
    onShopAdd: (AddShopData) -> Unit,
) {

}

@Preview(group = "AddShopScreen", name = "Add Shop Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(group = "AddShopScreen", name = "Add Shop Screen Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun AddShopScreenPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddShopScreen(
                onShopAdd = {},
                onBack = {},
            )
        }
    }
}
