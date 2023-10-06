package com.kssidll.arrugarq.ui.screen.home.predictions

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import com.kssidll.arrugarq.ui.theme.*

@Composable
fun PredictionsScreen(

) {
    PredictionsScreenContent(

    )
}

@Composable
private fun PredictionsScreenContent(

) {

}

@Preview(
    group = "Predictions Screen",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Predictions Screen",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun PredictionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            PredictionsScreenContent(

            )
        }
    }
}
