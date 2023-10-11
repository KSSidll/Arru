package com.kssidll.arrugarq.ui.screen.shop

import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.ui.theme.*


@Composable
fun ShopScreen(

) {

}

@Composable
fun ShopScreenContent(

) {

}

@Preview(
    group = "Shop Screen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "Shop Screen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ShopScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ShopScreenContent(

            )
        }
    }
}
