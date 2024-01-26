package com.kssidll.arrugarq.ui.screen.home.transactions


import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

/**
 * Page fetch size
 */
internal const val fullItemFetchCount = 8

/**
 * Maximum prefetched items
 */
internal const val fullItemMaxPrefetchCount = fullItemFetchCount * 6

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionBasketRepository: TransactionBasketRepositorySource
): ViewModel() {
    fun transactions(): Flow<List<TransactionBasketWithItems>> {
        return transactionBasketRepository.allTransactionBasketsWithItemsFlow()
    }
}
