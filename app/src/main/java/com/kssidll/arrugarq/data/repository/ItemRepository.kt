package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.flow.*

class ItemRepository(private val itemDao: ItemDao): IItemRepository {
    override suspend fun getAll(): List<Item> {
        return itemDao.getAll()
    }

    override fun getAllFlow(): Flow<List<Item>> {
        return itemDao.getAllFlow()
    }

    override suspend fun get(id: Long): Item? {
        return itemDao.get(id)
    }

    override fun getFlow(id: Long): Flow<Item> {
        return itemDao.getFlow(id)
    }

    override suspend fun getLast(): Item? {
        return itemDao.getLast()
    }

    override fun getLastFlow(): Flow<Item> {
        return itemDao.getLastFlow()
    }

    override suspend fun getAllEmbeddedItemSorted(): List<EmbeddedItem> {
        return itemDao.getAllEmbeddedItemSorted()
    }

    override fun getAllEmbeddedItemSortedFlow(): Flow<List<EmbeddedItem>> {
        return itemDao.getAllEmbeddedItemSortedFlow()
    }

    override suspend fun getEmbeddedItemsByShopSorted(
        offset: Int,
        count: Int,
        shopId: Long
    ): List<EmbeddedItem> {
        return itemDao.getEmbeddedItemsByShopSorted(
            offset = offset,
            count = count,
            shopId = shopId,
        )
    }

    override fun getEmbeddedItemsByShopSortedFlow(
        offset: Int,
        count: Int,
        shopId: Long
    ): Flow<List<EmbeddedItem>> {
        return itemDao.getEmbeddedItemsByShopSortedFlow(
            offset = offset,
            count = count,
            shopId = shopId,
        )
    }

    override suspend fun getEmbeddedItemsByProductSorted(
        offset: Int,
        count: Int,
        productId: Long
    ): List<EmbeddedItem> {
        return itemDao.getEmbeddedItemsByProductSorted(
            offset = offset,
            count = count,
            productId = productId,
        )
    }

    override fun getEmbeddedItemsByProductSortedFlow(
        offset: Int,
        count: Int,
        productId: Long
    ): Flow<List<EmbeddedItem>> {
        return itemDao.getEmbeddedItemsByProductSortedFlow(
            offset = offset,
            count = count,
            productId = productId,
        )
    }

    override suspend fun getEmbeddedItemsByProducerSorted(
        offset: Int,
        count: Int,
        producerId: Long
    ): List<EmbeddedItem> {
        return itemDao.getEmbeddedItemsByProducerSorted(
            offset = offset,
            count = count,
            producerId = producerId,
        )
    }

    override fun getEmbeddedItemsByProducerSortedFlow(
        offset: Int,
        count: Int,
        producerId: Long
    ): Flow<List<EmbeddedItem>> {
        return itemDao.getEmbeddedItemsByProducerSortedFlow(
            offset = offset,
            count = count,
            producerId = producerId,
        )
    }

    override suspend fun getEmbeddedItemsByCategorySorted(
        offset: Int,
        count: Int,
        categoryId: Long
    ): List<EmbeddedItem> {
        return itemDao.getEmbeddedItemsByCategorySorted(
            offset = offset,
            count = count,
            categoryId = categoryId,
        )
    }

    override fun getEmbeddedItemsByCategorySortedFlow(
        offset: Int,
        count: Int,
        categoryId: Long
    ): Flow<List<EmbeddedItem>> {
        return itemDao.getEmbeddedItemsByCategorySortedFlow(
            offset = offset,
            count = count,
            categoryId = categoryId,
        )
    }

    override suspend fun getEmbeddedItemsSorted(
        offset: Int,
        count: Int,
    ): List<EmbeddedItem> {
        return itemDao.getEmbeddedItemsSorted(
            offset = offset,
            count = count,
        )
    }

    override fun getEmbeddedItemsSortedFlow(
        offset: Int,
        count: Int,
    ): Flow<List<EmbeddedItem>> {
        return itemDao.getEmbeddedItemsSortedFlow(
            offset = offset,
            count = count,
        )
    }

    override suspend fun getItemEmbeddedProduct(productId: Long): EmbeddedProduct {
        return itemDao.getItemEmbeddedProduct(productId)
    }

    override fun getItemEmbeddedProductFlow(productId: Long): Flow<EmbeddedProduct> {
        return itemDao.getItemEmbeddedProductFlow(productId)
    }

    override suspend fun getFullItems(
        offset: Int,
        count: Int,
    ): List<FullItem> {
        return itemDao.getFullItems(
            offset = offset,
            count = count,
        )
    }

    override fun getFullItemsFlow(
        offset: Int,
        count: Int,
    ): Flow<List<FullItem>> {
        return itemDao.getFullItemsFlow(
            offset = offset,
            count = count,
        )
    }

    override suspend fun getFullItemsByShop(
        offset: Int,
        count: Int,
        shopId: Long,
    ): List<FullItem> {
        return itemDao.getFullItemsByShop(
            offset = offset,
            count = count,
            shopId = shopId,
        )
    }

    override fun getFullItemsByShopFlow(
        offset: Int,
        count: Int,
        shopId: Long,
    ): Flow<List<FullItem>> {
        return itemDao.getFullItemsByShopFlow(
            offset = offset,
            count = count,
            shopId = shopId,
        )
    }

    override suspend fun getFullItemsByProduct(
        offset: Int,
        count: Int,
        productId: Long
    ): List<FullItem> {
        return itemDao.getFullItemsByProduct(
            offset = offset,
            count = count,
            productId = productId,
        )
    }

    override fun getFullItemsByProductFlow(
        offset: Int,
        count: Int,
        productId: Long
    ): Flow<List<FullItem>> {
        return itemDao.getFullItemsByProductFlow(
            offset = offset,
            count = count,
            productId = productId,
        )
    }

    override suspend fun getFullItemsByProducer(
        offset: Int,
        count: Int,
        producerId: Long
    ): List<FullItem> {
        return itemDao.getFullItemsByProducer(
            offset = offset,
            count = count,
            producerId = producerId,
        )
    }

    override fun getFullItemsByProducerFlow(
        offset: Int,
        count: Int,
        producerId: Long
    ): Flow<List<FullItem>> {
        return itemDao.getFullItemsByProducerFlow(
            offset = offset,
            count = count,
            producerId = producerId,
        )
    }

    override suspend fun getFullItemsByCategory(
        offset: Int,
        count: Int,
        categoryId: Long
    ): List<FullItem> {
        return itemDao.getFullItemsByCategory(
            offset = offset,
            count = count,
            categoryId = categoryId,
        )
    }

    override fun getFullItemsByCategoryFlow(
        offset: Int,
        count: Int,
        categoryId: Long
    ): Flow<List<FullItem>> {
        return itemDao.getFullItemsByCategoryFlow(
            offset = offset,
            count = count,
            categoryId = categoryId,
        )
    }

    override suspend fun getShopTotalSpent(): List<ItemSpentByShop> {
        return itemDao.getShopTotalSpent()
    }

    override fun getShopTotalSpentFlow(): Flow<List<ItemSpentByShop>> {
        return itemDao.getShopTotalSpentFlow()
    }

    override suspend fun getCategoryTotalSpent(): List<ItemSpentByCategory> {
        return itemDao.getCategoryTotalSpent()
    }

    override fun getCategoryTotalSpentFlow(): Flow<List<ItemSpentByCategory>> {
        return itemDao.getCategoryTotalSpentFlow()
    }

    override suspend fun getTotalSpent(): Long {
        return itemDao.getTotalSpent()
    }

    override fun getTotalSpentFlow(): Flow<Long> {
        return itemDao.getTotalSpentFlow()
    }

    override suspend fun getTotalSpentByShop(shopId: Long): Long {
        return itemDao.getTotalSpentByShop(shopId)
    }

    override fun getTotalSpentByShopFlow(shopId: Long): Flow<Long> {
        return itemDao.getTotalSpentByShopFlow(shopId)
    }

    override suspend fun getTotalSpentByProduct(productId: Long): Long {
        return itemDao.getTotalSpentByProduct(productId)
    }

    override fun getTotalSpentByProductFlow(productId: Long): Flow<Long> {
        return itemDao.getTotalSpentByProductFlow(productId)
    }

    override suspend fun getTotalSpentByProducer(producerId: Long): Long {
        return itemDao.getTotalSpentByProducer(producerId)
    }

    override fun getTotalSpentByProducerFlow(producerId: Long): Flow<Long> {
        return itemDao.getTotalSpentByProducerFlow(producerId)
    }

    override suspend fun getTotalSpentByCategory(categoryId: Long): Long {
        return itemDao.getTotalSpentByCategory(categoryId)
    }

    override fun getTotalSpentByCategoryFlow(categoryId: Long): Flow<Long> {
        return itemDao.getTotalSpentByCategoryFlow(categoryId)
    }

    override suspend fun getTotalSpentByShopByDay(shopId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByShopByDay(shopId)
    }

    override fun getTotalSpentByShopByDayFlow(shopId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByShopByDayFlow(shopId)
    }

    override suspend fun getTotalSpentByProductByDay(productId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByProductByDay(productId)
    }

    override fun getTotalSpentByProductByDayFlow(productId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByProductByDayFlow(productId)
    }

    override suspend fun getTotalSpentByProducerByDay(producerId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByProducerByDay(producerId)
    }

    override fun getTotalSpentByProducerByDayFlow(producerId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByProducerByDayFlow(producerId)
    }

    override suspend fun getTotalSpentByCategoryByDay(categoryId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByCategoryByDay(categoryId)
    }

    override fun getTotalSpentByCategoryByDayFlow(categoryId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByCategoryByDayFlow(categoryId)
    }

    override suspend fun getTotalSpentByDay(): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByDay()
    }

    override fun getTotalSpentByDayFlow(): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByDayFlow()
    }

    override suspend fun getTotalSpentByShopByWeek(shopId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByShopByWeek(shopId)
    }

    override fun getTotalSpentByShopByWeekFlow(shopId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByShopByWeekFlow(shopId)
    }

    override suspend fun getTotalSpentByProductByWeek(productId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByProductByWeek(productId)
    }

    override fun getTotalSpentByProductByWeekFlow(productId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByProductByWeekFlow(productId)
    }

    override suspend fun getTotalSpentByProducerByWeek(producerId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByProducerByWeek(producerId)
    }

    override fun getTotalSpentByProducerByWeekFlow(producerId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByProducerByWeekFlow(producerId)
    }

    override suspend fun getTotalSpentByCategoryByWeek(categoryId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByCategoryByWeek(categoryId)
    }

    override fun getTotalSpentByCategoryByWeekFlow(categoryId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByCategoryByWeekFlow(categoryId)
    }

    override suspend fun getTotalSpentByWeek(): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByWeek()
    }

    override fun getTotalSpentByWeekFlow(): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByWeekFlow()
    }

    override suspend fun getTotalSpentByShopByMonth(shopId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByShopByMonth(shopId)
    }

    override fun getTotalSpentByShopByMonthFlow(shopId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByShopByMonthFlow(shopId)
    }

    override suspend fun getTotalSpentByProductByMonth(productId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByProductByMonth(productId)
    }

    override fun getTotalSpentByProductByMonthFlow(productId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByProductByMonthFlow(productId)
    }

    override suspend fun getTotalSpentByProducerByMonth(producerId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByProducerByMonth(producerId)
    }

    override fun getTotalSpentByProducerByMonthFlow(producerId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByProducerByMonthFlow(producerId)
    }

    override suspend fun getTotalSpentByCategoryByMonth(categoryId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByCategoryByMonth(categoryId)
    }

    override fun getTotalSpentByCategoryByMonthFlow(categoryId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByCategoryByMonthFlow(categoryId)
    }

    override suspend fun getTotalSpentByMonth(): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByMonth()
    }

    override fun getTotalSpentByMonthFlow(): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByMonthFlow()
    }

    override suspend fun getTotalSpentByShopByYear(shopId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByShopByYear(shopId)
    }

    override fun getTotalSpentByShopByYearFlow(shopId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByShopByYearFlow(shopId)
    }

    override suspend fun getTotalSpentByProductByYear(productId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByProductByYear(productId)
    }

    override fun getTotalSpentByProductByYearFlow(productId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByProductByYearFlow(productId)
    }

    override suspend fun getTotalSpentByProducerByYear(producerId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByProducerByYear(producerId)
    }

    override fun getTotalSpentByProducerByYearFlow(producerId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByProducerByYearFlow(producerId)
    }

    override suspend fun getTotalSpentByCategoryByYear(categoryId: Long): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByCategoryByYear(categoryId)
    }

    override fun getTotalSpentByCategoryByYearFlow(categoryId: Long): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByCategoryByYearFlow(categoryId)
    }

    override suspend fun getTotalSpentByYear(): List<ItemSpentByTime> {
        return itemDao.getTotalSpentByYear()
    }

    override fun getTotalSpentByYearFlow(): Flow<List<ItemSpentByTime>> {
        return itemDao.getTotalSpentByYearFlow()
    }

    override suspend fun getByProductId(productId: Long): List<Item> {
        return itemDao.getByProductId(productId)
    }

    override fun getByProductIdFlow(productId: Long): Flow<List<Item>> {
        return itemDao.getByProductIdFlow(productId)
    }

    override suspend fun getLastByProductId(productId: Long): Item? {
        return itemDao.getLastByProductId(productId)
    }

    override fun getLastByProductIdFlow(productId: Long): Flow<Item?> {
        return itemDao.getLastByProductIdFlow(productId)
    }

    override suspend fun getByVariant(variantId: Long): List<Item> {
        return itemDao.getByVariant(variantId)
    }

    override fun getByVariantFlow(variantId: Long): Flow<List<Item>> {
        return itemDao.getByVariantFlow(variantId)
    }

    override suspend fun getByShopId(shopId: Long): List<Item> {
        return itemDao.getByShopId(shopId)
    }

    override fun getByShopIdFlow(shopId: Long): Flow<List<Item>> {
        return itemDao.getByShopIdFlow(shopId)
    }

    override suspend fun getNewerThan(date: Long): List<Item> {
        return itemDao.getNewerThan(date)
    }

    override fun getNewerThanFlow(date: Long): Flow<List<Item>> {
        return itemDao.getNewerThanFlow(date)
    }

    override suspend fun getOlderThan(date: Long): List<Item> {
        return itemDao.getOlderThan(date)
    }

    override fun getOlderThanFlow(date: Long): Flow<List<Item>> {
        return itemDao.getOlderThanFlow(date)
    }

    override suspend fun getBetweenDates(
        lowerBoundDate: Long,
        higherBoundDate: Long,
    ): List<Item> {
        return itemDao.getBetweenDates(
            lowerBoundDate = lowerBoundDate,
            higherBoundDate = higherBoundDate,
        )
    }

    override fun getBetweenDatesFlow(
        lowerBoundDate: Long,
        higherBoundDate: Long,
    ): Flow<List<Item>> {
        return itemDao.getBetweenDatesFlow(
            lowerBoundDate = lowerBoundDate,
            higherBoundDate = higherBoundDate,
        )
    }

    override suspend fun insert(item: Item): Long {
        return itemDao.insert(item)
    }

    override suspend fun update(item: Item) {
        itemDao.update(item)
    }

    override suspend fun delete(item: Item) {
        itemDao.delete(item)
    }

}