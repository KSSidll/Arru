package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.ProductCategoryTypeDao
import com.kssidll.arrugarq.data.data.ProductCategoryType
import kotlinx.coroutines.flow.Flow

class ProductCategoryTypeRepository(private val productCategoryTypeDao: ProductCategoryTypeDao) : IProductCategoryTypeRepository {
    override suspend fun getAll(): List<ProductCategoryType> {
        return productCategoryTypeDao.getAll()
    }

    override fun getAllFlow(): Flow<List<ProductCategoryType>> {
        return productCategoryTypeDao.getAllFlow()
    }

    override suspend fun get(id: Long): ProductCategoryType {
        return productCategoryTypeDao.get(id)
    }

    override fun getFlow(id: Long): Flow<ProductCategoryType> {
        return productCategoryTypeDao.getFlow(id)
    }

    override suspend fun getByName(name: String): ProductCategoryType {
        return productCategoryTypeDao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<ProductCategoryType> {
        return productCategoryTypeDao.getByNameFlow(name)
    }

    override suspend fun insert(productCategoryType: ProductCategoryType): Long {
        return productCategoryTypeDao.insert(productCategoryType)
    }

    override suspend fun update(productCategoryType: ProductCategoryType) {
        productCategoryTypeDao.update(productCategoryType)
    }

    override suspend fun delete(productCategoryType: ProductCategoryType) {
        productCategoryTypeDao.delete(productCategoryType)
    }
}