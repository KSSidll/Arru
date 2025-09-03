package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.TotalSpentByCategory
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductCategoryRepositorySource {
    // Create

    suspend fun insert(entity: ProductCategoryEntity): Long

    // Update

    suspend fun update(entity: ProductCategoryEntity)

    // Delete

    suspend fun delete(entity: ProductCategoryEntity)

    // Read

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return [ProductCategoryEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<ProductCategoryEntity?>

    /**
     * @param name name of the [ProductCategoryEntity]
     * @return [ProductCategoryEntity] matching [name] name or null if none match
     */
    fun byName(name: String): Flow<ProductCategoryEntity?>

    /** @return list of all [ProductCategoryEntity] */
    fun all(): Flow<ImmutableList<ProductCategoryEntity>>

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return float representing total spending for [ProductCategoryEntity] matching [id] id or
     *   null if none match
     */
    fun totalSpent(id: Long): Flow<Float?>

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return [PagingData] of [Item] that is of [ProductCategoryEntity] [id]
     */
    fun itemsFor(id: Long): Flow<PagingData<Item>>

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by day
     */
    fun totalSpentByDay(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by week
     */
    fun totalSpentByWeek(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by month
     */
    fun totalSpentByMonth(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by year
     */
    fun totalSpentByYear(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /** @return list of [TotalSpentByCategory] representing total spending groupped by category */
    fun totalSpentByCategory(): Flow<ImmutableList<TotalSpentByCategory>>

    /**
     * @param year year to match the data to
     * @param month month to match the data to
     * @return list of [TotalSpentByCategory] representing total spending groupped by category in
     *   [year] and [month]
     */
    fun totalSpentByCategoryByMonth(
        year: Int,
        month: Int,
    ): Flow<ImmutableList<TotalSpentByCategory>>
}
