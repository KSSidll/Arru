package com.kssidll.arrugarq.presentation.screen.home

import android.content.res.Configuration.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.presentation.theme.*

@Composable
fun HomeScreen(
    onAddItem: () -> Unit,
) {
    Box(modifier = Modifier.padding(8.dp)) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {

            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                FilledIconButton(
                    modifier = Modifier.size(72.dp),
                    onClick = onAddItem
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add new item",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Preview(
    group = "HomeScreen",
    name = "Home Screen Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "HomeScreen",
    name = "Home Screen Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun HomeScreenPreview() {
    ArrugarqTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            HomeScreen(
                onAddItem = {}
            )
        }
    }
}