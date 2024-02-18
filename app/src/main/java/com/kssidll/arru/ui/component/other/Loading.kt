package com.kssidll.arru.ui.component.other

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.ui.theme.*

@Composable
fun Loading() {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(150.dp),
            strokeWidth = 12.dp
        )
    }
}

@PreviewLightDark
@Composable
fun LoadingPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Loading()
        }
    }
}
