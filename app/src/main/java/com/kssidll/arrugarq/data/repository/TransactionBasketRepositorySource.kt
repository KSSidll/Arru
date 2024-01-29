package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface TransactionBasketRepositorySource {
    // Create

    /**
     * Inserts [TransactionBasket]
     * @param transactionBasket [TransactionBasket] to insert
     * @return id of newly inserted [TransactionBasket]
     */
    suspend fun insert(transactionBasket: TransactionBasket): Long

    /**
     * Inserts [TransactionBasketItem]
     * @param transactionBasket [TransactionBasket] to insert the [item] to
     * @param item [Item] to insert to the [transactionBasket]
     * @return id of newly inserted [TransactionBasketItem]
     */
    suspend fun insertItemToBasket(
        transactionBasket: TransactionBasket,
        item: Item
    ): Long

    /**
     * Inserts [TransactionBasketItem]
     * @param transactionBasket [TransactionBasket] to insert the [item] to
     * @param item [FullItem] to insert to the [transactionBasket]
     * @return id of newly inserted [TransactionBasketItem]
     */
    suspend fun insertItemToBasket(
        transactionBasket: TransactionBasket,
        item: FullItem
    ): Long

    // Update

    /**
     * Updates matching [TransactionBasket] to provided [transactionBasket]
     *
     * Matches by id
     * @param transactionBasket [TransactionBasket] to update
     */
    suspend fun update(transactionBasket: TransactionBasket)

    /**
     * Updates all matching [TransactionBasket] to provided [transactionBaskets]
     *
     * Matches by id
     * @param transactionBaskets list of [TransactionBasket] to update
     */
    suspend fun update(transactionBaskets: List<TransactionBasket>)

    // Delete

    /**
     * Deletes [TransactionBasket]
     * @param transactionBasket [TransactionBasket] to delete
     */
    suspend fun delete(transactionBasket: TransactionBasket)

    /**
     * Deletes [TransactionBasket]
     * @param transactionBaskets list of [TransactionBasket] to delete
     */
    suspend fun delete(transactionBaskets: List<TransactionBasket>)

    /**
     * Deletes [TransactionBasketItem]
     * @param transactionBasketItem [TransactionBasket] to delete
     */
    suspend fun deleteTransactionBasketItem(transactionBasketItem: TransactionBasketItem)

    /**
     * Deletes [TransactionBasketItem]
     * @param transactionBasketItems list of [TransactionBasket] to delete
     */
    suspend fun deleteTransactionBasketItem(transactionBasketItems: List<TransactionBasketItem>)

    // Read

    /**
     * @param transactionBasketId id of the [TransactionBasket]
     * @return [TransactionBasket] matching [transactionBasketId] id or null if none match
     */
    suspend fun get(transactionBasketId: Long): TransactionBasket?

    /**
     * @return float representing total spending for the [category] as flow
     */
    fun totalSpentFlow(): Flow<Float>

    /**
     * @return list of [ItemSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(): Flow<List<ItemSpentByTime>>

    /**
     * @return list of [ItemSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(): Flow<List<ItemSpentByTime>>

    /**
     * @return list of [ItemSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(): Flow<List<ItemSpentByTime>>

    /**
     * @return list of [ItemSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(): Flow<List<ItemSpentByTime>>

    /**
     * @return list of all [TransactionBasket]
     */
    suspend fun all(): List<TransactionBasket>

    /**
     * @return list of all [TransactionBasket] as flow
     */
    fun allFlow(): Flow<List<TransactionBasket>>

    /**
     * @return list of all [TransactionBasketWithItems] as flow
     */
    fun allTransactionBasketsWithItemsFlow(): Flow<List<TransactionBasketWithItems>>
}