package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.IntermediateTransaction
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface TransactionRepositorySource {
    // Create

    suspend fun insert(entity: TransactionEntity): Long

    // Update

    suspend fun update(entity: TransactionEntity)

    suspend fun update(entity: List<TransactionEntity>)

    // Delete

    suspend fun delete(entity: TransactionEntity)

    suspend fun delete(entity: List<TransactionEntity>)

    // Read

    /**
     * @param id id of the [TransactionEntity]
     * @return [TransactionEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<TransactionEntity?>

    /**
     * @param id id of the [ShopEntity]
     * @return list of all [TransactionEntity] matching [ShopEntity] id or null if none match
     */
    fun byShop(id: Long): Flow<ImmutableList<TransactionEntity>>

    /** @return float representing total spending */
    fun totalSpent(): Flow<Float?>

    /** @return [PagingData] of all [IntermediateTransaction] */
    fun intermediates(): Flow<PagingData<IntermediateTransaction>>

    /**
     * @param id id of the [TransactionEntity]
     * @return [IntermediateTransaction] that is of [TransactionEntity] [id]
     */
    fun intermediateFor(id: Long): Flow<IntermediateTransaction?>

    /**
     * @return List of [TransactionSpentChartData] representing total spending partitioned by day
     */
    fun totalSpentByDay(): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @return List of [TransactionSpentChartData] representing total spending partitioned by week
     */
    fun totalSpentByWeek(): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @return List of [TransactionSpentChartData] representing total spending partitioned by month
     */
    fun totalSpentByMonth(): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @return List of [TransactionSpentChartData] representing total spending partitioned by year
     */
    fun totalSpentByYear(): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @return newest [TransactionEntity] (by time added, not transaction date) or null if none
     *   exist
     */
    fun newest(): Flow<TransactionEntity?>
}
