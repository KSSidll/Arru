package com.kssidll.arru.ui.screen.display.transaction

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.repository.TransactionRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionRepositorySource,
): ViewModel() {
    fun transaction(transactionId: Long): Flow<TransactionBasketWithItems?> {
        return transactionRepository.transactionBasketWithItems(transactionId)
    }
}