package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ProductVariantEntityDao
import com.kssidll.arru.data.data.ProductVariantEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map

class ProductVariantRepository(private val dao: ProductVariantEntityDao) :
    ProductVariantRepositorySource {
    // Create

    override suspend fun insert(entity: ProductVariantEntity): Long = dao.insert(entity)

    // Update

    override suspend fun update(entity: ProductVariantEntity) = dao.update(entity)

    override suspend fun update(entity: List<ProductVariantEntity>) = dao.update(entity)

    // Delete

    override suspend fun delete(entity: ProductVariantEntity) = dao.delete(entity)

    override suspend fun delete(entity: List<ProductVariantEntity>) = dao.delete(entity)

    // Read

    override fun get(id: Long): Flow<ProductVariantEntity?> = dao.get(id).cancellable()

    override fun byProductCategory(id: Long): Flow<ImmutableList<ProductVariantEntity>> =
        dao.byProductCategory(id).cancellable().map { it.toImmutableList() }

    override fun byProductProducer(id: Long): Flow<ImmutableList<ProductVariantEntity>> =
        dao.byProductProducer(id).cancellable().map { it.toImmutableList() }

    override fun byProduct(
        id: Long,
        showGlobal: Boolean,
    ): Flow<ImmutableList<ProductVariantEntity>> =
        dao.byProduct(id, showGlobal).cancellable().map { it.toImmutableList() }
}
