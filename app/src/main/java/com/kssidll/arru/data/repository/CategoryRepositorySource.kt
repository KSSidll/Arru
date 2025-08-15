package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface CategoryRepositorySource {
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
            data object InvalidCategory: Errors()
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
     * Inserts [ProductCategory]
     * @param name name of the [ProductCategory] to insert
     * @return [InsertResult] with id of the newly inserted [ProductCategory] or an error if any
     */
    suspend fun insert(name: String): InsertResult

    // Update

    /**
     * Updates [ProductCategory] with [categoryId] id to provided [name]
     * @param categoryId id to match [ProductCategory]
     * @param name name to update the matching [ProductCategory] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        categoryId: Long,
        name: String
    ): UpdateResult

    /**
     * Merges [category] into [mergingInto]
     * @param category [ProductCategory] to merge
     * @param mergingInto [ProductCategory] to merge the [category] into
     * @return [MergeResult] with the result
     */
    suspend fun merge(
        category: ProductCategory,
        mergingInto: ProductCategory,
    ): MergeResult

    // Delete

    /**
     * Deletes [ProductCategory] matching [productCategoryId]
     * @param productCategoryId id of the [ProductCategory] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        productCategoryId: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param categoryId id of the [ProductCategory]
     * @return [ProductCategory] matching [categoryId] id or null if none match
     */
    suspend fun get(categoryId: Long): ProductCategory?

    /**
     * @param categoryId id of the [ProductCategory]
     * @return [ProductCategory] matching [categoryId] id or null if none match, as flow
     */
    fun getFlow(categoryId: Long): Flow<Data<ProductCategory?>>

    /**
     * @param category [ProductCategory] to get the total spending from
     * @return float representing total spending for the [category] as flow
     */
    fun totalSpentFlow(category: ProductCategory): Flow<Data<Float?>>

    /**
     * @param category [ProductCategory] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(category: ProductCategory): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param category [ProductCategory] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(category: ProductCategory): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param category [ProductCategory] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(category: ProductCategory): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param category [ProductCategory] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(category: ProductCategory): Flow<Data<ImmutableList<ItemSpentByTime>>>

    /**
     * @param category [ProductCategory] to match the items to
     */
    fun fullItemsPagedFlow(category: ProductCategory): Flow<PagingData<FullItem>>

    /**
     * @return list of [ItemSpentByCategory] representing total spending groupped by category
     */
    fun totalSpentByCategoryFlow(): Flow<ImmutableList<ItemSpentByCategory>>

    /**
     * @param year year to match the data to
     * @param month month to match the data to
     * @return list of [ItemSpentByCategory] representing total spending groupped by category in [year] and [month]
     */
    fun totalSpentByCategoryByMonthFlow(
        year: Int,
        month: Int
    ): Flow<ImmutableList<ItemSpentByCategory>>

    /**
     * @return list of all [ProductCategory] as flow
     */
    fun allFlow(): Flow<Data<ImmutableList<ProductCategory>>>

    /**
     * @return total count of [ProductCategory]
     */
    suspend fun totalCount(): Int

    /**
     * @return list of at most [limit] categories offset by [offset]
     */
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<ProductCategory>
}