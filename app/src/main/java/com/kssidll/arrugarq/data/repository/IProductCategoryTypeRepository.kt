package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.ProductCategoryType
import kotlinx.coroutines.flow.Flow

interface IProductCategoryTypeRepository {
    suspend fun getAll(): List<ProductCategoryType>
    fun getAllFlow(): Flow<List<ProductCategoryType>>
    suspend fun get(id: Long): ProductCategoryType
    fun getFlow(id: Long): Flow<ProductCategoryType>
    suspend fun getByName(name: String): ProductCategoryType
    fun getByNameFlow(name: String): Flow<ProductCategoryType>
    suspend fun insert(productCategoryType: ProductCategoryType): Long
    suspend fun update(productCategoryType: ProductCategoryType)
    suspend fun delete(productCategoryType: ProductCategoryType)
}