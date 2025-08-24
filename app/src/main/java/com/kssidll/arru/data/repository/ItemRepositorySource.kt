package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
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
    fun byCategory(id: Long): Flow<ImmutableList<ItemEntity>>

    /**
     * @param id id of the [ProductEntity]
     * @return [ItemEntity] with matching [ProductEntity] id or null if none match
     */
    fun byProduct(id: Long): Flow<ImmutableList<ItemEntity>>

    /** @return newest [ItemEntity], null if none found */
    fun newest(): Flow<ItemEntity?>

    /**
     * @param id id of the [ProductEntity] to match by
     * @return newest [ItemEntity] matching [ProductEntity] of [id] id, null if none found
     */
    fun newestByProduct(id: Long): Flow<ItemEntity?>
}
