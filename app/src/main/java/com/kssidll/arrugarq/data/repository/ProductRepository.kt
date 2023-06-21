package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.ProductDao
import com.kssidll.arrugarq.data.data.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) : IProductRepository {
    override suspend fun getAll(): List<Product> {
        return productDao.getAll()
    }

    override fun getAllFlow(): Flow<List<Product>> {
        return productDao.getAllFlow()
    }

    override suspend fun get(id: Long): Product {
        return productDao.get(id)
    }

    override fun getFlow(id: Long): Flow<Product> {
        return productDao.getFlow(id)
    }

    override suspend fun getByCategoryId(categoryId: Long): List<Product> {
        return productDao.getByCategoryId(categoryId)
    }

    override fun getByCategoryIdFlow(categoryId: Long): Flow<List<Product>> {
        return productDao.getByCategoryIdFlow(categoryId)
    }

    override suspend fun getByName(name: String): Product {
        return productDao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<Product> {
        return productDao.getByNameFlow(name)
    }

    override suspend fun insert(product: Product): Long {
        return productDao.insert(product)
    }

    override suspend fun update(product: Product) {
        productDao.update(product)
    }

    override suspend fun delete(product: Product) {
        productDao.delete(product)
    }
}