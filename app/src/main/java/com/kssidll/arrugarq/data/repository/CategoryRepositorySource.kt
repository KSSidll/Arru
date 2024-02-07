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
     * Updates matching [ProductCategory] to provided [productCategory]
     *
     * Matches by id
     * @param productCategory [ProductCategory] to update
     */
    suspend fun update(productCategory: ProductCategory)

    /**
     * Updates all matching [ProductCategory] to provided [productCategories]
     *
     * Matches by id
     * @param productCategories list of [ProductCategory] to update
     */
    suspend fun update(productCategories: List<ProductCategory>)

    /**
     * Updates matching [ProductCategoryAltName] to provided [alternativeName]
     *
     * Matches by id
     * @param alternativeName [ProductCategoryAltName] to update
     */
    suspend fun updateAltName(alternativeName: ProductCategoryAltName)

    // Delete

    /**
     * Deletes [ProductCategory]
     * @param productCategory [ProductCategory] to delete
     */
    suspend fun delete(productCategory: ProductCategory)

    /**
     * Deletes [ProductCategory]
     * @param productCategories list of [ProductCategory] to delete
     */
    suspend fun delete(productCategories: List<ProductCategory>)

    /**
     * Deletes [ProductCategoryAltName]
     * @param alternativeName [ProductCategoryAltName] to delete
     */
    suspend fun deleteAltName(alternativeName: ProductCategoryAltName)

    /**
     * Deletes [ProductCategoryAltName]
     * @param alternativeNames list of [ProductCategoryAltName] to delete
     */
    suspend fun deleteAltName(alternativeNames: List<ProductCategoryAltName>)

    // Read

    /**
     * @param categoryId id of the [ProductCategory]
     * @return [ProductCategory] matching [categoryId] id or null if none match
     */
    suspend fun get(categoryId: Long): ProductCategory?

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