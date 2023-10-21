package com.kssidll.arrugarq.ui.screen.shop.addshop

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.shop.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddShopRoute(
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddShopViewModel = hiltViewModel()

    EditShopScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addShop()
                if (result != null) onBack()
            }
        },
    )
}