package com.kssidll.arrugarq.ui.screen.producer


import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.ui.theme.*

@Composable
fun ProducerScreen(

) {

}

@Composable
fun ProducerScreenContent(

) {

}

@Preview(
    group = "ProducerScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ProducerScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ProducerScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProducerScreenContent(

            )
        }
    }
}
