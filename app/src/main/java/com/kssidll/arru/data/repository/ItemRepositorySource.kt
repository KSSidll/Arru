package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.TransactionBasket
import com.kssidll.arru.domain.data.Data
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
     * Inserts [Item]
     * @param transactionId id of the [TransactionBasket] to add the [Item] to
     * @param productId id of the [Product] in the [Item]
     * @param variantId id of the [ProductVariant] in the [Item]
     * @param quantity quantity of the [Item]
     * @param price price of the [Item]
     * @return [InsertResult] with id of the newly inserted [Item] or an error if any
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
     * Updates [Item] with [itemId] to provided [productId], [variantId], [quantity] and [price]
     * @param itemId id to match [Item]
     * @param productId [Product] id to update the matching [Item] to
     * @param variantId [ProductVariant] id to update the matching [Item] to
     * @param quantity quantity to update the matching [Item] to
     * @param price price to update the matching [Item] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        itemId: Long,
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long
    ): UpdateResult

    // Delete

    /**
     * Deletes [Item]
     * @param itemId id of the [Item] to delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(itemId: Long): DeleteResult

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
    fun newestFlow(): Flow<Data<Item?>>

    /**
     * @return total count of [Item]
     */
    suspend fun totalCount(): Int

    /**
     * @return list of at most [limit] items offset by [offset]
     */
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<Item>
}