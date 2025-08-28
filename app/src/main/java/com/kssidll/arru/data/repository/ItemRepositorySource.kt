package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.TransactionEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ItemRepositorySource {
    // Create

    suspend fun insert(entity: ItemEntity): Long

    // Update

    suspend fun update(entity: ItemEntity)

    suspend fun update(entity: List<ItemEntity>)

    // Delete

    suspend fun delete(entity: ItemEntity)

    suspend fun delete(entity: List<ItemEntity>)

    // Read

    /**
     * @param id id of the [ItemEntity]
     * @return [ItemEntity] with [id] id or null if none match
     */
    fun get(id: Long): Flow<ItemEntity?>

    /**
     * @param id id of the [ProductCategoryEntity]
     * @return list of all [ItemEntity] matching [ProductCategoryEntity] id or null if none match
     */
    fun byProductCategory(id: Long): Flow<ImmutableList<ItemEntity>>

    /**
     * @param id id of the [ProductProducerEntity]
     * @return list of all [ItemEntity] matching [ProductProducerEntity] id or null if none match
     */
    fun byProductProducer(id: Long): Flow<ImmutableList<ItemEntity>>

    /**
     * @param id id of the [ProductVariantEntity]
     * @return list of all [ItemEntity] matching [ProductVariantEntity] id or null if none match
     */
    fun byProductVariant(id: Long): Flow<ImmutableList<ItemEntity>>

    /**
     * @param id id of the [ProductEntity]
     * @return [ItemEntity] with matching [ProductEntity] id or null if none match
     */
    fun byProduct(id: Long): Flow<ImmutableList<ItemEntity>>

    /**
     * @param id id of the [TransactionEntity]
     * @return [ItemEntity] with matching [TransactionEntity] id or null if none match
     */
    fun byTransaction(id: Long): Flow<ImmutableList<ItemEntity>>

    /** @return newest [ItemEntity], null if none found */
    fun newest(): Flow<ItemEntity?>

    /**
     * @param id id of the [ProductEntity] to match by
     * @return newest [ItemEntity] matching [ProductEntity] of [id] id, null if none found
     */
    fun newestByProduct(id: Long): Flow<ItemEntity?>
}
