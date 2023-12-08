package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class VariantRepository(private val dao: VariantDao): VariantRepositorySource {
    override suspend fun getAll(): List<ProductVariant> {
        return dao.getAll()
    }

    override fun getAllFlow(): Flow<List<ProductVariant>> {
        return dao.getAllFlow()
    }

    override suspend fun get(id: Long): ProductVariant? {
        return dao.get(id)
    }

    override fun getFlow(id: Long): Flow<ProductVariant> {
        return dao.getFlow(id)
    }

    override suspend fun getByProductIdAndName(
        productId: Long,
        name: String
    ): ProductVariant? {
        return dao.getByProductIdAndName(
            productId,
            name
        )
    }

    override suspend fun getByProductId(productId: Long): List<ProductVariant> {
        return dao.getByProductId(productId)
    }

    override fun getByProductIdFlow(productId: Long): Flow<List<ProductVariant>> {
        return dao.getByProductIdFlow(productId)
    }

    override suspend fun getByName(name: String): List<ProductVariant> {
        return dao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<List<ProductVariant>> {
        return dao.getByNameFlow(name)
    }

    override suspend fun insert(variant: ProductVariant): Long {
        return dao.insert(variant)
    }

    override suspend fun update(variant: ProductVariant) {
        dao.update(variant)
    }

    override suspend fun delete(variant: ProductVariant) {
        dao.delete(variant)
    }

    override suspend fun delete(variants: List<ProductVariant>) {
        dao.delete(variants)
    }

}