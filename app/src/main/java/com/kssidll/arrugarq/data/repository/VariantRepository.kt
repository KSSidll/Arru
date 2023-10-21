package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.flow.*

class VariantRepository(private val dao: VariantDao): IVariantRepository {
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

    override suspend fun getByProduct(productId: Long): List<ProductVariant> {
        return dao.getByProduct(productId)
    }

    override fun getByProductFlow(productId: Long): Flow<List<ProductVariant>> {
        return dao.getByProductFlow(productId)
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

}