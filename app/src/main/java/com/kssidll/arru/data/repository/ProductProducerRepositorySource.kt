package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductProducerRepositorySource {
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
        }

        sealed class MergeResult(
            val error: Errors? = null
        ) {
            data object Success: MergeResult()
            class Error(error: Errors): MergeResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidProducer: Errors()
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
     * Inserts [ProductProducerEntity]
     * @param name name of the [ProductProducerEntity]
     * @return [InsertResult] with id of the newly inserted [ProductProducerEntity] or an error if any
     */
    suspend fun insert(name: String): InsertResult

    // Update

    /**
     * Updates [ProductProducerEntity] with [id] to provided [name]
     * @param id id to match [ProductProducerEntity]
     * @param name name to update the matching [ProductProducerEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        id: Long,
        name: String
    ): UpdateResult

    /**
     * Merges [entity] into [mergingInto]
     * @param entity [ProductCategoryEntity] to merge
     * @param mergingInto [ProductCategoryEntity] to merge the [category] into
     * @return [MergeResult] with the result
     */
    suspend fun merge(
        entity: ProductProducerEntity,
        mergingInto: ProductProducerEntity,
    ): MergeResult

    // Delete

    /**
     * Deletes [ProductProducerEntity]
     * @param id id of the [ProductProducerEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        id: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param id id of the [ProductProducerEntity]
     * @return [ProductProducerEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<ProductProducerEntity?>















    /**
     * @return list of all [ProductProducerEntity]
     */
    fun all(): Flow<ImmutableList<ProductProducerEntity>>

    /**
     * @param producer [ProductProducerEntity] to get the total spending from
     * @return float representing total spending for the [producer]
     */
    fun totalSpent(producer: ProductProducerEntity): Flow<Float?>

    /**
     * @param producer [ProductProducerEntity] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day
     */
    fun totalSpentByDay(producer: ProductProducerEntity): Flow<ImmutableList<ItemSpentByTime>>

    /**
     * @param producer [ProductProducerEntity] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week
     */
    fun totalSpentByWeek(producer: ProductProducerEntity): Flow<ImmutableList<ItemSpentByTime>>

    /**
     * @param producer [ProductProducerEntity] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month
     */
    fun totalSpentByMonth(producer: ProductProducerEntity): Flow<ImmutableList<ItemSpentByTime>>

    /**
     * @param producer [ProductProducerEntity] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year
     */
    fun totalSpentByYear(producer: ProductProducerEntity): Flow<ImmutableList<ItemSpentByTime>>

    /**
     * @param producer [ProductProducerEntity] to match the items to
     */
    fun fullItemsPaged(producer: ProductProducerEntity): Flow<PagingData<FullItem>>
}