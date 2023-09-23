package com.kssidll.arrugarq.presentation.screen.home

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun HomeRoute(
    onAddItem: () -> Unit
) {
    val homeViewModel: HomeViewModel = hiltViewModel()

    HomeScreen(
        onAddItem = onAddItem,
        itemMonthlyTotals = homeViewModel.getItemTotalSpentByMonth().collectAsState(initial = listOf()).value
    )
}