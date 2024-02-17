package com.kssidll.arrugarq.ui.screen.modify.transaction.edittransaction

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.modify.transaction.*
import dev.olshevski.navigation.reimagined.hilt.*
import kotlinx.coroutines.*

@Composable
fun EditTransactionRoute(
    transactionId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: (transactionId: Long) -> Unit,
    navigateShopAdd: (query: String?) -> Unit,
    navigateShopEdit: (shopId: Long) -> Unit,
    providedShopId: Long?,
) {
    val scope = rememberCoroutineScope()
    val viewModel: EditTransactionViewModel = hiltViewModel()

    LaunchedEffect(transactionId) {
        if (!viewModel.updateState(transactionId)) {
            navigateBack()
        }
    }

    LaunchedEffect(providedShopId) {
        viewModel.setSelectedShopToProvided(providedShopId)
    }

    ModifyTransactionScreenImpl(
        onBack = navigateBack,
        state = viewModel.screenState,
        shops = viewModel.allShops()
            .collectAsState(initial = emptyList()).value,
        onNewShopSelected = {
            viewModel.onNewShopSelected(it)
        },
        onSubmit = {
            scope.launch {
                if (viewModel.updateTransaction(transactionId)
                        .isNotError()
                ) {
                    navigateBack()
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteTransaction(transactionId)
                        .isNotError()
                ) {
                    navigateBackDelete(transactionId)
                }
            }
        },
        submitButtonText = stringResource(id = R.string.transaction_edit),
        onShopAddButtonClick = navigateShopAdd,
        onTransactionShopLongClick = navigateShopEdit,
    )
}
