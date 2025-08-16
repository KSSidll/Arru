package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.data.data.Transaction
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** ENTITY */

class GetTransactionEntityUseCase(
    private val transactionRepository: TransactionRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = withContext(dispatcher) {
        transactionRepository.get(id)
    }
}


/** DOMAIN */

class GetTransactionUseCase(
    private val getTransactionEntityUseCase: GetTransactionEntityUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = getTransactionEntityUseCase(id, dispatcher).map {
        it?.let { Transaction.fromEntity(it) }
    }
}
