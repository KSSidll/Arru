package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.paging.*
import kotlinx.coroutines.flow.*

class TransactionBasketRepository(private val dao: TransactionBasketDao): TransactionBasketRepositorySource {
    // Create

    override suspend fun insert(transactionBasket: TransactionBasket): Long {
        return dao.insert(transactionBasket)
    }

    override suspend fun insertTransactionItem(transactionBasketItem: TransactionBasketItem): Long {
        return dao.insertTransactionBasketItem(transactionBasketItem)
    }

    // Update

    override suspend fun update(transactionBasket: TransactionBasket) {
        dao.update(transactionBasket)
    }

    // Delete

    override suspend fun delete(transactionBasket: TransactionBasket) {
        dao.delete(transactionBasket)
    }

    override suspend fun deleteTransactionBasketItem(transactionBasketItem: TransactionBasketItem) {
        dao.deleteTransactionBasketItem(transactionBasketItem)
    }

    // Read

    override suspend fun get(transactionBasketId: Long): TransactionBasket? {
        return dao.get(transactionBasketId)
    }

    override fun totalSpentFlow(): Flow<Long> {
        return dao.totalSpentFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByDayFlow(): Flow<List<TransactionSpentByTime>> {
        return dao.totalSpentByDayFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByWeekFlow(): Flow<List<TransactionSpentByTime>> {
        return dao.totalSpentByWeekFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByMonthFlow(): Flow<List<TransactionSpentByTime>> {
        return dao.totalSpentByMonthFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByYearFlow(): Flow<List<TransactionSpentByTime>> {
        return dao.totalSpentByYearFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override suspend fun transactionBasketsWithItems(
        startPosition: Int,
        count: Int
    ): List<TransactionBasketWithItems> {
        return dao.transactionBasketsWithItems(
            startPosition,
            count
        )
    }

    override fun transactionBasketsPagedFlow(): Flow<PagingData<TransactionBasketWithItems>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TransactionBasketWithItemsPagingSource(this) }
        )
            .flow
    }

}