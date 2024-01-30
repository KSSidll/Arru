package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class ShopRepository(private val dao: ShopDao): ShopRepositorySource {
    // Create

    override suspend fun insert(shop: Shop): Long {
        TODO("Not yet implemented")
    }

    // Update

    override suspend fun update(shop: Shop) {
        TODO("Not yet implemented")
    }

    override suspend fun update(shops: List<Shop>) {
        TODO("Not yet implemented")
    }

    // Delete

    override suspend fun delete(shop: Shop) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(shops: List<Shop>) {
        TODO("Not yet implemented")
    }

    // Read

    override suspend fun get(shopId: Long): Shop? {
//        TODO("Not yet implemented")
        return null
    }

    override fun totalSpentFlow(shop: Shop): Flow<Float> {
//        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByDayFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
//        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByWeekFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
//        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByMonthFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
//        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByYearFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
//        TODO("Not yet implemented")
        return emptyFlow()
    }

    override suspend fun fullItems(
        shop: Shop,
        count: Int,
        offset: Int
    ): List<FullItem> {
//        TODO("Not yet implemented")
        return emptyList()
    }

    override fun totalSpentByShopFlow(): Flow<List<ItemSpentByShop>> {
//        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun totalSpentByShopByMonthFlow(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByShop>> {
//        TODO("Not yet implemented")
        return emptyFlow()
    }

    override fun allFlow(): Flow<List<Shop>> {
//        TODO("Not yet implemented")
        return emptyFlow()
    }
}