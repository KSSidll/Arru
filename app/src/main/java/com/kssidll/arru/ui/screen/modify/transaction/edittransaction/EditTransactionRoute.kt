package com.kssidll.arru.ui.screen.modify.transaction.edittransaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionEvent
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionEventResult
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionScreenImpl
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

@Composable
fun EditTransactionRoute(
    isExpandedScreen: Boolean,
    transactionId: Long,
    navigateBack: () -> Unit,
    navigateAddShop: (query: String?) -> Unit,
    navigateEditShop: (shopId: Long) -> Unit,
    providedShopId: Long?,
    viewModel: EditTransactionViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val navigateBackLock = remember { Mutex() }

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(transactionId) && !navigateBackLock.isLocked) {
                navigateBackLock.tryLock()
                navigateBack()
            }
        }
    }

    LaunchedEffect(transactionId) { viewModel.updateState(transactionId) }

    LaunchedEffect(providedShopId) {
        viewModel.handleEvent(ModifyTransactionEvent.SelectShop(providedShopId))
    }

    ModifyTransactionScreenImpl(
        isExpandedScreen = isExpandedScreen,
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            scope.launch {
                when (event) {
                    is ModifyTransactionEvent.NavigateBack -> {
                        if (!navigateBackLock.isLocked) {
                            navigateBackLock.tryLock()
                            navigateBack()
                        }
                    }
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
                    is ModifyTransactionEvent.DeleteTransaction -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyTransactionEventResult.SuccessDelete &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            navigateBack()
                        }
                    }
                    is ModifyTransactionEvent.SetDangerousDeleteDialogVisibility ->
                        viewModel.handleEvent(event)
                    is ModifyTransactionEvent.SetDangerousDeleteDialogConfirmation ->
                        viewModel.handleEvent(event)
                    is ModifyTransactionEvent.Submit -> {
                        val result = viewModel.handleEvent(event)
                        if (
                            result is ModifyTransactionEventResult.SuccessUpdate &&
                                !navigateBackLock.isLocked
                        ) {
                            navigateBackLock.tryLock()
                            navigateBack()
                        }
                    }
                }
            }
        },
        submitButtonText = stringResource(id = R.string.transaction_edit),
    )
}
