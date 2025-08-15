package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProducerRepositorySource {
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
     * Updates [ProductProducerEntity] with [producerId] to provided [name]
     * @param producerId id to match [ProductProducerEntity]
     * @param name name to update the matching [ProductProducerEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        producerId: Long,
        name: String
    ): UpdateResult

    /**
     * Merges [producer] into [mergingInto]
     * @param producer [ProductCategory] to merge
     * @param mergingInto [ProductCategory] to merge the [category] into
     * @return [MergeResult] with the result
     */
    suspend fun merge(
        producer: ProductProducerEntity,
        mergingInto: ProductProducerEntity,
    ): MergeResult

    // Delete

    /**
     * Deletes [ProductProducerEntity]
     * @param producerid id of the [ProductProducerEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        producerid: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param producerId id of the [ProductProducerEntity]
     * @return [ProductProducerEntity] matching [producerId] id or null if none match
     */
    suspend fun get(producerId: Long): ProductProducerEntity?

    /**
     * @param producerId id of the [ProductProducerEntity]
     * @return [ProductProducerEntity] matching [producerId] id or null if none match, as flow
     */
    fun getFlow(producerId: Long): Flow<Data<ProductProducerEntity?>>

    /**
     * @param producer [ProductProducerEntity] to get the total spending from
     * @return float representing total spending for the [producer] as flow
     */
    fun totalSpentFlow(producer: ProductProducerEntity): Flow<Data<Float?>>

    /**
     * @param producer [ProductProducerEntity] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(producer: ProductProducerEntity): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param producer [ProductProducerEntity] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(producer: ProductProducerEntity): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param producer [ProductProducerEntity] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(producer: ProductProducerEntity): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param producer [ProductProducerEntity] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(producer: ProductProducerEntity): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param producer [ProductProducerEntity] to match the items to
     */
    fun fullItemsPagedFlow(producer: ProductProducerEntity): Flow<PagingData<FullItem>>

    /**
     * @return list of all [ProductProducerEntity] as flow
     */
    fun allFlow(): Flow<Data<ImmutableList<ProductProducerEntity>>>

    /**
     * @return total count of [ProductProducerEntity]
     */
    suspend fun totalCount(): Int

    /**
     * @return list of at most [limit] producers offset by [offset]
     */
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<ProductProducerEntity>
}