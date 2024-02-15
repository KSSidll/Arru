package com.kssidll.arrugarq.data.repository

import androidx.paging.*
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
     * @param transactionBasketItem [TransactionBasketItem] to insert
     * @return id of newly inserted [TransactionBasketItem]
     */
    suspend fun insertTransactionItem(transactionBasketItem: TransactionBasketItem): Long

    // Update

    /**
     * Updates matching [TransactionBasket] to provided [transactionBasket]
     *
     * Matches by id
     * @param transactionBasket [TransactionBasket] to update
     */
    suspend fun update(transactionBasket: TransactionBasket)

    // Delete

    /**
     * Deletes [TransactionBasket]
     * @param transactionBasket [TransactionBasket] to delete
     */
    suspend fun delete(transactionBasket: TransactionBasket)

    /**
     * Deletes [TransactionBasketItem]
     * @param transactionBasketItem [TransactionBasket] to delete
     */
    suspend fun deleteTransactionBasketItem(transactionBasketItem: TransactionBasketItem)

    // Read

    /**
     * @param transactionBasketId id of the [TransactionBasket]
     * @return [TransactionBasket] matching [transactionBasketId] id or null if none match
     */
    suspend fun get(transactionBasketId: Long): TransactionBasket?

    /**
     * @return long representing total spending for the [category] as flow
     */
    fun totalSpentFlow(): Flow<Long>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(): Flow<List<TransactionSpentByTime>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(): Flow<List<TransactionSpentByTime>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(): Flow<List<TransactionSpentByTime>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(): Flow<List<TransactionSpentByTime>>

    /**
     * @param startPosition position, from 0 up, to get next [count] items from
     * @param count how many items to query
     * @return list of [count] [TransactionBasketWithItems] where the first item is the item at [startPosition]
     */
    suspend fun transactionBasketsWithItems(
        startPosition: Int,
        count: Int
    ): List<TransactionBasketWithItems>


    /**
     * @return [TransactionBasketWithItems] as [PagingData] as [Flow]
     */
    fun transactionBasketsPagedFlow(): Flow<PagingData<TransactionBasketWithItems>>
}