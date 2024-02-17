package com.kssidll.arru.ui.screen.display.transaction

import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionBasketRepositorySource,
): ViewModel() {
    fun transaction(transactionId: Long): Flow<TransactionBasketWithItems?> {
        return transactionRepository.transactionBasketWithItemsFlow(transactionId)
    }
}