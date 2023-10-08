package com.kssidll.arrugarq.ui.screen.addshop

import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddShopRoute(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addShopViewModel: AddShopViewModel = hiltViewModel()

    AddShopScreen(
        onBack = onBack,
        state = addShopViewModel.addShopScreenState,
        onShopAdd = {
            scope.launch {
                val result = addShopViewModel.addShop()
                if (result != null) onBack()
            }
        },
    )
}