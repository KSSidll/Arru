package com.kssidll.arrugarq.domain.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface IVariantRepository {
    suspend fun getAll(): List<ProductVariant>
    fun getAllFlow(): Flow<List<ProductVariant>>
    suspend fun get(id: Long): ProductVariant?
    fun getFlow(id: Long): Flow<ProductVariant>
    suspend fun getByProduct(productId: Long): List<ProductVariant>
    fun getByProductFlow(productId: Long): Flow<List<ProductVariant>>
    suspend fun getByName(name: String): List<ProductVariant>
    fun getByNameFlow(name: String): Flow<List<ProductVariant>>
    suspend fun insert(variant: ProductVariant): Long
    suspend fun update(variant: ProductVariant)
    suspend fun delete(variant: ProductVariant)
}