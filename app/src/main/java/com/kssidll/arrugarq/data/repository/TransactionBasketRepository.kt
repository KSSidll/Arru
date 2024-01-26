package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class TransactionBasketRepository(private val dao: TransactionBasketDao): TransactionBasketRepositorySource {
    override suspend fun allTransactionBaskets(): List<TransactionBasket> {
        return dao.allTransactionBaskets()
    }

    override fun allTransactionBasketsFlow(): Flow<List<TransactionBasket>> {
        return dao.allTransactionBasketsFlow()
    }

    override fun allTransactionBasketsWithItemsFlow(): Flow<List<TransactionBasketWithItems>> {
        return dao.allTransactionBasketsWithItemsFlow()
    }

    override suspend fun insert(transactionBasket: TransactionBasket): Long {
        return dao.insert(transactionBasket)
    }

    override suspend fun update(transactionBasket: TransactionBasket) {
        dao.update(transactionBasket)
    }

    override suspend fun delete(transactionBasket: TransactionBasket) {
        dao.delete(transactionBasket)
    }
}