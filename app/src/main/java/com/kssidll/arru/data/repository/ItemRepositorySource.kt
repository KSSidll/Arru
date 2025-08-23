package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductEntity
import kotlinx.coroutines.flow.Flow

interface ItemRepositorySource {
    companion object {
        sealed class DeleteResult(val error: Errors? = null) {
            data object Success : DeleteResult()

            class Error(error: Errors) : DeleteResult(error = error)

            fun isError(): Boolean = this is Error

            fun isNotError(): Boolean = isError().not()

            sealed class Errors

            data object InvalidId : Errors()
        }
    }

    // Create

    suspend fun insert(entity: ItemEntity): Long

    // Update

    suspend fun update(entity: ItemEntity)

    // Delete

    suspend fun delete(entity: ItemEntity)

    // Read

    /**
     * @param id id of the [ItemEntity]
     * @return [ItemEntity] with [id] id or null if none match
     */
    fun get(id: Long): Flow<ItemEntity?>

    /** @return newest [ItemEntity], null if none found */
    fun newest(): Flow<ItemEntity?>

    /**
     * @param id id of the [ProductEntity] to match by
     * @return newest [ItemEntity] matching [ProductEntity] of [id] id, null if none found
     */
    fun newestByProduct(id: Long): Flow<ItemEntity?>
}
