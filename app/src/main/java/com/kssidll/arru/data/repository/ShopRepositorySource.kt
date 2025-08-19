package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ShopRepositorySource {
    companion object {
        sealed class InsertResult(val id: Long? = null, val error: Errors? = null) {
            class Success(id: Long) : InsertResult(id)

            class Error(error: Errors) : InsertResult(error = error)

            fun isError(): Boolean = this is Error

            fun isNotError(): Boolean = isError().not()

            sealed class Errors

            data object InvalidName : Errors()

            data object DuplicateName : Errors()
        }

        sealed class UpdateResult(val error: Errors? = null) {
            data object Success : UpdateResult()

            class Error(error: Errors) : UpdateResult(error = error)

            fun isError(): Boolean = this is Error

            fun isNotError(): Boolean = isError().not()

            sealed class Errors

            data object InvalidId : Errors()

            data object InvalidName : Errors()

            data object DuplicateName : Errors()
        }

        sealed class MergeResult(val error: Errors? = null) {
            data object Success : MergeResult()

            class Error(error: Errors) : MergeResult(error = error)

            fun isError(): Boolean = this is Error

            fun isNotError(): Boolean = isError().not()

            sealed class Errors

            data object InvalidShop : Errors()

            data object InvalidMergingInto : Errors()
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
     * Inserts [ShopEntity]
     *
     * @param name name of the [ShopEntity] to insert
     * @return [InsertResult] with id of the newly inserted [ShopEntity] or an error if any
     */
    suspend fun insert(name: String): InsertResult

    // Update

    /**
     * Updates [ShopEntity] with [id] to provided [name]
     *
     * @param id id to match [ShopEntity]
     * @param name name to update the matching [ShopEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(id: Long, name: String): UpdateResult

    /**
     * Merges [entity] into [mergingInto]
     *
     * @param entity [ShopEntity] to merge
     * @param mergingInto [ShopEntity] to merge the [entity] into
     * @return [MergeResult] with the result
     */
    suspend fun merge(entity: ShopEntity, mergingInto: ShopEntity): MergeResult

    // Delete

    /**
     * Deletes [ShopEntity] matching [id]
     *
     * @param id id fo the [ShopEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(id: Long, force: Boolean): DeleteResult

    // Read

    /**
     * @param id id of the [ShopEntity]
     * @return [ShopEntity] matching [id] id or null if none match
     */
    fun get(id: Long): Flow<ShopEntity?>

    /**
     * @param id id of the [ShopEntity]
     * @return float representing total spending for [ShopEntity] matching [id] id or null if none
     *   match
     */
    fun totalSpent(id: Long): Flow<Float?>

    /**
     * @param id id of the [ShopEntity]
     * @return [PagingData] of [Item] that is of [ShopEntity] [id]
     */
    fun itemsFor(id: Long): Flow<PagingData<Item>>

    /**
     * @param id id of the [ShopEntity]
     * @return List of [TransactionSpentChartData] representing total spending partitioned by day
     */
    fun totalSpentByDay(id: Long): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @param id id of the [ShopEntity]
     * @return List of [TransactionSpentChartData] representing total spending partitioned by week
     */
    fun totalSpentByWeek(id: Long): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @param id id of the [ShopEntity]
     * @return List of [TransactionSpentChartData] representing total spending partitioned by month
     */
    fun totalSpentByMonth(id: Long): Flow<ImmutableList<TransactionSpentChartData>>

    /**
     * @param id id of the [ShopEntity]
     * @return List of [TransactionSpentChartData] representing total spending partitioned by year
     */
    fun totalSpentByYear(id: Long): Flow<ImmutableList<TransactionSpentChartData>>

    /** @return list of all [ShopEntity] */
    fun all(): Flow<ImmutableList<ShopEntity>>

    /** @param entity [ShopEntity] to match the items to */
    fun fullItemsPaged(entity: ShopEntity): Flow<PagingData<FullItem>>

    /**
     * @return list of [TransactionTotalSpentByShop] representing total spending groupped by shop
     */
    fun totalSpentByShop(): Flow<ImmutableList<TransactionTotalSpentByShop>>

    /**
     * @param year year to match the data to
     * @param month month to match the data to
     * @return list of [TransactionTotalSpentByShop] representing total spending groupped by shop in
     *   [year] and [month]
     */
    fun totalSpentByShopByMonth(
        year: Int,
        month: Int,
    ): Flow<ImmutableList<TransactionTotalSpentByShop>>
}
