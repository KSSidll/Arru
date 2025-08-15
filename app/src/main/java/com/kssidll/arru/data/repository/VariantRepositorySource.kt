package com.kssidll.arru.data.repository

import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import kotlinx.collections.immutable.ImmutableList
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
     * Inserts [ProductVariantEntity]
     * @param productId id of the [ProductEntity] to insert the [ProductVariantEntity] for, null for global [ProductVariantEntity]
     * @param name name of the variant
     * @return [InsertResult] with id of the newly inserted [ProductVariantEntity] or an error if any
     */
    suspend fun insert(
        productId: Long?,
        name: String
    ): InsertResult

    // Update

    /**
     * Updates [ProductVariantEntity] with [variantId] id to provided [name]
     * @param variantId id to match [ProductVariantEntity]
     * @param name name to update the matching [ProductVariantEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        variantId: Long,
        name: String
    ): UpdateResult

    /**
     * Updates [ProductVariantEntity] with [variantId] id to provided [productId] and [name]
     * @param variantId id to match [ProductVariantEntity]
     * @param productId [ProductEntity] id to update the matching [ProductVariantEntity] to, null for global [ProductVariantEntity]
     * @param name name to update the matching [ProductVariantEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        variantId: Long,
        productId: Long?,
        name: String
    ): UpdateResult

    // Delete

    /**
     * Deletes [ProductVariantEntity]
     * @param variantId id of the [ProductVariantEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        variantId: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param variantId id of the [ProductVariantEntity]
     * @return [ProductVariantEntity] matching [variantId] id or null if none match
     */
    fun get(variantId: Long): Flow<ProductVariantEntity?>

    /**
     * @param productEntity [ProductEntity] to match the [ProductVariantEntity] with
     * @param showGlobal whether to return global variants as well
     * @return list of [ProductVariantEntity] matching [productEntity]
     */
    fun byProduct(productEntity: ProductEntity, showGlobal: Boolean): Flow<ImmutableList<ProductVariantEntity>>
}