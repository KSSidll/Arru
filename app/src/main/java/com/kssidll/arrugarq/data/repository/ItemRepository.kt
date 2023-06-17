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

    override suspend fun get(id: Long): Item {
        return itemDao.get(id)
    }

    override fun getFlow(id: Long): Flow<Item> {
        return itemDao.getFlow(id)
    }

    override suspend fun getByName(name: String): List<Item> {
        return itemDao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<List<Item>> {
        return itemDao.getByNameFlow(name)
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