package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.flow.*

class CategoryRepository(private val dao: CategoryDao): ICategoryRepository {
    override suspend fun getAll(): List<ProductCategory> {
        return dao.getAll()
    }

    override fun getAllFlow(): Flow<List<ProductCategory>> {
        return dao.getAllFlow()
    }

    override suspend fun get(id: Long): ProductCategory? {
        return dao.get(id)
    }

    override fun getFlow(id: Long): Flow<ProductCategory> {
        return dao.getFlow(id)
    }

    override suspend fun getByName(name: String): ProductCategory? {
        return dao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<ProductCategory> {
        return dao.getByNameFlow(name)
    }

    override suspend fun findLike(name: String): List<ProductCategory> {
        return dao.findLike(name)
    }

    override suspend fun findLikeFlow(name: String): Flow<List<ProductCategory>> {
        return dao.findLikeFlow(name)
    }

    override suspend fun getAllWithAltNames(): List<ProductCategoryWithAltNames> {
        return dao.getAllWithAltNames()
    }

    override fun getAllWithAltNamesFlow(): Flow<List<ProductCategoryWithAltNames>> {
        return dao.getAllWithAltNamesFlow()
    }

    override suspend fun insert(productCategory: ProductCategory): Long {
        return dao.insert(productCategory)
    }

    override suspend fun addAltName(alternativeName: ProductCategoryAltName): Long {
        return dao.addAltName(alternativeName)
    }

    override suspend fun update(productCategory: ProductCategory) {
        dao.update(productCategory)
    }

    override suspend fun updateAltName(alternativeName: ProductCategoryAltName) {
        dao.updateAltName(alternativeName)
    }

    override suspend fun delete(productCategory: ProductCategory) {
        dao.delete(productCategory)
    }

    override suspend fun deleteAltName(alternativeName: ProductCategoryAltName) {
        dao.deleteAltName(alternativeName)
    }

}