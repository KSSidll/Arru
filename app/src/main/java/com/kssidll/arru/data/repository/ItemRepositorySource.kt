package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface ItemRepositorySource {
    companion object {
        sealed class InsertResult(
            val id: Long? = null,
            val error: Errors? = null
        ) {
            class Success(id: Long): InsertResult(id)
            class Error(error: Errors): InsertResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidTransactionId: Errors()
            data object InvalidProductId: Errors()
            data object InvalidVariantId: Errors()
            data object InvalidQuantity: Errors()
            data object InvalidPrice: Errors()
        }

        sealed class UpdateResult(
            val error: Errors? = null
        ) {
            data object Success: UpdateResult()
            class Error(error: Errors): UpdateResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidId: Errors()
            data object InvalidProductId: Errors()
            data object InvalidVariantId: Errors()
            data object InvalidQuantity: Errors()
            data object InvalidPrice: Errors()
        }

        sealed class DeleteResult(
            val error: Errors? = null
        ) {
            data object Success: DeleteResult()
            class Error(error: Errors): DeleteResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidId: Errors()
        }
    }

    // Create

    /**
     * Inserts [ItemEntity]
     * @param transactionId id of the [TransactionEntity] to add the [ItemEntity] to
     * @param productId id of the [ProductEntity] in the [ItemEntity]
     * @param variantId id of the [ProductVariantEntity] in the [ItemEntity]
     * @param quantity quantity of the [ItemEntity]
     * @param price price of the [ItemEntity]
     * @return [InsertResult] with id of the newly inserted [ItemEntity] or an error if any
     */
    suspend fun insert(
        transactionId: Long,
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long
    ): InsertResult

    // Update

    /**
     * Updates [ItemEntity] with [id] to provided [productId], [variantId], [quantity] and [price]
     * @param id id to match [ItemEntity]
     * @param productId [ProductEntity] id to update the matching [ItemEntity] to
     * @param variantId [ProductVariantEntity] id to update the matching [ItemEntity] to
     * @param quantity quantity to update the matching [ItemEntity] to
     * @param price price to update the matching [ItemEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        id: Long,
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long
    ): UpdateResult

    // Delete

    /**
     * Deletes [ItemEntity]
     * @param id id of the [ItemEntity] to delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(id: Long): DeleteResult

    // Read

    /**
     * @param id id of the [ItemEntity]
     * @return [ItemEntity] with [id] id or null if none match
     */
    fun get(id: Long): Flow<ItemEntity?>









    /**
     * @return newest [ItemEntity], null if none found
     */
    fun newest(): Flow<ItemEntity?>
}