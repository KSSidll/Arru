package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
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

    override suspend fun update(transactionBaskets: List<TransactionBasket>) {
        dao.update(transactionBaskets)
    }

    // Delete

    override suspend fun delete(transactionBasket: TransactionBasket) {
        dao.delete(transactionBasket)
    }

    override suspend fun delete(transactionBaskets: List<TransactionBasket>) {
        dao.delete(transactionBaskets)
    }

    override suspend fun deleteTransactionBasketItem(transactionBasketItem: TransactionBasketItem) {
        dao.deleteTransactionBasketItem(transactionBasketItem)
    }

    override suspend fun deleteTransactionBasketItem(transactionBasketItems: List<TransactionBasketItem>) {
        dao.deleteTransactionBasketItem(transactionBasketItems)
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

    override suspend fun all(): List<TransactionBasket> {
        return dao.all()
    }

    override fun allFlow(): Flow<List<TransactionBasket>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun allTransactionBasketsWithItemsFlow(): Flow<List<TransactionBasketWithItems>> {
        return dao.allTransactionBasketsWithItemsFlow()
            .cancellable()
            .distinctUntilChanged()
    }
}