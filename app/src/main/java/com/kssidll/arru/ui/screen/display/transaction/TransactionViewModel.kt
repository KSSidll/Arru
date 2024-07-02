package com.kssidll.arru.ui.screen.display.transaction

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.Transaction
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import com.kssidll.arru.domain.data.Data
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionBasketRepositorySource,
): ViewModel() {
    fun transaction(transactionId: Long): Flow<Data<Transaction?>> {
        return transactionRepository.transactionBasketWithItemsFlow(transactionId)
    }
}