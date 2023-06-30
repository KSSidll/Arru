package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.ProductVariantDao
import com.kssidll.arrugarq.data.data.ProductVariant
import kotlinx.coroutines.flow.Flow

class ProductVariantRepository(private val productVariantDao: ProductVariantDao) : IProductVariantRepository {
    override suspend fun getAll(): List<ProductVariant> {
        return productVariantDao.getAll()
    }

    override fun getAllFlow(): Flow<List<ProductVariant>> {
        return productVariantDao.getAllFlow()
    }

    override suspend fun get(id: Long): ProductVariant? {
        return productVariantDao.get(id)
    }

    override fun getFlow(id: Long): Flow<ProductVariant> {
        return productVariantDao.getFlow(id)
    }

    override suspend fun getByProduct(productId: Long): List<ProductVariant> {
        return productVariantDao.getByProduct(productId)
    }

    override fun getByProductFlow(productId: Long): Flow<List<ProductVariant>> {
        return productVariantDao.getByProductFlow(productId)
    }

    override suspend fun getByName(name: String): List<ProductVariant> {
        return productVariantDao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<List<ProductVariant>> {
        return productVariantDao.getByNameFlow(name)
    }

    override suspend fun insert(variant: ProductVariant): Long {
        return productVariantDao.insert(variant)
    }

    override suspend fun update(variant: ProductVariant) {
        productVariantDao.update(variant)
    }

    override suspend fun delete(variant: ProductVariant) {
        productVariantDao.delete(variant)
    }

}