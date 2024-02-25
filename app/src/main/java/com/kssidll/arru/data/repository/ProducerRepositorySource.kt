package com.kssidll.arru.data.repository

import androidx.paging.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import kotlinx.coroutines.flow.*

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
     * Inserts [ProductProducer]
     * @param name name of the [ProductProducer]
     * @return [InsertResult] with id of the newly inserted [ProductProducer] or an error if any
     */
    suspend fun insert(name: String): InsertResult

    // Update

    /**
     * Updates [ProductProducer] with [producerId] to provided [name]
     * @param producerId id to match [ProductProducer]
     * @param name name to update the matching [ProductProducer] to
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
        producer: ProductProducer,
        mergingInto: ProductProducer,
    ): MergeResult

    // Delete

    /**
     * Deletes [ProductProducer]
     * @param producerid id of the [ProductProducer] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        producerid: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param producerId id of the [ProductProducer]
     * @return [ProductProducer] matching [producerId] id or null if none match
     */
    suspend fun get(producerId: Long): ProductProducer?

    /**
     * @param producerId id of the [ProductProducer]
     * @return [ProductProducer] matching [producerId] id or null if none match, as flow
     */
    fun getFlow(producerId: Long): Flow<Data<ProductProducer?>>

    /**
     * @param producer [ProductProducer] to get the total spending from
     * @return float representing total spending for the [producer] as flow
     */
    fun totalSpentFlow(producer: ProductProducer): Flow<Data<Float?>>

    /**
     * @param producer [ProductProducer] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(producer: ProductProducer): Flow<Data<List<ItemSpentByTime>>>

    /**
     * @param producer [ProductProducer] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(producer: ProductProducer): Flow<Data<List<ItemSpentByTime>>>

    /**
     * @param producer [ProductProducer] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(producer: ProductProducer): Flow<Data<List<ItemSpentByTime>>>

    /**
     * @param producer [ProductProducer] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(producer: ProductProducer): Flow<Data<List<ItemSpentByTime>>>

    /**
     * @param producer [ProductProducer] to match the items to
     */
    fun fullItemsPagedFlow(producer: ProductProducer): Flow<PagingData<FullItem>>

    /**
     * @return list of all [ProductProducer] as flow
     */
    fun allFlow(): Flow<Data<List<ProductProducer>>>
}