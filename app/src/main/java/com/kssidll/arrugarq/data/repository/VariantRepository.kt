package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class VariantRepository(private val dao: VariantDao): VariantRepositorySource {
    // Create

    override suspend fun insert(variant: ProductVariant): Long {
        TODO("Not yet implemented")
    }

    // Update

    override suspend fun update(variant: ProductVariant) {
        TODO("Not yet implemented")
    }

    override suspend fun update(variants: List<ProductVariant>) {
        TODO("Not yet implemented")
    }

    // Delete

    override suspend fun delete(variant: ProductVariant) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(variants: List<ProductVariant>) {
        TODO("Not yet implemented")
    }

    // Read

    override suspend fun get(variantId: Long): ProductVariant? {
        TODO("Not yet implemented")
    }

    override fun byProductFlow(product: Product): Flow<List<ProductVariant>> {
        TODO("Not yet implemented")
    }
}