package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.IntermediateTransaction
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface TransactionRepositorySource {
    companion object {
        sealed class InsertResult(val id: Long? = null, val error: Errors? = null) {
            class Success(id: Long) : InsertResult(id)

            class Error(error: Errors) : InsertResult(error = error)

            fun isError(): Boolean = this is Error

            fun isNotError(): Boolean = isError().not()

            sealed class Errors

            data object InvalidDate : Errors()

            data object InvalidTotalCost : Errors()

            data object InvalidShopId : Errors()
        }

        sealed class ItemInsertResult(val id: Long? = null, val error: Errors? = null) {
            class Success(id: Long) : ItemInsertResult(id)

            class Error(error: Errors) : ItemInsertResult(error = error)

            fun isError(): Boolean = this is Error

            fun isNotError(): Boolean = isError().not()

            sealed class Errors

            data object InvalidTransactionId : Errors()

            data object InvalidItemId : Errors()
        }

        sealed class UpdateResult(val error: Errors? = null) {
            data object Success : UpdateResult()

            class Error(error: Errors) : UpdateResult(error = error)

            fun isError(): Boolean = this is Error

            fun isNotError(): Boolean = isError().not()

            sealed class Errors

            data object InvalidId : Errors()

            data object InvalidDate : Errors()

            data object InvalidTotalCost : Errors()

            data object InvalidShopId : Errors()
        }

        sealed class DeleteResult(val error: Errors? = null) {
            data object Success : DeleteResult()

            class Error(error: Errors) : DeleteResult(error = error)

            fun isError(): Boolean = this is Error

            fun isNotError(): Boolean = isError().not()

            sealed class Errors

            data object InvalidId : Errors()

            data object DangerousDelete : Errors()
        }
    }

    // Create

    /**
     * Inserts [TransactionEntity]
     *
     * @param date date of the [TransactionEntity]
     * @param totalCost total cost of the [TransactionEntity]
     * @param shopId id of the [ShopEntity] in the [TransactionEntity]
     * @param note note of the [ShopEntity] in the [TransactionEntity]
     * @return [InsertResult] with id of the newly inserted [TransactionEntity] or an error if any
     */
    suspend fun insert(date: Long, totalCost: Long, shopId: Long?, note: String?): InsertResult

    // Update

    /**
     * Updates [TransactionEntity] with [id] to provided [date], [totalCost] and [shopId]
     *
     * @param id id to match [TransactionEntity]
     * @param date date to update the matching [TransactionEntity] to
     * @param totalCost total cost to update the matching [TransactionEntity] to
     * @param shopId id of the [ShopEntity] to update the matching [TransactionEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        id: Long,
        date: Long,
        totalCost: Long,
        shopId: Long?,
        note: String?,
    ): UpdateResult

    // Delete

    /**
     * Deletes [TransactionEntity]
     *
     * @param id id of the [TransactionEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(id: Long, force: Boolean): DeleteResult

    // Read

    /**
     * @param id id of the [TransactionEntity]
     * @return [TransactionEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<TransactionEntity?>

    /** @return float representing total spending */
    fun totalSpent(): Flow<Float?>

    /** @return [PagingData] of all [IntermediateTransaction] */
    fun intermediates(): Flow<PagingData<IntermediateTransaction>>

    /**
     * @param id id of the [TransactionEntity]
     * @return [IntermediateTransaction] that is of [TransactionEntity] [id]
     */
    fun intermediateFor(id: Long): Flow<IntermediateTransaction?>

    /**
     * @return List of [TransactionSpentChartData] representing total spending partitioned by day
     */
    fun totalSpentByDay(): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @return List of [TransactionSpentChartData] representing total spending partitioned by week
     */
    fun totalSpentByWeek(): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @return List of [TransactionSpentChartData] representing total spending partitioned by month
     */
    fun totalSpentByMonth(): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @return List of [TransactionSpentChartData] representing total spending partitioned by year
     */
    fun totalSpentByYear(): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @return newest [TransactionEntity] (by time added, not transaction date) or null if none
     *   exist
     */
    fun newest(): Flow<TransactionEntity?>
}
