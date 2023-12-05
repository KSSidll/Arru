package com.kssidll.arrugarq.ui.screen.modify.shop.editshop


import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.modify.shop.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditShopRoute(
    shopId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    val viewModel: EditShopViewModel = hiltViewModel()

    LaunchedEffect(shopId) {
        if (!viewModel.updateState(shopId)) {
            navigateBack()
        }
    }

    ModifyShopScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        onSubmit = {
            scope.launch {
                if (viewModel.updateShop(shopId)) {
                    navigateBack()
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteShop(shopId)) {
                    navigateBackDelete()
                }
            }
        },
        submitButtonText = stringResource(id = R.string.item_shop_edit),
    )
}
