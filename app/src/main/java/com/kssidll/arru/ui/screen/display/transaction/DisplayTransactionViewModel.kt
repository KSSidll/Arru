package com.kssidll.arru.ui.screen.display.transaction

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.repository.TransactionRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

@HiltViewModel
class DisplayTransactionViewModel
@Inject
constructor(private val transactionRepository: TransactionRepositorySource) : ViewModel() {
    fun transaction(transactionId: Long): Flow<TransactionBasketWithItems?> {
        return transactionRepository.transactionBasketWithItems(transactionId)
    }
}
