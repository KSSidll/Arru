package com.kssidll.arrugarq.presentation.screen.addshop

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun AddShopRoute(
    onBack: () -> Unit,
) {
    val addShopViewModel: AddShopViewModel = hiltViewModel()

    AddShopScreen(
        onBack = onBack,
        onShopAdd = {
            addShopViewModel.addShop(it)
        },
    )
}