package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.data.data.Transaction
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/** ENTITY */
class GetTransactionEntityUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.get(id).flowOn(dispatcher)
}

/** DOMAIN */
class GetTransactionUseCase(private val transactionRepository: TransactionRepositorySource) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository
            .intermediateFor(id)
            .map { intermediate ->
                intermediate?.let { transaction ->
                    Transaction(
                        id = transaction.entity.id,
                        date = transaction.entity.date,
                        shopId = transaction.entity.shopEntityId,
                        shopName = transaction.shopEntity?.name,
                        totalCost = transaction.entity.totalCost,
                        note = transaction.entity.note,
                        items = transaction.items.toImmutableList(),
                    )
                }
            }
            .flowOn(dispatcher)
}

class GetTotalSpentUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpent().flowOn(dispatcher)
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
