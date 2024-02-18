package com.kssidll.arru.ui.screen.modify.transaction.addtransaction

import androidx.compose.runtime.*
import com.kssidll.arru.ui.screen.modify.transaction.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddTransactionRoute(
    isExpandedScreen: Boolean,
    navigateBack: () -> Unit,
    navigateTransaction: (transactionId: Long) -> Unit,
    navigateShopAdd: (query: String?) -> Unit,
    navigateShopEdit: (shopId: Long) -> Unit,
    providedShopId: Long?,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddTransactionViewModel = hiltViewModel()

    LaunchedEffect(providedShopId) {
        viewModel.setSelectedShopToProvided(providedShopId)
    }

    ModifyTransactionScreenImpl(
        isExpandedScreen = isExpandedScreen,
        onBack = navigateBack,
        state = viewModel.screenState,
        shops = viewModel.allShops()
            .collectAsState(initial = emptyList()).value,
        onNewShopSelected = {
            viewModel.onNewShopSelected(it)
        },
        onSubmit = {
            scope.launch {
                val result = viewModel.addTransaction()
                if (result.isNotError() && result.id != null) {
                    navigateBack()
                    navigateTransaction(result.id)
                }
            }
        },
        onShopAddButtonClick = navigateShopAdd,
        onTransactionShopLongClick = navigateShopEdit,
    )
}
