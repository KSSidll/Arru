package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

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
     * Inserts [TransactionBasket]
     * @param date date of the [TransactionBasket]
     * @param totalCost total cost of the [TransactionBasket]
     * @param shopId id of the [Shop] in the [TransactionBasket]
     * @return [InsertResult] with id of the newly inserted [TransactionBasket] or an error if any
     */
    suspend fun insert(
        date: Long,
        totalCost: Long,
        shopId: Long?,
    ): InsertResult

    /**
     * Inserts [TransactionBasketItem]
     * @param transactionBasketId id of the [TransactionBasket] in the [TransactionBasketItem]
     * @param itemId id of the [Item] in the [TransactionBasketItem]
     * @return [ItemInsertResult] with id of the newly inserted [TransactionBasketItem] or an error if any
     */
    suspend fun insertTransactionItem(
        transactionBasketId: Long,
        itemId: Long,
    ): ItemInsertResult

    // Update

    /**
     * Updates [TransactionBasket] with [transactionId] to provided [date], [totalCost] and [shopId]
     * @param transactionId id to match [TransactionBasket]
     * @param date date to update the matching [TransactionBasket] to
     * @param totalCost total cost to update the matching [TransactionBasket] to
     * @param shopId id of the [Shop] to update the matching [TransactionBasket] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        transactionId: Long,
        date: Long,
        totalCost: Long,
        shopId: Long?,
    ): UpdateResult

    // Delete

    /**
     * Deletes [TransactionBasket]
     * @param transactionId id of the [TransactionBasket] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        transactionId: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param transactionBasketId id of the [TransactionBasket]
     * @return [TransactionBasket] matching [transactionBasketId] id or null if none match
     */
    suspend fun get(transactionBasketId: Long): TransactionBasket?

    /**
     * @return long representing total spending for the [category] as flow
     */
    fun totalSpentFlow(): Flow<Long>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(): Flow<List<TransactionSpentByTime>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(): Flow<List<TransactionSpentByTime>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(): Flow<List<TransactionSpentByTime>>

    /**
     * @return list of [TransactionSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(): Flow<List<TransactionSpentByTime>>

    /**
     * @param startPosition position, from 0 up, to get next [count] items from
     * @param count how many items to query
     * @return list of [count] [TransactionBasketWithItems] where the first item is the item at [startPosition]
     */
    suspend fun transactionBasketsWithItems(
        startPosition: Int,
        count: Int
    ): List<TransactionBasketWithItems>


    /**
     * @return [TransactionBasketWithItems] as [PagingData] as [Flow]
     */
    fun transactionBasketsPagedFlow(): Flow<PagingData<TransactionBasketWithItems>>

    /**
     * @param transactionId id to match [TransactionBasketWithItems] with
     * @return [TransactionBasketWithItems] matching [transactionId] as flow
     */
    fun transactionBasketWithItemsFlow(transactionId: Long): Flow<TransactionBasketWithItems>
}