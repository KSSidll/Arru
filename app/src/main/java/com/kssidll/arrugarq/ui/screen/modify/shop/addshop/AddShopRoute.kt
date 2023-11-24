package com.kssidll.arrugarq.ui.screen.modify.shop.addshop

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.modify.shop.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddShopRoute(
    navigateBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddShopViewModel = hiltViewModel()

    ModifyShopScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                val result = viewModel.addShop()
                if (result != null) navigateBack()
            }
        },
    )
}