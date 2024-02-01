package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class ShopRepository(private val dao: ShopDao): ShopRepositorySource {
    // Create

    override suspend fun insert(shop: Shop): Long {
        return dao.insert(shop)
    }

    // Update

    override suspend fun update(shop: Shop) {
        dao.update(shop)
    }

    override suspend fun update(shops: List<Shop>) {
        dao.update(shops)
    }

    // Delete

    override suspend fun delete(shop: Shop) {
        dao.delete(shop)
    }

    override suspend fun delete(shops: List<Shop>) {
        dao.delete(shops)
    }

    // Read

    override suspend fun get(shopId: Long): Shop? {
        return dao.get(shopId)
    }

    override fun totalSpentFlow(shop: Shop): Flow<Long> {
        return dao.totalSpentFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByDayFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByDayFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByWeekFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByWeekFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByMonthFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByMonthFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByYearFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByYearFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override suspend fun fullItems(
        shop: Shop,
        count: Int,
        offset: Int
    ): List<FullItem> {
        return dao.fullItems(
            shop.id,
            count,
            offset
        )
    }

    override fun totalSpentByShopFlow(): Flow<List<ItemSpentByShop>> {
        return dao.totalSpentByShopFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByShopByMonthFlow(
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

        return dao.totalSpentByShopByMonthFlow(date)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun allFlow(): Flow<List<Shop>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
    }
}