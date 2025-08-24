package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TotalSpentByShop
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ShopRepositorySource {
    // Create

    suspend fun insert(entity: ShopEntity): Long

    // Update

    suspend fun update(entity: ShopEntity)

    // Delete

    suspend fun delete(entity: ShopEntity)

    // Read

    /**
     * @param id id of the [ShopEntity]
     * @return [ShopEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<ShopEntity?>

    /** @return list of all [ShopEntity] */
    fun all(): Flow<ImmutableList<ShopEntity>>

    /**
     * @param name name of the [ShopEntity]
     * @return [ShopEntity] matching [name] name or null if none match
     */
    fun byName(name: String): Flow<ShopEntity?>

    /**
     * @param id id of the [ShopEntity]
     * @return float representing total spending for [ShopEntity] matching [id] id or null if none
     *   match
     */
    fun totalSpent(id: Long): Flow<Float?>

    /**
     * @param id id of the [ShopEntity]
     * @return [PagingData] of [Item] that is of [ShopEntity] [id]
     */
    fun itemsFor(id: Long): Flow<PagingData<Item>>

    /**
     * @param id id of the [ShopEntity]
     * @return List of [TransactionSpentChartData] representing total spending partitioned by day
     */
    fun totalSpentByDay(id: Long): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @param id id of the [ShopEntity]
     * @return List of [TransactionSpentChartData] representing total spending partitioned by week
     */
    fun totalSpentByWeek(id: Long): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @param id id of the [ShopEntity]
     * @return List of [TransactionSpentChartData] representing total spending partitioned by month
     */
    fun totalSpentByMonth(id: Long): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @param id id of the [ShopEntity]
     * @return List of [TransactionSpentChartData] representing total spending partitioned by year
     */
    fun totalSpentByYear(id: Long): Flow<ImmutableList<TransactionSpentChartData>>

    /** @return list of [TotalSpentByShop] representing total spending groupped by shop */
    fun totalSpentByShop(): Flow<ImmutableList<TotalSpentByShop>>

    /**
     * @param year year to match the data to
     * @param month month to match the data to
     * @return list of [TotalSpentByShop] representing total spending groupped by shop in [year] and
     *   [month]
     */
    fun totalSpentByShopByMonth(year: Int, month: Int): Flow<ImmutableList<TotalSpentByShop>>
}
