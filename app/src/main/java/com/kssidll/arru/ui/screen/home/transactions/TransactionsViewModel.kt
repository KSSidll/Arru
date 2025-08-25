package com.kssidll.arru.ui.screen.home.transactions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.kssidll.arru.domain.data.data.Transaction
import com.kssidll.arru.domain.data.emptyImmutableSet
import com.kssidll.arru.domain.usecase.data.GetAllTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update

@Immutable
data class TransactionsUiState(
    val listState: LazyListState = LazyListState(),
    val transactions: Flow<PagingData<Transaction>> = flowOf(),
    val transactionWithVisibleItems: ImmutableSet<Long> = emptyImmutableSet(),
)

@Immutable
sealed class TransactionsEvent {
    data object NavigateSearch : TransactionsEvent()

    data class NavigateDisplayProduct(val productId: Long) : TransactionsEvent()

    data class NavigateDisplayProductCategory(val productCategoryId: Long) : TransactionsEvent()

    data class NavigateDisplayProductProducer(val productProducerId: Long) : TransactionsEvent()

    data class NavigateDisplayShop(val shopId: Long) : TransactionsEvent()

    data class NavigateEditTransaction(val transactionId: Long) : TransactionsEvent()

    data class NavigateAddItem(val transactionId: Long) : TransactionsEvent()

    data class NavigateEditItem(val itemId: Long) : TransactionsEvent()

    data class ToggleTransactionItemVisibility(val transactionId: Long) : TransactionsEvent()
}

@HiltViewModel
class TransactionsViewModel
@Inject
constructor(private val getAllTransactionsUseCase: GetAllTransactionsUseCase) : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        _uiState.update { currentState ->
            currentState.copy(transactions = getAllTransactionsUseCase().cachedIn(viewModelScope))
        }
    }

    fun handleEvent(event: TransactionsEvent) {
        when (event) {
            is TransactionsEvent.NavigateAddItem -> {}
            is TransactionsEvent.NavigateDisplayProduct -> {}
            is TransactionsEvent.NavigateDisplayProductCategory -> {}
            is TransactionsEvent.NavigateDisplayProductProducer -> {}
            is TransactionsEvent.NavigateDisplayShop -> {}
            is TransactionsEvent.NavigateEditItem -> {}
            is TransactionsEvent.NavigateEditTransaction -> {}
            is TransactionsEvent.NavigateSearch -> {}
            is TransactionsEvent.ToggleTransactionItemVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(
                        transactionWithVisibleItems =
                            if (
                                currentState.transactionWithVisibleItems.contains(
                                    event.transactionId
                                )
                            ) {
                                (currentState.transactionWithVisibleItems - event.transactionId)
                                    .toImmutableSet()
                            } else {
                                (currentState.transactionWithVisibleItems + event.transactionId)
                                    .toImmutableSet()
                            }
                    )
                }
            }
        }
    }
}
