package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.ProductCategoryDao
import com.kssidll.arrugarq.data.data.ProductCategory
import kotlinx.coroutines.flow.Flow

class ProductCategoryRepository(private val productCategoryDao: ProductCategoryDao) : IProductCategoryRepository {
    override suspend fun getAll(): List<ProductCategory> {
        return productCategoryDao.getAll()
    }

    override fun getAllFlow(): Flow<List<ProductCategory>> {
        return productCategoryDao.getAllFlow()
    }

    override suspend fun get(id: Long): ProductCategory {
        return productCategoryDao.get(id)
    }

    override fun getFlow(id: Long): Flow<ProductCategory> {
        return productCategoryDao.getFlow(id)
    }

    override suspend fun getByTypeId(typeId: Long): List<ProductCategory> {
        return productCategoryDao.getByTypeId(typeId)
    }

    override fun getByTypeIdFlow(typeId: Long): Flow<List<ProductCategory>> {
        return productCategoryDao.getByTypeIdFlow(typeId)
    }

    override suspend fun getByName(name: String): ProductCategory {
        return productCategoryDao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<ProductCategory> {
        return productCategoryDao.getByNameFlow(name)
    }

    override suspend fun insert(productCategory: ProductCategory): Long {
        return productCategoryDao.insert(productCategory)
    }

    override suspend fun update(productCategory: ProductCategory) {
        productCategoryDao.update(productCategory)
    }

    override suspend fun delete(productCategory: ProductCategory) {
        productCategoryDao.delete(productCategory)
    }

}