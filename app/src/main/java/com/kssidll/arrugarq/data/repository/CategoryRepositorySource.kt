package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

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

        sealed class AltInsertResult(
            val id: Long? = null,
            val error: Errors? = null
        ) {
            class Success(id: Long): AltInsertResult(id)
            class Error(error: Errors): AltInsertResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidId: Errors()
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

        sealed class AltUpdateResult(
            val error: Errors? = null
        ) {
            data object Success: AltUpdateResult()
            class Error(error: Errors): AltUpdateResult(error = error)

            fun isError(): Boolean = this is Error
            fun isNotError(): Boolean = isError().not()

            sealed class Errors
            data object InvalidId: Errors()
            data object InvalidCategoryId: Errors()
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

    /**
     * Inserts [ProductCategoryAltName]
     * @param category [ProductCategory] to add the [alternativeName] to
     * @param alternativeName alternative name to add to the [category]
     * @return [AltInsertResult] with id of the newly inserted [ProductCategoryAltName] or an error if any
     */
    suspend fun insertAltName(
        category: ProductCategory,
        alternativeName: String
    ): AltInsertResult

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
     * Updates [ProductCategoryAltName] with [alternativeNameId] id to provided [categoryId] and [name]
     * @param alternativeNameId id to match [ProductCategoryAltName]
     * @param categoryId categoryId to update the matching [ProductCategoryAltName] to
     * @param name name to update the matching [ProductCategoryAltName] to
     * @return [UpdateResult] with the result
     */
    suspend fun updateAltName(
        alternativeNameId: Long,
        categoryId: Long,
        name: String
    ): AltUpdateResult

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

    /**
     * Deletes [ProductCategoryAltName] matching [alternativeNameId]
     * @param alternativeNameId id of the [ProductCategoryAltName] to delete
     * @return [DeleteResult] with the result
     */
    suspend fun deleteAltName(alternativeNameId: Long): DeleteResult

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
    fun getFlow(categoryId: Long): Flow<ProductCategory?>

    /**
     * @param category [ProductCategory] to get the total spending from
     * @return long representing total spending for the [category] as flow
     */
    fun totalSpentFlow(category: ProductCategory): Flow<Long>

    /**
     * @param category [ProductCategory] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(category: ProductCategory): Flow<List<ItemSpentByTime>>

    /**
     * @param category [ProductCategory] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(category: ProductCategory): Flow<List<ItemSpentByTime>>

    /**
     * @param category [ProductCategory] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(category: ProductCategory): Flow<List<ItemSpentByTime>>

    /**
     * @param category [ProductCategory] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(category: ProductCategory): Flow<List<ItemSpentByTime>>

    /**
     * @param category [ProductCategory] to match the items to
     */
    fun fullItemsPagedFlow(category: ProductCategory): Flow<PagingData<FullItem>>

    /**
     * @return list of [ItemSpentByCategory] representing total spending groupped by category
     */
    fun totalSpentByCategoryFlow(): Flow<List<ItemSpentByCategory>>

    /**
     * @param year year to match the data to
     * @param month month to match the data to
     * @return list of [ItemSpentByCategory] representing total spending groupped by category in [year] and [month]
     */
    fun totalSpentByCategoryByMonthFlow(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByCategory>>

    /**
     * @return list of all [ProductCategory] as flow
     */
    fun allFlow(): Flow<List<ProductCategory>>

    /**
     * @return list of all [ProductCategoryWithAltNames] as flow
     */
    fun allWithAltNamesFlow(): Flow<List<ProductCategoryWithAltNames>>
}