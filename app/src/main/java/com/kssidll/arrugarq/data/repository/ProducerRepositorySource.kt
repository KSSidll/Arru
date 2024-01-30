package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface ProducerRepositorySource {
    // Create

    /**
     * Inserts [ProductProducer]
     * @param producer [ProductProducer] to insert
     * @return id of newly inserted [ProductProducer]
     */
    suspend fun insert(producer: ProductProducer): Long

    // Update

    /**
     * Updates matching [ProductProducer] to provided [producer]
     *
     * Matches by id
     * @param producer [ProductProducer] to update
     */
    suspend fun update(producer: ProductProducer)

    /**
     * Updates all matching [ProductProducer] to provided [producers]
     *
     * Matches by id
     * @param producers list of [ProductProducer] to update
     */
    suspend fun update(producers: List<ProductProducer>)

    // Delete

    /**
     * Deletes [ProductProducer]
     * @param producer [ProductProducer] to delete
     */
    suspend fun delete(producer: ProductProducer)

    /**
     * Deletes [ProductProducer]
     * @param producers list of [ProductProducer] to delete
     */
    suspend fun delete(producers: List<ProductProducer>)

    // Read

    /**
     * @param producerId id of the [ProductProducer]
     * @return [ProductProducer] matching [producerId] id or null if none match
     */
    suspend fun get(producerId: Long): ProductProducer?

    /**
     * @param producer [ProductProducer] to get the total spending from
     * @return long representing total spending for the [producer] as flow
     */
    fun totalSpentFlow(producer: ProductProducer): Flow<Long>

    /**
     * @param producer [ProductProducer] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>>

    /**
     * @param producer [ProductProducer] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>>

    /**
     * @param producer [ProductProducer] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>>

    /**
     * @param producer [ProductProducer] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>>

    /**
     * @param producer [ProductProducer] to match the items to
     * @param count how many items to return
     * @param offset how many items to skip before returning [count] items
     * @return list of [count] [FullItem] offset by [offset] that match the [producer]
     */
    suspend fun fullItems(
        producer: ProductProducer,
        count: Int,
        offset: Int
    ): List<FullItem>

    /**
     * @return list of all [ProductProducer] as flow
     */
    fun allFlow(): Flow<List<ProductProducer>>
}