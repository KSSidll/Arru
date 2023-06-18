package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.Product
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    suspend fun getAll(): List<Product>
    fun getAllFlow(): Flow<List<Product>>
    suspend fun get(id: Long): Product
    fun getFlow(id: Long): Flow<Product>
    suspend fun getByCategoryId(categoryId: Long): List<Product>
    fun getByCategoryIdFlow(categoryId: Long): Flow<List<Product>>
    suspend fun getByName(name: String): Product
    fun getByNameFlow(name: String): Flow<Product>
    suspend fun insert(product: Product): Long
    suspend fun update(product: Product)
    suspend fun delete(product: Product)
}