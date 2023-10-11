package com.kssidll.arrugarq.ui.screen.category


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryRoute(
    categoryId: Long,
    onBack: () -> Unit,
) {
    val category: CategoryViewModel = hiltViewModel()

    CategoryScreen(

    )
}