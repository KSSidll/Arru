package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class ItemRepository(private val dao: ItemDao): ItemRepositorySource {
    override suspend fun getAll(): List<Item> {
        return dao.getAll()
    }

    override fun getAllFlow(): Flow<List<Item>> {
        return dao.getAllFlow()
    }

    override suspend fun get(id: Long): Item? {
        return dao.get(id)
    }

    override fun getFlow(id: Long): Flow<Item> {
        return dao.getFlow(id)
    }

    override suspend fun getLast(): Item? {
        return dao.getLast()
    }

    override fun getLastFlow(): Flow<Item> {
        return dao.getLastFlow()
    }

    override suspend fun getAllEmbeddedItemSorted(): List<EmbeddedItem> {
        return dao.getAllEmbeddedItemSorted()
    }

    override fun getAllEmbeddedItemSortedFlow(): Flow<List<EmbeddedItem>> {
        return dao.getAllEmbeddedItemSortedFlow()
    }

    override suspend fun getEmbeddedItemsByShopSorted(
        offset: Int,
        count: Int,
        shopId: Long
    ): List<EmbeddedItem> {
        return dao.getEmbeddedItemsByShopSorted(
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
        return dao.getEmbeddedItemsByShopSortedFlow(
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
        return dao.getEmbeddedItemsByProductSorted(
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
        return dao.getEmbeddedItemsByProductSortedFlow(
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
        return dao.getEmbeddedItemsByProducerSorted(
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
        return dao.getEmbeddedItemsByProducerSortedFlow(
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
        return dao.getEmbeddedItemsByCategorySorted(
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
        return dao.getEmbeddedItemsByCategorySortedFlow(
            offset = offset,
            count = count,
            categoryId = categoryId,
        )
    }

    override suspend fun getEmbeddedItemsSorted(
        offset: Int,
        count: Int,
    ): List<EmbeddedItem> {
        return dao.getEmbeddedItemsSorted(
            offset = offset,
            count = count,
        )
    }

    override fun getEmbeddedItemsSortedFlow(
        offset: Int,
        count: Int,
    ): Flow<List<EmbeddedItem>> {
        return dao.getEmbeddedItemsSortedFlow(
            offset = offset,
            count = count,
        )
    }

    override suspend fun getItemEmbeddedProduct(productId: Long): EmbeddedProduct {
        return dao.getItemEmbeddedProduct(productId)
    }

    override fun getItemEmbeddedProductFlow(productId: Long): Flow<EmbeddedProduct> {
        return dao.getItemEmbeddedProductFlow(productId)
    }

    override suspend fun getProductsAveragePriceByShopByMonthSorted(productId: Long): List<ProductPriceByShopByTime> {
        return dao.getProductsAveragePriceByShopByMonthSorted(productId)
    }

    override fun getProductsAveragePriceByShopByMonthSortedFlow(productId: Long): Flow<List<ProductPriceByShopByTime>> {
        return dao.getProductsAveragePriceByShopByMonthSortedFlow(productId)
    }

    override suspend fun getFullItems(
        offset: Int,
        count: Int,
    ): List<FullItem> {
        return dao.getFullItems(
            offset = offset,
            count = count,
        )
    }

    override fun getFullItemsFlow(
        offset: Int,
        count: Int,
    ): Flow<List<FullItem>> {
        return dao.getFullItemsFlow(
            offset = offset,
            count = count,
        )
    }

    override suspend fun getFullItemsByShop(
        offset: Int,
        count: Int,
        shopId: Long,
    ): List<FullItem> {
        return dao.getFullItemsByShop(
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
        return dao.getFullItemsByShopFlow(
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
        return dao.getFullItemsByProduct(
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
        return dao.getFullItemsByProductFlow(
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
        return dao.getFullItemsByProducer(
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
        return dao.getFullItemsByProducerFlow(
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
        return dao.getFullItemsByCategory(
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
        return dao.getFullItemsByCategoryFlow(
            offset = offset,
            count = count,
            categoryId = categoryId,
        )
    }

    override suspend fun getShopTotalSpent(): List<ItemSpentByShop> {
        return dao.getShopTotalSpent()
    }

    override fun getShopTotalSpentFlow(): Flow<List<ItemSpentByShop>> {
        return dao.getShopTotalSpentFlow()
    }

    override fun getShopTotalSpentFlowByMonth(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByShop>> {
        val date: String = buildString {
            append(year)
            append("-")

            val monthStr: String = if (month < 10) {
                "0$month"
            } else {
                month.toString()
            }
            append(monthStr)
        }
        return dao.getShopTotalSpentFlowByMonth(date)
    }

    override suspend fun getCategoryTotalSpent(): List<ItemSpentByCategory> {
        return dao.getCategoryTotalSpent()
    }

    override fun getCategoryTotalSpentFlow(): Flow<List<ItemSpentByCategory>> {
        return dao.getCategoryTotalSpentFlow()
    }

    override fun getCategoryTotalSpentFlowByMonth(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByCategory>> {
        val date: String = buildString {
            append(year)
            append("-")

            val monthStr: String = if (month < 10) {
                "0$month"
            } else {
                month.toString()
            }
            append(monthStr)
        }
        return dao.getCategoryTotalSpentFlowByMonth(date)
    }

    override suspend fun getTotalSpent(): Long {
        return dao.getTotalSpent()
    }

    override fun getTotalSpentFlow(): Flow<Long> {
        return dao.getTotalSpentFlow()
    }

    override suspend fun getTotalSpentByShop(shopId: Long): Long {
        return dao.getTotalSpentByShop(shopId)
    }

    override fun getTotalSpentByShopFlow(shopId: Long): Flow<Long> {
        return dao.getTotalSpentByShopFlow(shopId)
    }

    override suspend fun getTotalSpentByProduct(productId: Long): Long {
        return dao.getTotalSpentByProduct(productId)
    }

    override fun getTotalSpentByProductFlow(productId: Long): Flow<Long> {
        return dao.getTotalSpentByProductFlow(productId)
    }

    override suspend fun getTotalSpentByProducer(producerId: Long): Long {
        return dao.getTotalSpentByProducer(producerId)
    }

    override fun getTotalSpentByProducerFlow(producerId: Long): Flow<Long> {
        return dao.getTotalSpentByProducerFlow(producerId)
    }

    override suspend fun getTotalSpentByCategory(categoryId: Long): Long {
        return dao.getTotalSpentByCategory(categoryId)
    }

    override fun getTotalSpentByCategoryFlow(categoryId: Long): Flow<Long> {
        return dao.getTotalSpentByCategoryFlow(categoryId)
    }

    override suspend fun getTotalSpentByShopByDay(shopId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByShopByDay(shopId)
    }

    override fun getTotalSpentByShopByDayFlow(shopId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByShopByDayFlow(shopId)
    }

    override suspend fun getTotalSpentByProductByDay(productId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByProductByDay(productId)
    }

    override fun getTotalSpentByProductByDayFlow(productId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByProductByDayFlow(productId)
    }

    override suspend fun getTotalSpentByProducerByDay(producerId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByProducerByDay(producerId)
    }

    override fun getTotalSpentByProducerByDayFlow(producerId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByProducerByDayFlow(producerId)
    }

    override suspend fun getTotalSpentByCategoryByDay(categoryId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByCategoryByDay(categoryId)
    }

    override fun getTotalSpentByCategoryByDayFlow(categoryId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByCategoryByDayFlow(categoryId)
    }

    override suspend fun getTotalSpentByDay(): List<ItemSpentByTime> {
        return dao.getTotalSpentByDay()
    }

    override fun getTotalSpentByDayFlow(): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByDayFlow()
    }

    override suspend fun getTotalSpentByShopByWeek(shopId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByShopByWeek(shopId)
    }

    override fun getTotalSpentByShopByWeekFlow(shopId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByShopByWeekFlow(shopId)
    }

    override suspend fun getTotalSpentByProductByWeek(productId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByProductByWeek(productId)
    }

    override fun getTotalSpentByProductByWeekFlow(productId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByProductByWeekFlow(productId)
    }

    override suspend fun getTotalSpentByProducerByWeek(producerId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByProducerByWeek(producerId)
    }

    override fun getTotalSpentByProducerByWeekFlow(producerId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByProducerByWeekFlow(producerId)
    }

    override suspend fun getTotalSpentByCategoryByWeek(categoryId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByCategoryByWeek(categoryId)
    }

    override fun getTotalSpentByCategoryByWeekFlow(categoryId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByCategoryByWeekFlow(categoryId)
    }

    override suspend fun getTotalSpentByWeek(): List<ItemSpentByTime> {
        return dao.getTotalSpentByWeek()
    }

    override fun getTotalSpentByWeekFlow(): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByWeekFlow()
    }

    override suspend fun getTotalSpentByShopByMonth(shopId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByShopByMonth(shopId)
    }

    override fun getTotalSpentByShopByMonthFlow(shopId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByShopByMonthFlow(shopId)
    }

    override suspend fun getTotalSpentByProductByMonth(productId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByProductByMonth(productId)
    }

    override fun getTotalSpentByProductByMonthFlow(productId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByProductByMonthFlow(productId)
    }

    override suspend fun getTotalSpentByProducerByMonth(producerId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByProducerByMonth(producerId)
    }

    override fun getTotalSpentByProducerByMonthFlow(producerId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByProducerByMonthFlow(producerId)
    }

    override suspend fun getTotalSpentByCategoryByMonth(categoryId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByCategoryByMonth(categoryId)
    }

    override fun getTotalSpentByCategoryByMonthFlow(categoryId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByCategoryByMonthFlow(categoryId)
    }

    override suspend fun getTotalSpentByMonth(): List<ItemSpentByTime> {
        return dao.getTotalSpentByMonth()
    }

    override fun getTotalSpentByMonthFlow(): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByMonthFlow()
    }

    override suspend fun getTotalSpentByShopByYear(shopId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByShopByYear(shopId)
    }

    override fun getTotalSpentByShopByYearFlow(shopId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByShopByYearFlow(shopId)
    }

    override suspend fun getTotalSpentByProductByYear(productId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByProductByYear(productId)
    }

    override fun getTotalSpentByProductByYearFlow(productId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByProductByYearFlow(productId)
    }

    override suspend fun getTotalSpentByProducerByYear(producerId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByProducerByYear(producerId)
    }

    override fun getTotalSpentByProducerByYearFlow(producerId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByProducerByYearFlow(producerId)
    }

    override suspend fun getTotalSpentByCategoryByYear(categoryId: Long): List<ItemSpentByTime> {
        return dao.getTotalSpentByCategoryByYear(categoryId)
    }

    override fun getTotalSpentByCategoryByYearFlow(categoryId: Long): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByCategoryByYearFlow(categoryId)
    }

    override suspend fun getTotalSpentByYear(): List<ItemSpentByTime> {
        return dao.getTotalSpentByYear()
    }

    override fun getTotalSpentByYearFlow(): Flow<List<ItemSpentByTime>> {
        return dao.getTotalSpentByYearFlow()
    }

    override suspend fun getByProductId(productId: Long): List<Item> {
        return dao.getByProductId(productId)
    }

    override fun getByProductIdFlow(productId: Long): Flow<List<Item>> {
        return dao.getByProductIdFlow(productId)
    }

    override suspend fun getLastByProductId(productId: Long): Item? {
        return dao.getLastByProductId(productId)
    }

    override fun getLastByProductIdFlow(productId: Long): Flow<Item?> {
        return dao.getLastByProductIdFlow(productId)
    }

    override suspend fun getByVariantId(variantId: Long): List<Item> {
        return dao.getByVariantId(variantId)
    }

    override fun getByVariantIdFlow(variantId: Long): Flow<List<Item>> {
        return dao.getByVariantIdFlow(variantId)
    }

    override suspend fun getByShopId(shopId: Long): List<Item> {
        return dao.getByShopId(shopId)
    }

    override fun getByShopIdFlow(shopId: Long): Flow<List<Item>> {
        return dao.getByShopIdFlow(shopId)
    }

    override suspend fun getNewerThan(date: Long): List<Item> {
        return dao.getNewerThan(date)
    }

    override fun getNewerThanFlow(date: Long): Flow<List<Item>> {
        return dao.getNewerThanFlow(date)
    }

    override suspend fun getOlderThan(date: Long): List<Item> {
        return dao.getOlderThan(date)
    }

    override fun getOlderThanFlow(date: Long): Flow<List<Item>> {
        return dao.getOlderThanFlow(date)
    }

    override suspend fun getBetweenDates(
        lowerBoundDate: Long,
        higherBoundDate: Long,
    ): List<Item> {
        return dao.getBetweenDates(
            lowerBoundDate = lowerBoundDate,
            higherBoundDate = higherBoundDate,
        )
    }

    override fun getBetweenDatesFlow(
        lowerBoundDate: Long,
        higherBoundDate: Long,
    ): Flow<List<Item>> {
        return dao.getBetweenDatesFlow(
            lowerBoundDate = lowerBoundDate,
            higherBoundDate = higherBoundDate,
        )
    }

    override suspend fun insert(item: Item): Long {
        return dao.insert(item)
    }

    override suspend fun update(item: Item) {
        dao.update(item)
    }

    override suspend fun delete(item: Item) {
        dao.delete(item)
    }

    override suspend fun delete(items: List<Item>) {
        dao.delete(items)
    }

}