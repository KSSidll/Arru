package com.kssidll.arrugarq.ui.additem

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kssidll.arrugarq.ui.shared.SecondaryAppBar
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme

@Composable
fun AddItemScreen(
    onBack: () -> Unit
) {
    Column {
        SecondaryAppBar(onBack = onBack) {}

        Box {
            AddItemScreenContent(

            )
        }
    }
}

@Composable
fun AddItemScreenContent() {

}

@Preview(group = "AddItemScreen", name = "Add Item Screen Dark", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(group = "AddItemScreen", name = "Add Item Screen Light", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun AddItemScreenPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AddItemScreen(
                onBack = {}
            )
        }
    }
}
