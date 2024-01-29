package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface VariantRepositorySource {
    // Create

    /**
     * Inserts [ProductVariant]
     * @param variant [ProductVariant] to insert
     * @return id of newly inserted [ProductVariant]
     */
    suspend fun insert(variant: ProductVariant): Long

    // Update

    /**
     * Updates matching [ProductVariant] to provided [variant]
     *
     * Matches by id
     * @param variant [ProductVariant] to update
     */
    suspend fun update(variant: ProductVariant)

    /**
     * Updates all matching [ProductVariant] to provided [variants]
     *
     * Matches by id
     * @param variants list of [ProductVariant] to update
     */
    suspend fun update(variants: List<ProductVariant>)

    // Delete

    /**
     * Deletes [ProductVariant]
     * @param variant [ProductVariant] to delete
     */
    suspend fun delete(variant: ProductVariant)

    /**
     * Deletes [ProductVariant]
     * @param variants list of [ProductVariant] to delete
     */
    suspend fun delete(variants: List<ProductVariant>)

    // Read

    /**
     * @param variantId id of the [ProductVariant]
     * @return [ProductVariant] matching [variantId] id or null if none match
     */
    suspend fun get(variantId: Long): ProductVariant?

    /**
     * @param product [Product] to match the [ProductVariant] with
     * @return list of [ProductVariant] matching [product] as flow
     */
    fun byProductFlow(product: Product): Flow<List<ProductVariant>>
}