package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface TransactionBasketRepositorySource {
    /**
     * @return all [TransactionBasket] entries in the database as a list
     */
    suspend fun allTransactionBaskets(): List<TransactionBasket>

    /**
     * @return all [TransactionBasket] entries in the database as a list flow
     */
    fun allTransactionBasketsFlow(): Flow<List<TransactionBasket>>

    /**
     * @return all [TransactionBasketWithItems] entries created from the database [TransactionBasket]
     * entries as list flow
     */
    fun allTransactionBasketsWithItemsFlow(): Flow<List<TransactionBasketWithItems>>

    suspend fun insert(transactionBasket: TransactionBasket): Long
    suspend fun update(transactionBasket: TransactionBasket)
    suspend fun delete(transactionBasket: TransactionBasket)

}