package com.kssidll.arru.ui.screen.home.transactions


import androidx.lifecycle.*
import androidx.paging.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionBasketRepository: TransactionBasketRepositorySource
): ViewModel() {
    fun transactions(): Flow<PagingData<TransactionBasketWithItems>> {
        return transactionBasketRepository.transactionBasketsPagedFlow()
    }
}
