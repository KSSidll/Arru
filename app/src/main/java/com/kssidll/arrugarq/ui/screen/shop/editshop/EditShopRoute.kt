package com.kssidll.arrugarq.ui.screen.shop.editshop


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.shop.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditShopRoute(
    shopId: Long,
    onBack: () -> Unit,
    onBackDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditShopViewModel = hiltViewModel()

    LaunchedEffect(shopId) {
        viewModel.updateState(shopId)
    }

    EditShopScreenImpl(
        onBack = onBack,
        state = viewModel.screenState,
        onSubmit = {
            viewModel.updateShop(shopId)
            onBack()
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteShop(shopId)) {
                    onBackDelete()
                }
            }
        }
    )
}
