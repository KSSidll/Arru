package com.kssidll.arrugarq.ui.addshop

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AddShopRoute(
    onBack: () -> Unit,
) {
    val addShopViewModel: AddShopViewModel = hiltViewModel()

    AddShopScreen (
        onBack = onBack,
        onShopAdd = {
            addShopViewModel.addShop(it)
        },
    )
}