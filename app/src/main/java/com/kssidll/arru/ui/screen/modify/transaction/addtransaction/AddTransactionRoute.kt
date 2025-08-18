package com.kssidll.arru.ui.screen.modify.transaction.addtransaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun AddTransactionRoute(
    isExpandedScreen: Boolean,
    navigateBack: () -> Unit,
    navigateDisplayTransaction: (transactionId: Long) -> Unit,
    navigateAddShop: (query: String?) -> Unit,
    navigateEditShop: (shopId: Long) -> Unit,
    providedShopId: Long?,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddTransactionViewModel = hiltViewModel()

    LaunchedEffect(providedShopId) { viewModel.setSelectedShopToProvided(providedShopId) }

    ModifyTransactionScreenImpl(
        isExpandedScreen = isExpandedScreen,
        onBack = navigateBack,
        state = viewModel.screenState,
        shops = viewModel.allShops().collectAsState(initial = emptyImmutableList()).value,
        onNewShopSelected = { viewModel.onNewShopSelected(it) },
        onSubmit = {
            scope.launch {
                val result = viewModel.addTransaction()
                if (result.isNotError() && result.id != null) {
                    navigateBack()
                    navigateDisplayTransaction(result.id)
                }
            }
        },
        onShopAddButtonClick = navigateAddShop,
        onTransactionShopLongClick = navigateEditShop,
    )
}
