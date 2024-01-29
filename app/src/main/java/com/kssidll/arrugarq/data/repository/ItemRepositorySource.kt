package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface ItemRepositorySource {
    // Create

    /**
     * Inserts [Item]
     * @param item [Item] to insert
     * @return id of newly inserted [Item]
     */
    suspend fun insert(item: Item): Long

    // Update

    /**
     * Updates matching [Item] to provided [item]
     *
     * Matches by id
     * @param item [Item] to update
     */
    suspend fun update(item: Item)

    /**
     * Updates all matching [Item] to provided [items]
     *
     * Matches by id
     * @param items list of [Item] to update
     */
    suspend fun update(items: List<Item>)

    // Delete

    /**
     * Deletes [Item]
     * @param item [Item] to delete
     */
    suspend fun delete(item: Item)

    /**
     * Deletes [Item]
     * @param items list of [Item] to delete
     */
    suspend fun delete(items: List<Item>)

    // Read

    /**
     * @param itemId id of the [Item]
     * @return [Item] with [itemId] id or null if none match
     */
    suspend fun get(itemId: Long): Item?

    /**
     * @return newest [Item], null if none found
     */
    suspend fun newest(): Item?

    /**
     * @return newest [Item] as flow
     */
    suspend fun newestFlow(): Flow<Item>
}