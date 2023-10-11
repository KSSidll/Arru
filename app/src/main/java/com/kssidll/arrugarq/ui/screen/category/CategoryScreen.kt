package com.kssidll.arrugarq.ui.screen.category


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.ui.theme.*

@Composable
fun CategoryScreen(

) {

}

@Composable
fun CategoryScreenContent(

) {

}

@Preview(
    group = "CategoryScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "CategoryScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun CategoryScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            CategoryScreenContent(

            )
        }
    }
}
