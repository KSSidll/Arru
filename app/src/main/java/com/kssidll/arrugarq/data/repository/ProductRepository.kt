package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.ProductDao
import com.kssidll.arrugarq.data.data.Product
import com.kssidll.arrugarq.data.data.ProductAltName
import com.kssidll.arrugarq.data.data.ProductWithAltNames
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

    override suspend fun getByProducerId(producerId: Long): List<Product> {
        return productDao.getByProducerId(producerId)
    }

    override fun getByProducerIdFlow(producerId: Long): Flow<List<Product>> {
        return productDao.getByProducerIdFlow(producerId)
    }

    override suspend fun getByName(name: String): Product {
        return productDao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<Product> {
        return productDao.getByNameFlow(name)
    }

    override suspend fun findLike(name: String): List<Product> {
        return productDao.findLike(name)
    }

    override fun findLikeFlow(name: String): Flow<List<Product>> {
        return productDao.findLikeFlow(name)
    }

    override suspend fun getAllWithAltNames(): List<ProductWithAltNames> {
        return productDao.getAllWithAltNames()
    }

    override fun getAllWithAltNamesFlow(): Flow<List<ProductWithAltNames>> {
        return productDao.getAllWithAltNamesFlow()
    }

    override suspend fun insert(product: Product): Long {
        return productDao.insert(product)
    }

    override suspend fun addAltName(alternativeName: ProductAltName): Long {
        return productDao.addAltName(alternativeName)
    }

    override suspend fun update(product: Product) {
        productDao.update(product)
    }

    override suspend fun updateAltName(alternativeName: ProductAltName) {
        productDao.updateAltName(alternativeName)
    }

    override suspend fun delete(product: Product) {
        productDao.delete(product)
    }

    override suspend fun deleteAltName(alternativeName: ProductAltName) {
        productDao.deleteAltName(alternativeName)
    }
}