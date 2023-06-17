package com.kssidll.arrugarq.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme

@Composable
fun HomeScreen() {

}

@Preview(group = "HomeScreen", name = "Home Screen Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(group = "HomeScreen", name = "Home Screen Light", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
@Composable
fun HomeScreenPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen()
        }
    }
}