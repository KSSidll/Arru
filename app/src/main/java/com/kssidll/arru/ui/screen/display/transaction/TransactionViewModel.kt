package com.kssidll.arru.ui.screen.display.transaction

import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.domain.data.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionBasketRepositorySource,
): ViewModel() {
    fun transaction(transactionId: Long): Flow<Data<TransactionBasketWithItems?>> {
        return transactionRepository.transactionBasketWithItemsFlow(transactionId)
    }
}