package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductPriceByShopByTime
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductRepositorySource {
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
            data object InvalidName: Errors()
            data object DuplicateName: Errors()
            data object InvalidCategoryId: Errors()
            data object InvalidProducerId: Errors()
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
            data object InvalidName: Errors()
            data object DuplicateName: Errors()
            data object InvalidCategoryId: Errors()
            data object InvalidProducerId: Errors()
        }

        sealed class MergeResult(
            val error: Errors? = null
        ) {
            data object Success: MergeResult()
            class Error(error: Errors): MergeResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidProduct: Errors()
            data object InvalidMergingInto: Errors()
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
     * Inserts [ProductEntity]
     * @param name name of the [ProductEntity] to insert
     * @param categoryId id of the [ProductCategoryEntity] of the [ProductEntity] to insert
     * @param producerId id of the [ProductProducerEntity] of the [ProductEntity] to insert
     * @return [InsertResult] with id of the newly inserted [ProductEntity] or an error if any
     */
    suspend fun insert(
        name: String,
        categoryId: Long,
        producerId: Long?
    ): InsertResult

    // Update

    /**
     * Updates [ProductEntity] with [productId] id to provided [name], [categoryId] and [producerId]
     * @param productId id to match [ProductEntity]
     * @param name name to update the matching [ProductEntity] to
     * @param categoryId id of the [ProductCategoryEntity] to update the matching [ProductEntity] to
     * @param producerId id of the [ProductProducerEntity] to update the matching [ProductEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        productId: Long,
        name: String,
        categoryId: Long,
        producerId: Long?
    ): UpdateResult

    /**
     * Merges [entity] into [mergingInto]
     * @param entity [ProductEntity] to merge
     * @param mergingInto [ProductEntity] to merge the [entity] into
     * @return [MergeResult] with the result
     */
    suspend fun merge(
        entity: ProductEntity,
        mergingInto: ProductEntity,
    ): MergeResult

    // Delete

    /**
     * Deletes [ProductEntity] matching [productId]
     * @param productId id of the [ProductEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        productId: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param productId id of the [ProductEntity]
     * @return [ProductEntity] matching [productId] id or null if none match
     */
    suspend fun get(productId: Long): ProductEntity?

    /**
     * @param productId id of the [ProductEntity]
     * @return [ProductEntity] matching [productId] id or null if none match, as flow
     */
    fun getFlow(productId: Long): Flow<Data<ProductEntity?>>

    /**
     * @param entity [ProductEntity] to get the total spending from
     * @return float representing total spending for the [entity] as flow
     */
    fun totalSpentFlow(entity: ProductEntity): Flow<Data<Float?>>

    /**
     * @param entity [ProductEntity] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(entity: ProductEntity): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param entity [ProductEntity] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(entity: ProductEntity): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param entity [ProductEntity] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(entity: ProductEntity): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param entity [ProductEntity] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(entity: ProductEntity): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param entity [ProductEntity] to match the items to
     */
    fun fullItemsPagedFlow(entity: ProductEntity): Flow<PagingData<FullItem>>

    /**
     * @param entity [ProductEntity] to match the [ItemEntity] with
     * @return newest [ItemEntity] that matches [entity], null if none match
     */
    suspend fun newestItem(entity: ProductEntity): ItemEntity?

    /**
     * @param entity [ProductEntity] to match the data with
     * @return list of [ProductPriceByShopByTime] representing the average price of [entity] groupped by variant, shop and month as flow
     */
    fun averagePriceByVariantByShopByMonthFlow(entity: ProductEntity): Flow<Data<ImmutableList<ProductPriceByShopByTime>>>

    /**
     * @return list of all [ProductEntity] as flow
     */
    fun allFlow(): Flow<Data<ImmutableList<ProductEntity>>>
}