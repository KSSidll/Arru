package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.ProductCategoryDao
import com.kssidll.arrugarq.data.data.ProductCategory
import com.kssidll.arrugarq.data.data.ProductCategoryAltName
import com.kssidll.arrugarq.data.data.ProductCategoryWithAltNames
import kotlinx.coroutines.flow.Flow

class ProductCategoryRepository(private val productCategoryDao: ProductCategoryDao) : IProductCategoryRepository {
    override suspend fun getAll(): List<ProductCategory> {
        return productCategoryDao.getAll()
    }

    override fun getAllFlow(): Flow<List<ProductCategory>> {
        return productCategoryDao.getAllFlow()
    }

    override suspend fun get(id: Long): ProductCategory? {
        return productCategoryDao.get(id)
    }

    override fun getFlow(id: Long): Flow<ProductCategory> {
        return productCategoryDao.getFlow(id)
    }

    override suspend fun getByName(name: String): ProductCategory? {
        return productCategoryDao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<ProductCategory> {
        return productCategoryDao.getByNameFlow(name)
    }

    override suspend fun findLike(name: String): List<ProductCategory> {
        return productCategoryDao.findLike(name)
    }

    override suspend fun findLikeFlow(name: String): Flow<List<ProductCategory>> {
        return productCategoryDao.findLikeFlow(name)
    }

    override suspend fun getAllWithAltNames(): List<ProductCategoryWithAltNames> {
        return productCategoryDao.getAllWithAltNames()
    }

    override fun getAllWithAltNamesFlow(): Flow<List<ProductCategoryWithAltNames>> {
        return productCategoryDao.getAllWithAltNamesFlow()
    }

    override suspend fun insert(productCategory: ProductCategory): Long {
        return productCategoryDao.insert(productCategory)
    }

    override suspend fun addAltName(alternativeName: ProductCategoryAltName): Long {
        return productCategoryDao.addAltName(alternativeName)
    }

    override suspend fun update(productCategory: ProductCategory) {
        productCategoryDao.update(productCategory)
    }

    override suspend fun updateAltName(alternativeName: ProductCategoryAltName) {
        productCategoryDao.updateAltName(alternativeName)
    }

    override suspend fun delete(productCategory: ProductCategory) {
        productCategoryDao.delete(productCategory)
    }

    override suspend fun deleteAltName(alternativeName: ProductCategoryAltName) {
        productCategoryDao.deleteAltName(alternativeName)
    }

}