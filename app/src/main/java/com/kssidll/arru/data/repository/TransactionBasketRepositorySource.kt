package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.data.TransactionSpentByTime
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface TransactionBasketRepositorySource {
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
            data object InvalidShopId: Errors()
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
            data object InvalidShopId: Errors()
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
     * @param shopId id of the [ShopEntity] in the [TransactionEntity]
     * @param note note of the [ShopEntity] in the [TransactionEntity]
     * @return [InsertResult] with id of the newly inserted [TransactionEntity] or an error if any
     */
    suspend fun insert(
        date: Long,
        totalCost: Long,
        shopId: Long?,
        note: String?
    ): InsertResult

    // Update

    /**
     * Updates [TransactionEntity] with [transactionId] to provided [date], [totalCost] and [shopId]
     * @param transactionId id to match [TransactionEntity]
     * @param date date to update the matching [TransactionEntity] to
     * @param totalCost total cost to update the matching [TransactionEntity] to
     * @param shopId id of the [ShopEntity] to update the matching [TransactionEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        transactionId: Long,
        date: Long,
        totalCost: Long,
        shopId: Long?,
        note: String?
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
     * @param transactionBasketId id of the [TransactionEntity]
     * @return [TransactionEntity] matching [transactionBasketId] id or null if none match
     */
    suspend fun get(transactionBasketId: Long): TransactionEntity?

    /**
     * @return newest [TransactionEntity] (by time added, not transaction date) or null if none exist
     */
    suspend fun newest(): TransactionEntity?

    /**
     * @return value representing the count of [TransactionEntity]
     */
    suspend fun count(): Int

    /**
     * @param [transactionBasketId] id of the [TransactionEntity] that acts as the breakpoint before which the transactions are counted
     * @return value representing the count of [TransactionEntity] added before (chronologically) [transactionBasketId], counts by id, so doesn't check if the transaction actually exists
     */
    suspend fun countBefore(transactionBasketId: Long): Int

    /**
     * @param [transactionBasketId] id of the [TransactionEntity] that acts as the breakpoint after which the transactions are counted
     * @return value representing the count of [TransactionEntity] added after (chronologically) [transactionBasketId], counts by id, so doesn't check if the transaction actually exists
     */
    suspend fun countAfter(transactionBasketId: Long): Int

    /**
     * @return long representing total spending for the [category]
     */
    suspend fun totalSpentLong(): Data<Long?>

    /**
     * @return float representing total spending for the [category] as flow
     */
    fun totalSpentFlow(): Flow<Float?>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(): Flow<ImmutableList<TransactionSpentByTime>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(): Flow<ImmutableList<TransactionSpentByTime>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(): Flow<ImmutableList<TransactionSpentByTime>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(): Flow<ImmutableList<TransactionSpentByTime>>

    /**
     * @param startPosition position, from 0 up, to get next [count] items from
     * @param count how many items to query
     * @return list of [count] [TransactionBasketWithItems] where the first item is the item at [startPosition]
     */
    suspend fun transactionBasketsWithItems(
        startPosition: Int,
        count: Int
    ): ImmutableList<TransactionBasketWithItems>


    /**
     * @return [TransactionBasketWithItems] as [PagingData] as [Flow], includes null data for placeholder values
     */
    fun transactionBasketsPagedFlow(): Flow<PagingData<TransactionBasketWithItems>>

    /**
     * @param transactionId id to match [TransactionBasketWithItems] with
     * @return [TransactionBasketWithItems] matching [transactionId] as flow
     */
    fun transactionBasketWithItemsFlow(transactionId: Long): Flow<Data<TransactionBasketWithItems?>>

    /**
     * @return total count of [TransactionEntity]
     */
    suspend fun totalCount(): Int

    /**
     * @return list of at most [limit] baskets offset by [offset]
     */
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<TransactionEntity>
}