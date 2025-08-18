package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.TransactionRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */
class GetTransactionEntityUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.get(id).flowOn(dispatcher)
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

class GetTotalSpentUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpent().flowOn(dispatcher)
}

class GetItemsUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.items().flowOn(dispatcher)
}

/** DOMAIN CHART */
class GetTotalSpentByDayUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpentByDay().flowOn(dispatcher)
}

class GetTotalSpentByWeekUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpentByWeek().flowOn(dispatcher)
}

class GetTotalSpentByMonthUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpentByMonth().flowOn(dispatcher)
}

class GetTotalSpentByYearUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpentByYear().flowOn(dispatcher)
}
