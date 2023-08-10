package com.kssidll.arrugarq.ui.addshop

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.*

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