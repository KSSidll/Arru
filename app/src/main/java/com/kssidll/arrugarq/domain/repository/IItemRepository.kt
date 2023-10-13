package com.kssidll.arrugarq.domain.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface IItemRepository {
    suspend fun getAll(): List<Item>
    fun getAllFlow(): Flow<List<Item>>
    suspend fun get(id: Long): Item?
    fun getFlow(id: Long): Flow<Item>
    suspend fun getLast(): Item?
    fun getLastFlow(): Flow<Item>
    suspend fun getAllEmbeddedItemSorted(): List<EmbeddedItem>
    fun getAllEmbeddedItemSortedFlow(): Flow<List<EmbeddedItem>>
    suspend fun getEmbeddedItemsSorted(
        offset: Int,
        count: Int
    ): List<EmbeddedItem>

    fun getEmbeddedItemsSortedFlow(
        offset: Int,
        count: Int
    ): Flow<List<EmbeddedItem>>

    suspend fun getItemEmbeddedProduct(productId: Long): EmbeddedProduct
    fun getItemEmbeddedProductFlow(productId: Long): Flow<EmbeddedProduct>
    suspend fun getFullItems(
        offset: Int,
        count: Int,
    ): List<FullItem>

    fun getFullItemsFlow(
        offset: Int,
        count: Int,
    ): Flow<List<FullItem>>

    suspend fun getFullItemsByShop(
        offset: Int,
        count: Int,
        shopId: Long,
    ): List<FullItem>

    fun getFullItemsByShopFlow(
        offset: Int,
        count: Int,
        shopId: Long,
    ): Flow<List<FullItem>>

    suspend fun getShopTotalSpent(): List<ItemSpentByShop>
    fun getShopTotalSpentFlow(): Flow<List<ItemSpentByShop>>
    suspend fun getCategoryTotalSpent(): List<ItemSpentByCategory>
    fun getCategoryTotalSpentFlow(): Flow<List<ItemSpentByCategory>>
    suspend fun getTotalSpent(): Long
    fun getTotalSpentFlow(): Flow<Long>
    suspend fun getTotalSpentByShopByDay(shopId: Long): List<ItemSpentByTime>
    fun getTotalSpentByShopByDayFlow(shopId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByDay(): List<ItemSpentByTime>
    fun getTotalSpentByDayFlow(): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByWeek(): List<ItemSpentByTime>
    fun getTotalSpentByWeekFlow(): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByMonth(): List<ItemSpentByTime>
    fun getTotalSpentByMonthFlow(): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByYear(): List<ItemSpentByTime>
    fun getTotalSpentByYearFlow(): Flow<List<ItemSpentByTime>>
    suspend fun getByProductId(productId: Long): List<Item>
    fun getByProductIdFlow(productId: Long): Flow<List<Item>>
    suspend fun getLastByProductId(productId: Long): Item?
    fun getLastByProductIdFlow(productId: Long): Flow<Item?>
    suspend fun getByVariant(variantId: Long): List<Item>
    fun getByVariantFlow(variantId: Long): Flow<List<Item>>
    suspend fun getByShopId(shopId: Long): List<Item>
    fun getByShopIdFlow(shopId: Long): Flow<List<Item>>
    suspend fun getNewerThan(date: Long): List<Item>
    fun getNewerThanFlow(date: Long): Flow<List<Item>>
    suspend fun getOlderThan(date: Long): List<Item>
    fun getOlderThanFlow(date: Long): Flow<List<Item>>
    suspend fun getBetweenDates(
        lowerBoundDate: Long,
        higherBoundDate: Long
    ): List<Item>

    fun getBetweenDatesFlow(
        lowerBoundDate: Long,
        higherBoundDate: Long
    ): Flow<List<Item>>

    suspend fun insert(item: Item): Long
    suspend fun update(item: Item)
    suspend fun delete(item: Item)
}