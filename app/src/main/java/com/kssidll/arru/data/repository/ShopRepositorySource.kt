package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.data.TransactionTotalSpentByTime
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ShopRepositorySource {
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
            data object InvalidShop: Errors()
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
     * Inserts [ShopEntity]
     * @param name name of the [ShopEntity] to insert
     * @return [InsertResult] with id of the newly inserted [ShopEntity] or an error if any
     */
    suspend fun insert(name: String): InsertResult

    // Update

    /**
     * Updates [ShopEntity] with [shopId] to provided [name]
     * @param shopId id to match [ShopEntity]
     * @param name name to update the matching [ShopEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        shopId: Long,
        name: String
    ): UpdateResult

    /**
     * Merges [shop] into [mergingInto]
     * @param shop [ShopEntity] to merge
     * @param mergingInto [ShopEntity] to merge the [shop] into
     * @return [MergeResult] with the result
     */
    suspend fun merge(
        shop: ShopEntity,
        mergingInto: ShopEntity
    ): MergeResult

    // Delete

    /**
     * Deletes [ShopEntity] matching [shopId]
     * @param shopId id fo the [ShopEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        shopId: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param shopId id of the [ShopEntity]
     * @return [ShopEntity] matching [shopId] id or null if none match
     */
    suspend fun get(shopId: Long): ShopEntity?

    /**
     * @param shopId id of the [ShopEntity]
     * @return [ShopEntity] matching [shopId] id or null if none match, as flow
     */
    fun getFlow(shopId: Long): Flow<Data<ShopEntity?>>

    /**
     * @param entity [ShopEntity] to get the total spending from
     * @return float representing total spending for the [entity] as flow
     */
    fun totalSpentFlow(entity: ShopEntity): Flow<Data<Float?>>

    /**
     * @param entity [ShopEntity] to get the total spending by day from
     * @return list of [TransactionTotalSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(entity: ShopEntity): Flow<Data<ImmutableList<TransactionTotalSpentByTime>>>

    /**
     * @param entity [ShopEntity] to get the total spending by week from
     * @return list of [TransactionTotalSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(entity: ShopEntity): Flow<Data<ImmutableList<TransactionTotalSpentByTime>>>

    /**
     * @param entity [ShopEntity] to get the total spending by month from
     * @return list of [TransactionTotalSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(entity: ShopEntity): Flow<Data<ImmutableList<TransactionTotalSpentByTime>>>

    /**
     * @param entity [ShopEntity] to get the total spending by year from
     * @return list of [TransactionTotalSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(entity: ShopEntity): Flow<Data<ImmutableList<TransactionTotalSpentByTime>>>

    /**
     * @param shopEntity [ShopEntity] to match the items to
     */
    fun fullItemsPagedFlow(entity: ShopEntity): Flow<PagingData<FullItem>>

    /**
     * @return list of [TransactionTotalSpentByShop] representing total spending groupped by shop
     */
    fun totalSpentByShopFlow(): Flow<ImmutableList<TransactionTotalSpentByShop>>

    /**
     * @param year year to match the data to
     * @param month month to match the data to
     * @return list of [TransactionTotalSpentByShop] representing total spending groupped by shop in [year] and [month]
     */
    fun totalSpentByShopByMonthFlow(
        year: Int,
        month: Int
    ): Flow<ImmutableList<TransactionTotalSpentByShop>>

    /**
     * @return list of all [ShopEntity] as flow
     */
    fun allFlow(): Flow<Data<ImmutableList<ShopEntity>>>

    /**
     * @return total count of [ShopEntity]
     */
    suspend fun totalCount(): Int

    /**
     * @return list of at most [limit] shops offset by [offset]
     */
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<ShopEntity>
}