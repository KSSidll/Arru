package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface ProductRepositorySource {
    suspend fun getAll(): List<Product>
    fun getAllFlow(): Flow<List<Product>>
    suspend fun get(id: Long): Product?
    fun getFlow(id: Long): Flow<Product>
    suspend fun getByCategoryId(categoryId: Long): List<Product>
    fun getByCategoryIdFlow(categoryId: Long): Flow<List<Product>>
    suspend fun getByProducerId(producerId: Long): List<Product>
    fun getByProducerIdFlow(producerId: Long): Flow<List<Product>>
    suspend fun getByName(name: String): Product?
    fun getByNameFlow(name: String): Flow<Product>
    suspend fun findLike(name: String): List<Product>
    fun findLikeFlow(name: String): Flow<List<Product>>
    suspend fun getAllWithAltNames(): List<ProductWithAltNames>
    fun getAllWithAltNamesFlow(): Flow<List<ProductWithAltNames>>
    suspend fun insert(product: Product): Long
    suspend fun addAltName(alternativeName: ProductAltName): Long
    suspend fun update(product: Product)
    suspend fun updateAltName(alternativeName: ProductAltName)
    suspend fun delete(product: Product)
    suspend fun delete(products: List<Product>)
    suspend fun deleteAltName(alternativeName: ProductAltName)
}