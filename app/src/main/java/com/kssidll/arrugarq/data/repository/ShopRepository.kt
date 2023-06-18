package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.ShopDao
import com.kssidll.arrugarq.data.data.Shop
import kotlinx.coroutines.flow.Flow

class ShopRepository(private val shopDao: ShopDao) : IShopRepository {
    override suspend fun getAll(): List<Shop> {
        return shopDao.getAll()
    }

    override fun getAllFlow(): Flow<List<Shop>> {
        return shopDao.getAllFlow()
    }

    override suspend fun get(id: Long): Shop {
        return shopDao.get(id)
    }

    override fun getFlow(id: Long): Flow<Shop> {
        return shopDao.getFlow(id)
    }

    override suspend fun getByName(name: String): Shop {
        return shopDao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<Shop> {
        return shopDao.getByNameFlow(name)
    }

    override suspend fun insert(shop: Shop): Long {
        return shopDao.insert(shop)
    }

    override suspend fun update(shop: Shop) {
        shopDao.update(shop)
    }

    override suspend fun delete(shop: Shop) {
        shopDao.delete(shop)
    }
}