package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.DeleteResult.Errors
import com.kssidll.arru.domain.data.Data
import kotlinx.coroutines.flow.Flow

interface VariantRepositorySource {
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
            data object InvalidProductId: Errors()
            data object InvalidName: Errors()
            data object DuplicateName: Errors()
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
            data object InvalidName: Errors()
            data object DuplicateName: Errors()
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
            data object DangerousDelete: Errors()
        }
    }

    // Create

    /**
     * Inserts [ProductVariant]
     * @param productId id of the [Product] to insert the [ProductVariant] for
     * @param name name of the variant
     * @return [InsertResult] with id of the newly inserted [ProductVariant] or an error if any
     */
    suspend fun insert(
        productId: Long,
        name: String
    ): InsertResult

    // Update

    /**
     * Updates [ProductVariant] with [variantId] id to provided [name]
     * @param variantId id to match [ProductVariant]
     * @param name name to update the matching [ProductVariant] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        variantId: Long,
        name: String
    ): UpdateResult

    /**
     * Updates [ProductVariant] with [variantId] id to provided [productId] and [name]
     * @param variantId id to match [ProductVariant]
     * @param productId [Product] id to update the matching [ProductVariant] to
     * @param name name to update the matching [ProductVariant] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        variantId: Long,
        productId: Long,
        name: String
    ): UpdateResult

    // Delete

    /**
     * Deletes [ProductVariant]
     * @param variantId id of the [ProductVariant] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        variantId: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param variantId id of the [ProductVariant]
     * @return [ProductVariant] matching [variantId] id or null if none match
     */
    suspend fun get(variantId: Long): ProductVariant?

    /**
     * @param variantId id of the [ProductVariant]
     * @return [ProductVariant] matching [variantId] id or null if none match, as flow
     */
    fun getFlow(variantId: Long): Flow<Data<ProductVariant?>>

    /**
     * @param product [Product] to match the [ProductVariant] with
     * @return list of [ProductVariant] matching [product] as flow
     */
    fun byProductFlow(product: Product): Flow<Data<List<ProductVariant>>>

    /**
     * @return total count of [ProductVariant]
     */
    suspend fun totalCount(): Int

    /**
     * @return list of at most [limit] variants offset by [offset]
     */
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<ProductVariant>
}