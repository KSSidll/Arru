package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class VariantRepository(private val dao: VariantDao): VariantRepositorySource {
    // Create

    override suspend fun insert(variant: ProductVariant): Long {
        return dao.insert(variant)
    }

    // Update

    override suspend fun update(variant: ProductVariant) {
        dao.update(variant)
    }

    override suspend fun update(variants: List<ProductVariant>) {
        dao.update(variants)
    }

    // Delete

    override suspend fun delete(variant: ProductVariant) {
        dao.delete(variant)
    }

    override suspend fun delete(variants: List<ProductVariant>) {
        dao.delete(variants)
    }

    // Read

    override suspend fun get(variantId: Long): ProductVariant? {
        return dao.get(variantId)
    }

    override fun byProductFlow(product: Product): Flow<List<ProductVariant>> {
        return dao.byProductFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }
}