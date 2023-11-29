package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface CategoryRepositorySource {
    suspend fun getAll(): List<ProductCategory>
    fun getAllFlow(): Flow<List<ProductCategory>>
    suspend fun get(id: Long): ProductCategory?
    fun getFlow(id: Long): Flow<ProductCategory>
    suspend fun getByName(name: String): ProductCategory?
    fun getByNameFlow(name: String): Flow<ProductCategory>
    suspend fun findLike(name: String): List<ProductCategory>
    suspend fun findLikeFlow(name: String): Flow<List<ProductCategory>>
    suspend fun getAllWithAltNames(): List<ProductCategoryWithAltNames>
    fun getAllWithAltNamesFlow(): Flow<List<ProductCategoryWithAltNames>>
    suspend fun insert(productCategory: ProductCategory): Long
    suspend fun addAltName(alternativeName: ProductCategoryAltName): Long
    suspend fun update(productCategory: ProductCategory)
    suspend fun updateAltName(alternativeName: ProductCategoryAltName)
    suspend fun delete(productCategory: ProductCategory)
    suspend fun deleteAltName(alternativeName: ProductCategoryAltName)
}