package com.kssidll.arrugarq.ui.screen.shop.editshop


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.shop.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun EditShopRoute(
    shopId: Long,
    onBack: () -> Unit,
) {
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
        // TODO implement shop deletion with warnings when shop contains values
        onDelete = null
    )
}
