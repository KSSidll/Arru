package com.kssidll.arru.ui.screen.modify.transaction.addtransaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionEvent
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionEventResult
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
    viewModel: AddTransactionViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    LaunchedEffect(providedShopId) {
        viewModel.handleEvent(ModifyTransactionEvent.SelectShop(providedShopId))
    }

    ModifyTransactionScreenImpl(
        isExpandedScreen = isExpandedScreen,
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyTransactionEvent.NavigateBack -> navigateBack()
                    is ModifyTransactionEvent.NavigateAddShop -> navigateAddShop(event.name)
                    is ModifyTransactionEvent.NavigateEditShop -> navigateEditShop(event.shopId)
                    is ModifyTransactionEvent.SetShopSearchDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyTransactionEvent.SetDatePickerDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyTransactionEvent.SetDate -> viewModel.handleEvent(event)
                    is ModifyTransactionEvent.SetNote -> viewModel.handleEvent(event)
                    is ModifyTransactionEvent.SetTotalCost -> viewModel.handleEvent(event)
                    is ModifyTransactionEvent.IncrementTotalCost -> viewModel.handleEvent(event)
                    is ModifyTransactionEvent.DecrementTotalCost -> viewModel.handleEvent(event)
                    is ModifyTransactionEvent.SelectShop -> viewModel.handleEvent(event)
                    is ModifyTransactionEvent.DeleteTransaction -> {}
                    is ModifyTransactionEvent.SetDangerousDeleteDialogVisibility -> {}
                    is ModifyTransactionEvent.SetDangerousDeleteDialogConfirmation -> {}
                    is ModifyTransactionEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (result is ModifyTransactionEventResult.SuccessInsert) {
                            navigateBack()
                            navigateDisplayTransaction(result.id)
                        }
                    }
                }
            }
        },
    )
}
