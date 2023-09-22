package com.kssidll.arrugarq.presentation.screen.home

import androidx.compose.runtime.*

@Composable
fun HomeRoute(
    onAddItem: () -> Unit
) {
    HomeScreen(
        onAddItem = onAddItem,
    )
}