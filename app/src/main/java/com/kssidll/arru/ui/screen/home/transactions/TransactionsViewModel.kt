package com.kssidll.arru.ui.screen.home.transactions

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.model.TransactionPreview
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val transactionRepository: TransactionRepositorySource
): ViewModel() {
    fun transactions(): Flow<PagingData<TransactionPreview>> {
        return transactionRepository.allPagedAsPreview()
    }
}
