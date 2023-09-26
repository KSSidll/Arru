package com.kssidll.arrugarq.domain.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface IShopRepository {
    suspend fun getAll(): List<Shop>
    fun getAllFlow(): Flow<List<Shop>>
    suspend fun get(id: Long): Shop?
    fun getFlow(id: Long): Flow<Shop>
    suspend fun getByName(name: String): Shop?
    fun getByNameFlow(name: String): Flow<Shop>
    suspend fun insert(shop: Shop): Long
    suspend fun update(shop: Shop)
    suspend fun delete(shop: Shop)
}