package com.kssidll.arru.ui.screen.modify.transaction.addtransaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

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
            .collectAsState(initial = Data.Loading()).value,
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
