package com.kssidll.arru.ui.screen.modify.transaction.edittransaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun EditTransactionRoute(
    isExpandedScreen: Boolean,
    transactionId: Long,
    navigateBack: () -> Unit,
    navigateBackDelete: (transactionId: Long) -> Unit,
    navigateAddShop: (query: String?) -> Unit,
    navigateEditShop: (shopId: Long) -> Unit,
    providedShopId: Long?,
    viewModel: EditTransactionViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(transactionId) {
        if (!viewModel.updateState(transactionId)) {
            navigateBack()
        }
    }

    LaunchedEffect(providedShopId) { viewModel.setSelectedShopToProvided(providedShopId) }

    ModifyTransactionScreenImpl(
        isExpandedScreen = isExpandedScreen,
        onBack = navigateBack,
        state = viewModel.screenState,
        shops = viewModel.allShops().collectAsState(initial = emptyImmutableList()).value,
        onNewShopSelected = { viewModel.onNewShopSelected(it) },
        onSubmit = {
            scope.launch {
                if (viewModel.updateTransaction(transactionId).isNotError()) {
                    navigateBack()
                }
            }
        },
        onDelete = {
            scope.launch {
                if (viewModel.deleteTransaction(transactionId).isNotError()) {
                    navigateBackDelete(transactionId)
                }
            }
        },
        submitButtonText = stringResource(id = R.string.transaction_edit),
        onShopAddButtonClick = navigateAddShop,
        onTransactionShopLongClick = navigateEditShop,
    )
}
