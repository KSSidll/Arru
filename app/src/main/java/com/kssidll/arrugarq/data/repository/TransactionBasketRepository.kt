package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class TransactionBasketRepository(private val dao: TransactionBasketDao): TransactionBasketRepositorySource {
    // Create

    override suspend fun insert(transactionBasket: TransactionBasket): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertItemToBasket(
        transactionBasket: TransactionBasket,
        item: Item
    ): Long {
        TODO("Not yet implemented")
    }

    override suspend fun insertItemToBasket(
        transactionBasket: TransactionBasket,
        item: FullItem
    ): Long {
        TODO("Not yet implemented")
    }

    // Update

    override suspend fun update(transactionBasket: TransactionBasket) {
        TODO("Not yet implemented")
    }

    override suspend fun update(transactionBaskets: List<TransactionBasket>) {
        TODO("Not yet implemented")
    }

    // Delete

    override suspend fun delete(transactionBasket: TransactionBasket) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(transactionBaskets: List<TransactionBasket>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransactionBasketItem(transactionBasketItem: TransactionBasketItem) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTransactionBasketItem(transactionBasketItems: List<TransactionBasketItem>) {
        TODO("Not yet implemented")
    }

    // Read

    override suspend fun get(transactionBasketId: Long): TransactionBasket? {
        //        TODO("Not yet implemented")
        return null
    }

    override fun totalSpentFlow(): Flow<Float> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByDayFlow(): Flow<List<ItemSpentByTime>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByWeekFlow(): Flow<List<ItemSpentByTime>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByMonthFlow(): Flow<List<ItemSpentByTime>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByYearFlow(): Flow<List<ItemSpentByTime>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override suspend fun all(): List<TransactionBasket> {
        //        TODO("Not yet implemented")
        return emptyList()
    }

    override fun allFlow(): Flow<List<TransactionBasket>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun allTransactionBasketsWithItemsFlow(): Flow<List<TransactionBasketWithItems>> {
        //        TODO("Not yet implemented")
        return emptyFlow()
    }
}