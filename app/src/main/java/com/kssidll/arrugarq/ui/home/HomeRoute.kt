package com.kssidll.arrugarq.ui.home

import androidx.compose.runtime.Composable

@Composable
fun HomeRoute(
    onAddItem: () -> Unit
) {
    HomeScreen(
        onAddItem = onAddItem
    )
}