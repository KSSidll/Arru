package com.kssidll.arrugarq.ui.home

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kssidll.arrugarq.ui.theme.ArrugarqTheme

@Composable
fun HomeScreen(
    onAddItem: () -> Unit,
) {
    Box(modifier = Modifier.padding(8.dp)) {
        Column {
            Row (
                modifier = Modifier.fillMaxWidth()
            ){

            }
            Row (
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ){
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

@Preview(group = "HomeScreen", name = "Home Screen Dark", showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Preview(group = "HomeScreen", name = "Home Screen Light", showBackground = true, uiMode = UI_MODE_NIGHT_NO)
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