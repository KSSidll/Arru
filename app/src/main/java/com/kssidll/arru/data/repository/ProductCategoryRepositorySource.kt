package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductCategoryEntity
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

interface ProductCategoryRepositorySource {
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
     * Inserts [ProductCategoryEntity]
     * @param name name of the [ProductCategoryEntity] to insert
     * @return [InsertResult] with id of the newly inserted [ProductCategoryEntity] or an error if any
     */
    suspend fun insert(name: String): InsertResult

    // Update

    /**
     * Updates [ProductCategoryEntity] with [categoryId] id to provided [name]
     * @param categoryId id to match [ProductCategoryEntity]
     * @param name name to update the matching [ProductCategoryEntity] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        categoryId: Long,
        name: String
    ): UpdateResult

    /**
     * Merges [category] into [mergingInto]
     * @param category [ProductCategoryEntity] to merge
     * @param mergingInto [ProductCategoryEntity] to merge the [category] into
     * @return [MergeResult] with the result
     */
    suspend fun merge(
        category: ProductCategoryEntity,
        mergingInto: ProductCategoryEntity,
    ): MergeResult

    // Delete

    /**
     * Deletes [ProductCategoryEntity] matching [productCategoryId]
     * @param productCategoryId id of the [ProductCategoryEntity] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        productCategoryId: Long,
        force: Boolean
    ): DeleteResult

    // Read

    /**
     * @param categoryId id of the [ProductCategoryEntity]
     * @return [ProductCategoryEntity] matching [categoryId] id or null if none match
     */
    fun get(categoryId: Long): Flow<ProductCategoryEntity?>

    /**
     * @return list of all [ProductCategoryEntity]
     */
    fun all(): Flow<ImmutableList<ProductCategoryEntity>>

    /**
     * @param category [ProductCategoryEntity] to get the total spending from
     * @return float representing total spending for the [category]
     */
    fun totalSpent(category: ProductCategoryEntity): Flow<Float?>

    /**
     * @param category [ProductCategoryEntity] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day
     */
    fun totalSpentByDay(category: ProductCategoryEntity): Flow<ImmutableList<ItemSpentByTime>>

    /**
     * @param category [ProductCategoryEntity] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week
     */
    fun totalSpentByWeek(category: ProductCategoryEntity): Flow<ImmutableList<ItemSpentByTime>>

    /**
     * @param category [ProductCategoryEntity] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month
     */
    fun totalSpentByMonth(category: ProductCategoryEntity): Flow<ImmutableList<ItemSpentByTime>>

    /**
     * @param category [ProductCategoryEntity] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year
     */
    fun totalSpentByYear(category: ProductCategoryEntity): Flow<ImmutableList<ItemSpentByTime>>

    /**
     * @param category [ProductCategoryEntity] to match the items to
     */
    fun fullItemsPaged(category: ProductCategoryEntity): Flow<PagingData<FullItem>>

    /**
     * @return list of [ItemSpentByCategory] representing total spending groupped by category
     */
    fun totalSpentByCategory(): Flow<ImmutableList<ItemSpentByCategory>>

    /**
     * @param year year to match the data to
     * @param month month to match the data to
     * @return list of [ItemSpentByCategory] representing total spending groupped by category in [year] and [month]
     */
    fun totalSpentByCategoryByMonth(
        year: Int,
        month: Int
    ): Flow<ImmutableList<ItemSpentByCategory>>
}