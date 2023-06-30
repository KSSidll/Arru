package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.Item
import kotlinx.coroutines.flow.Flow

interface IItemRepository {
    suspend fun getAll(): List<Item>
    fun getAllFlow(): Flow<List<Item>>
    suspend fun get(id: Long): Item
    fun getFlow(id: Long): Flow<Item>
    suspend fun getLast(): Item
    fun getLastFlow(): Flow<Item>
    suspend fun getByProductId(productId: Long): List<Item>
    fun getByProductIdFlow(productId: Long): Flow<List<Item>>
    suspend fun getByShopId(shopId: Long): List<Item>
    fun getByShopIdFlow(shopId: Long): Flow<List<Item>>
    suspend fun getNewerThan(date: Long): List<Item>
    fun getNewerThanFlow(date: Long): Flow<List<Item>>
    suspend fun getOlderThan(date: Long): List<Item>
    fun getOlderThanFlow(date: Long): Flow<List<Item>>
    suspend fun getBetweenDates(lowerBoundDate: Long, higherBoundDate: Long): List<Item>
    fun getBetweenDatesFlow(lowerBoundDate: Long, higherBoundDate: Long): Flow<List<Item>>
    suspend fun insert(item: Item): Long
    suspend fun update(item: Item)
    suspend fun delete (item: Item)
}