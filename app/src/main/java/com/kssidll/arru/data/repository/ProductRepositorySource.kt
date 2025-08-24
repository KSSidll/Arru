package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.data.ProductPriceByShopByVariantByProducerByTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductRepositorySource {
    // Create

    suspend fun insert(entity: ProductEntity): Long

    // Update

    suspend fun update(entity: ProductEntity)

    suspend fun update(entity: List<ProductEntity>)

    // Delete

    suspend fun delete(entity: ProductEntity)

    suspend fun delete(entity: List<ProductEntity>)

    // Read

    /**
     * @param id id of the [ProductEntity]
     * @return [ProductEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<ProductEntity?>

    /**
     * @param name name of the [ProductEntity]
     * @return [ProductEntity] matching [name] name or null if none match
     */
    fun byName(name: String): Flow<ProductEntity?>

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return list of all [ProductEntity] matching [ProductCategoryEntity] id or null if none match
     */
    fun byCategory(id: Long): Flow<ImmutableList<ProductEntity>>

    /**
     * @param id id of the [ProductProducerEntity]
     * @return list of all [ProductEntity] matching [ProductProducerEntity] id or null if none match
     */
    fun byProducer(id: Long): Flow<ImmutableList<ProductEntity>>

    /** @return list of all [ProductEntity] */
    fun all(): Flow<ImmutableList<ProductEntity>>

    /**
     * @param id id of the [ProductEntity]
     * @return float representing total spending for [ProductEntity] matching [id] id or null if
     *   none match
     */
    fun totalSpent(id: Long): Flow<Float?>

    /**
     * @param id id of the [ProductEntity]
     * @return [PagingData] of [Item] that is of [ProductEntity] [id]
     */
    fun itemsFor(id: Long): Flow<PagingData<Item>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by day
     */
    fun totalSpentByDay(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by week
     */
    fun totalSpentByWeek(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by month
     */
    fun totalSpentByMonth(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ItemSpentChartData] representing total spending partitioned by year
     */
    fun totalSpentByYear(id: Long): Flow<ImmutableList<ItemSpentChartData>>

    /**
     * @param id id of the [ProductEntity]
     * @return List of [ProductPriceByShopByVariantByProducerByTime] representing average spending
     *   partitioned by shop, variant, producer and day
     */
    fun averagePriceByShopByVariantByProducerByDay(
        id: Long
    ): Flow<ImmutableList<ProductPriceByShopByVariantByProducerByTime>>
}
