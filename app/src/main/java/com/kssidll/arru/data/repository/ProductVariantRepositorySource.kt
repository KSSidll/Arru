package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductVariantRepositorySource {
    // Create

    suspend fun insert(entity: ProductVariantEntity): Long

    // Update

    suspend fun update(entity: ProductVariantEntity)

    suspend fun update(entity: List<ProductVariantEntity>)

    // Delete

    suspend fun delete(entity: ProductVariantEntity)

    suspend fun delete(entity: List<ProductVariantEntity>)

    // Read

    /**
     * @param id id of the [ProductVariantEntity]
     * @return [ProductVariantEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<ProductVariantEntity?>

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return list of all [ProductVariantEntity] matching [ProductCategoryEntity] id or null if
     *   none match
     */
    fun byProductCategory(id: Long): Flow<ImmutableList<ProductVariantEntity>>

    /**
     * @param id id of the [ProductProducerEntity]
     * @return list of all [ProductVariantEntity] matching [ProductProducerEntity] id or null if
     *   none match
     */
    fun byProductProducer(id: Long): Flow<ImmutableList<ProductVariantEntity>>

    /**
     * @param id of the [ProductEntity]
     * @param showGlobal whether to return global variants as well
     * @return list of [ProductVariantEntity] with matching [ProductEntity] [id]
     */
    fun byProduct(id: Long, showGlobal: Boolean): Flow<ImmutableList<ProductVariantEntity>>
}
