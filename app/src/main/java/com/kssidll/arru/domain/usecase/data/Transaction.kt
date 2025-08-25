package com.kssidll.arru.domain.usecase.data

import androidx.paging.map
import com.kssidll.arru.data.repository.TransactionRepositorySource
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
            .map { intermediate -> intermediate?.toTransaction() }
            .flowOn(dispatcher)
}

class GetAllTransactionsUseCase(private val transactionRepository: TransactionRepositorySource) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository
            .intermediates()
            .map { pagingData -> pagingData.map { intermediate -> intermediate.toTransaction() } }
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
