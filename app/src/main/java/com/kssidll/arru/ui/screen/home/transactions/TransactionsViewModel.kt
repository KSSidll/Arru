package com.kssidll.arru.ui.screen.home.transactions


import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.paging.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

data class TransactionBasketDisplayData(
    val basket: TransactionBasketWithItems,
    var itemsVisible: MutableState<Boolean> = mutableStateOf(false)
)

fun Flow<PagingData<TransactionBasketWithItems>>.toDisplayData(): Flow<PagingData<TransactionBasketDisplayData>> {
    return map { flow -> flow.map { TransactionBasketDisplayData(it) } }
}

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionBasketRepository: TransactionBasketRepositorySource
): ViewModel() {
    fun transactions(): Flow<PagingData<TransactionBasketDisplayData>> {
        return transactionBasketRepository.transactionBasketsPagedFlow()
            .toDisplayData()
    }
}
