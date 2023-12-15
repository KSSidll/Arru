package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface VariantRepositorySource {
    suspend fun getAll(): List<ProductVariant>
    fun getAllFlow(): Flow<List<ProductVariant>>
    suspend fun get(id: Long): ProductVariant?
    fun getFlow(id: Long): Flow<ProductVariant>
    suspend fun getByProductIdAndName(
        productId: Long,
        name: String
    ): ProductVariant?

    suspend fun getByProductId(productId: Long): List<ProductVariant>
    fun getByProductIdFlow(productId: Long): Flow<List<ProductVariant>>
    suspend fun getByName(name: String): List<ProductVariant>
    fun getByNameFlow(name: String): Flow<List<ProductVariant>>
    suspend fun insert(variant: ProductVariant): Long
    suspend fun update(variant: ProductVariant)
    suspend fun update(variants: List<ProductVariant>)
    suspend fun delete(variant: ProductVariant)
    suspend fun delete(variants: List<ProductVariant>)
}