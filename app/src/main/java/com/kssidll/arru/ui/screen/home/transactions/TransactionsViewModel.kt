package com.kssidll.arru.ui.screen.home.transactions


import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.map
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

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
