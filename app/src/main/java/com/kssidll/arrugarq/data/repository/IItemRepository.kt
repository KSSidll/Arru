package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.Item
import kotlinx.coroutines.flow.Flow

interface IItemRepository {
    suspend fun getAll(): List<Item>
    fun getAllFlow(): Flow<List<Item>>
    suspend fun get(id: Long): Item
    fun getFlow(id: Long): Flow<Item>
    suspend fun getByName(name: String): List<Item>
    fun getByNameFlow(name: String): Flow<List<Item>>
    suspend fun insert(item: Item): Long
    suspend fun update(item: Item)
    suspend fun delete (item: Item)
}