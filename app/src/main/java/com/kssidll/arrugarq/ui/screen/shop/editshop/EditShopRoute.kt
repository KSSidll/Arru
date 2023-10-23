package com.kssidll.arrugarq.ui.screen.shop.editshop


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
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
        if (!viewModel.updateState(shopId)) {
            onBack()
        }
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
        },
        submitButtonText = stringResource(id = R.string.item_shop_edit),
    )
}
