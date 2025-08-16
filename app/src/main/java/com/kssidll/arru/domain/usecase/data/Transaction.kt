package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.TransactionRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */

class GetTransactionEntityUseCase(
    private val transactionRepository: TransactionRepositorySource,
) {
     operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = transactionRepository.get(id).flowOn(dispatcher)
}


/** DOMAIN */

// class GetTransactionUseCase(
//     private val getTransactionEntityUseCase: GetTransactionEntityUseCase,
// ) {
//     operator fun invoke(
//         id: Long,
//         dispatcher: CoroutineDispatcher = Dispatchers.IO,
//     ) = getTransactionEntityUseCase(id, dispatcher).map {
//         it?.let { Transaction.fromEntity(it) }
//     }
// }
