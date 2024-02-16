package com.kssidll.arrugarq.ui.screen.modify.transaction.addtransaction

import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.modify.transaction.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun AddTransactionRoute(
    navigateBack: () -> Unit,
    navigateShopAdd: (query: String?) -> Unit,
    navigateShopEdit: (shopId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val viewModel: AddTransactionViewModel = hiltViewModel()

    ModifyTransactionScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        shops = viewModel.allShops()
            .collectAsState(initial = emptyList()).value,
        onSubmit = {
            scope.launch {
                if (viewModel.addTransaction()
                        .isNotError()
                ) {
                    navigateBack()
                }
            }
        },
        onShopAddButtonClick = navigateShopAdd,
        onTransactionShopLongClick = navigateShopEdit,
    )
}
