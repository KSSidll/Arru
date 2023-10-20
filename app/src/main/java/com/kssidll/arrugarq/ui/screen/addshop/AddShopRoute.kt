package com.kssidll.arrugarq.ui.screen.addshop

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.shared.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddShopRoute(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val addShopViewModel: AddShopViewModel = hiltViewModel()

    EditShopScreen(
        onBack = onBack,
        state = addShopViewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = addShopViewModel.addShop()
                if (result != null) onBack()
            }
        },
    )
}