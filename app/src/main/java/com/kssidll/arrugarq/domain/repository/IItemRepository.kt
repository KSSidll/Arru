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
    suspend fun getEmbeddedItemsByShopSorted(
        offset: Int,
        count: Int,
        shopId: Long,
    ): List<EmbeddedItem>

    fun getEmbeddedItemsByShopSortedFlow(
        offset: Int,
        count: Int,
        shopId: Long,
    ): Flow<List<EmbeddedItem>>

    suspend fun getEmbeddedItemsByProductSorted(
        offset: Int,
        count: Int,
        productId: Long,
    ): List<EmbeddedItem>

    fun getEmbeddedItemsByProductSortedFlow(
        offset: Int,
        count: Int,
        productId: Long,
    ): Flow<List<EmbeddedItem>>

    suspend fun getEmbeddedItemsByProducerSorted(
        offset: Int,
        count: Int,
        producerId: Long,
    ): List<EmbeddedItem>

    fun getEmbeddedItemsByProducerSortedFlow(
        offset: Int,
        count: Int,
        producerId: Long,
    ): Flow<List<EmbeddedItem>>

    suspend fun getEmbeddedItemsByCategorySorted(
        offset: Int,
        count: Int,
        categoryId: Long,
    ): List<EmbeddedItem>

    fun getEmbeddedItemsByCategorySortedFlow(
        offset: Int,
        count: Int,
        categoryId: Long,
    ): Flow<List<EmbeddedItem>>

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

    suspend fun getFullItemsByProduct(
        offset: Int,
        count: Int,
        productId: Long,
    ): List<FullItem>

    fun getFullItemsByProductFlow(
        offset: Int,
        count: Int,
        productId: Long,
    ): Flow<List<FullItem>>

    suspend fun getFullItemsByProducer(
        offset: Int,
        count: Int,
        producerId: Long,
    ): List<FullItem>

    fun getFullItemsByProducerFlow(
        offset: Int,
        count: Int,
        producerId: Long,
    ): Flow<List<FullItem>>

    suspend fun getFullItemsByCategory(
        offset: Int,
        count: Int,
        categoryId: Long,
    ): List<FullItem>

    fun getFullItemsByCategoryFlow(
        offset: Int,
        count: Int,
        categoryId: Long,
    ): Flow<List<FullItem>>

    suspend fun getShopTotalSpent(): List<ItemSpentByShop>
    fun getShopTotalSpentFlow(): Flow<List<ItemSpentByShop>>
    suspend fun getCategoryTotalSpent(): List<ItemSpentByCategory>
    fun getCategoryTotalSpentFlow(): Flow<List<ItemSpentByCategory>>
    suspend fun getTotalSpent(): Long
    fun getTotalSpentFlow(): Flow<Long>
    suspend fun getTotalSpentByShop(shopId: Long): Long
    fun getTotalSpentByShopFlow(shopId: Long): Flow<Long>
    suspend fun getTotalSpentByProduct(productId: Long): Long
    fun getTotalSpentByProductFlow(productId: Long): Flow<Long>
    suspend fun getTotalSpentByProducer(producerId: Long): Long
    fun getTotalSpentByProducerFlow(producerId: Long): Flow<Long>
    suspend fun getTotalSpentByCategory(categoryId: Long): Long
    fun getTotalSpentByCategoryFlow(categoryId: Long): Flow<Long>
    suspend fun getTotalSpentByShopByDay(shopId: Long): List<ItemSpentByTime>
    fun getTotalSpentByShopByDayFlow(shopId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByProductByDay(productId: Long): List<ItemSpentByTime>
    fun getTotalSpentByProductByDayFlow(productId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByProducerByDay(producerId: Long): List<ItemSpentByTime>
    fun getTotalSpentByProducerByDayFlow(producerId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByCategoryByDay(categoryId: Long): List<ItemSpentByTime>
    fun getTotalSpentByCategoryByDayFlow(categoryId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByDay(): List<ItemSpentByTime>
    fun getTotalSpentByDayFlow(): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByShopByWeek(shopId: Long): List<ItemSpentByTime>
    fun getTotalSpentByShopByWeekFlow(shopId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByProductByWeek(productId: Long): List<ItemSpentByTime>
    fun getTotalSpentByProductByWeekFlow(productId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByProducerByWeek(producerId: Long): List<ItemSpentByTime>
    fun getTotalSpentByProducerByWeekFlow(producerId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByCategoryByWeek(categoryId: Long): List<ItemSpentByTime>
    fun getTotalSpentByCategoryByWeekFlow(categoryId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByWeek(): List<ItemSpentByTime>
    fun getTotalSpentByWeekFlow(): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByShopByMonth(shopId: Long): List<ItemSpentByTime>
    fun getTotalSpentByShopByMonthFlow(shopId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByProductByMonth(productId: Long): List<ItemSpentByTime>
    fun getTotalSpentByProductByMonthFlow(productId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByProducerByMonth(producerId: Long): List<ItemSpentByTime>
    fun getTotalSpentByProducerByMonthFlow(producerId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByCategoryByMonth(categoryId: Long): List<ItemSpentByTime>
    fun getTotalSpentByCategoryByMonthFlow(categoryId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByMonth(): List<ItemSpentByTime>
    fun getTotalSpentByMonthFlow(): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByShopByYear(shopId: Long): List<ItemSpentByTime>
    fun getTotalSpentByShopByYearFlow(shopId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByProductByYear(productId: Long): List<ItemSpentByTime>
    fun getTotalSpentByProductByYearFlow(productId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByProducerByYear(producerId: Long): List<ItemSpentByTime>
    fun getTotalSpentByProducerByYearFlow(producerId: Long): Flow<List<ItemSpentByTime>>
    suspend fun getTotalSpentByCategoryByYear(categoryId: Long): List<ItemSpentByTime>
    fun getTotalSpentByCategoryByYearFlow(categoryId: Long): Flow<List<ItemSpentByTime>>
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