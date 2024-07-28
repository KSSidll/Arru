package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.domain.model.TransactionPreview
import com.kssidll.arru.domain.model.TransactionSpentByTime
import kotlinx.coroutines.flow.Flow

interface TransactionRepositorySource {
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
            data object InvalidDate: Errors()
            data object InvalidTotalCost: Errors()
        }

        sealed class ItemInsertResult(
            val id: Long? = null,
            val error: Errors? = null
        ) {
            class Success(id: Long): ItemInsertResult(id)
            class Error(error: Errors): ItemInsertResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidTransactionId: Errors()
            data object InvalidItemId: Errors()
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
            data object InvalidDate: Errors()
            data object InvalidTotalCost: Errors()
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
     * Inserts [TransactionEntity]
     * @param date date of the [TransactionEntity]
     * @param totalCost total cost of the [TransactionEntity]
     * @return [InsertResult] with id of the newly inserted [TransactionEntity] or an error if any
     */
    suspend fun insert(
        date: Long,
        totalCost: Long,
    ): InsertResult

    // Update

    /**
     * Updates [TransactionEntity] with [transactionId] to provided [date] and [totalCost]
     * @param transactionId id to match [TransactionEntity]
     * @param date date to update the matching [TransactionEntity] to
     * @param totalCost total cost to update the matching [TransactionEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        transactionId: Long,
        date: Long,
        totalCost: Long,
    ): UpdateResult

    // Delete

    /**
     * Deletes [TransactionEntity]
     * @param transactionId id of the [TransactionEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        transactionId: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @return sum of all totalCost of [TransactionEntity] objects if any
     */
    suspend fun totalRawSpent(): Long

    /**
     * @return count of [TransactionEntity] objects
     */
    suspend fun count(): Int

    /**
     * @return sum of all totalCost of [TransactionEntity] objects as [Flow] of [Data]
     */
    fun totalSpentFlow(): Flow<Data<Float?>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(): Flow<Data<List<TransactionSpentByTime>>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(): Flow<Data<List<TransactionSpentByTime>>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(): Flow<Data<List<TransactionSpentByTime>>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(): Flow<Data<List<TransactionSpentByTime>>>

    /**
     * @return all [TransactionEntity] objects mapped to [TransactionPreview] as [Flow] of [PagingData]
     */
    fun allPagedAsPreview(): Flow<PagingData<TransactionPreview>>
}