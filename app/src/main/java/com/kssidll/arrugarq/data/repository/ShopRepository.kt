package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.flow.*

class ShopRepository(private val dao: ShopDao): IShopRepository {
    override suspend fun getAll(): List<Shop> {
        return dao.getAll()
    }

    override fun getAllFlow(): Flow<List<Shop>> {
        return dao.getAllFlow()
    }

    override suspend fun get(id: Long): Shop? {
        return dao.get(id)
    }

    override fun getFlow(id: Long): Flow<Shop> {
        return dao.getFlow(id)
    }

    override suspend fun getByName(name: String): Shop? {
        return dao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<Shop> {
        return dao.getByNameFlow(name)
    }

    override suspend fun insert(shop: Shop): Long {
        return dao.insert(shop)
    }

    override suspend fun update(shop: Shop) {
        dao.update(shop)
    }

    override suspend fun delete(shop: Shop) {
        dao.delete(shop)
    }
}