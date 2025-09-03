package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductProducerRepositorySource {
    // Create

    suspend fun insert(entity: ProductProducerEntity): Long

    // Update

    suspend fun update(entity: ProductProducerEntity)

    // Delete

    suspend fun delete(entity: ProductProducerEntity)

    // Read

    /**
     * @param id id of the [ProductProducerEntity]
     * @return [ProductProducerEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<ProductProducerEntity?>

    /**
     * @param name name of the [ProductProducerEntity]
     * @return [ProductProducerEntity] matching [name] name or null if none match
     */
    fun byName(name: String): Flow<ProductProducerEntity?>

    /** @return list of all [ProductProducerEntity] */
    fun all(): Flow<ImmutableList<ProductProducerEntity>>

    /**
     * @param id id of the [ProductProducerEntity]
     * @return float representing total spending for [ProductProducerEntity] matching [id] id or
     *   null if none match
     */
    fun totalSpent(id: Long): Flow<Float?>

    /**
     * @param id id of the [ProductProducerEntity]
     * @return [PagingData] of [Item] that is of [ProductProducerEntity] [id]
     */
    fun itemsFor(id: Long): Flow<PagingData<Item>>

    /**
     * @param id id of the [ProductProducerEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by day
     */
    fun totalSpentByDay(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductProducerEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by week
     */
    fun totalSpentByWeek(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductProducerEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by month
     */
    fun totalSpentByMonth(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductProducerEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by year
     */
    fun totalSpentByYear(id: Long): Flow<ImmutableList<ItemSpentChartData>>
}
