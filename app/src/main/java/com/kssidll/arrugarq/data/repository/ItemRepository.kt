package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.ItemDao
import com.kssidll.arrugarq.data.data.Item
import kotlinx.coroutines.flow.Flow

class ItemRepository(private val itemDao: ItemDao) : IItemRepository {
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
        higherBoundDate: Long
    ): List<Item> {
        return itemDao.getBetweenDates(lowerBoundDate, higherBoundDate)
    }

    override fun getBetweenDatesFlow(
        lowerBoundDate: Long,
        higherBoundDate: Long
    ): Flow<List<Item>> {
        return itemDao.getBetweenDatesFlow(lowerBoundDate, higherBoundDate)
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