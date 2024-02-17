package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.CategoryRepositorySource.Companion.MergeResult
import kotlinx.coroutines.flow.*

interface ProductRepositorySource {
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
            data object InvalidCategoryId: Errors()
            data object InvalidProducerId: Errors()
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
            data object InvalidCategoryId: Errors()
            data object InvalidProducerId: Errors()
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
            data object InvalidProductId: Errors()
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
            data object InvalidProduct: Errors()
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
     * Inserts [Product]
     * @param name name of the [Product] to insert
     * @param categoryId id of the [ProductCategory] of the [Product] to insert
     * @param producerId id of the [ProductProducer] of the [Product] to insert
     * @return [InsertResult] with id of the newly inserted [Product] or an error if any
     */
    suspend fun insert(
        name: String,
        categoryId: Long,
        producerId: Long?
    ): InsertResult

    /**
     * Inserts [ProductAltName]
     * @param product [Product] to add the [alternativeName] to
     * @param alternativeName alternative name to add to the [product]
     * @return [AltInsertResult] with id of the newly inserted [Product] or an error if any
     */
    suspend fun insertAltName(
        product: Product,
        alternativeName: String
    ): AltInsertResult

    // Update

    /**
     * Updates [Product] with [productId] id to provided [name], [categoryId] and [producerId]
     * @param productId id to match [Product]
     * @param name name to update the matching [Product] to
     * @param categoryId id of the [ProductCategory] to update the matching [Product] to
     * @param producerId id of the [ProductProducer] to update the matching [Product] to
     * @return [UpdateResult] with the result
     */
    suspend fun update(
        productId: Long,
        name: String,
        categoryId: Long,
        producerId: Long?
    ): UpdateResult

    /**
     * Updates [ProductAltName] with [alternativeNameId] id to provided [productId] and [name]
     * @param alternativeNameId id to match [ProductAltName]
     * @param productId product id to update the matching [ProductAltName] to
     * @param name name to update the matching [ProductAltName] to
     * @return [UpdateResult] with the result
     */
    suspend fun updateAltName(
        alternativeNameId: Long,
        productId: Long,
        name: String
    ): AltUpdateResult

    /**
     * Merges [product] into [mergingInto]
     * @param product [Product] to merge
     * @param mergingInto [Product] to merge the [product] into
     * @return [MergeResult] with the result
     */
    suspend fun merge(
        product: Product,
        mergingInto: Product,
    ): MergeResult

    // Delete

    /**
     * Deletes [Product] matching [productId]
     * @param productId id of the [Product] to delete
     * @param force whether to force delete on dangerous delete
     * @return [DeleteResult] with the result
     */
    suspend fun delete(
        productId: Long,
        force: Boolean
    ): DeleteResult

    /**
     * Deletes [ProductAltName] matching [alternativeNameId]
     * @param alternativeNameId id of the [ProductAltName] to delete
     * @return [DeleteResult] with the result
     */
    suspend fun deleteAltName(alternativeNameId: Long): DeleteResult

    // Read

    /**
     * @param productId id of the [Product]
     * @return [Product] matching [productId] id or null if none match
     */
    suspend fun get(productId: Long): Product?

    /**
     * @param productId id of the [Product]
     * @return [Product] matching [productId] id or null if none match, as flow
     */
    fun getFlow(productId: Long): Flow<Product?>

    /**
     * @param product [Product] to get the total spending from
     * @return long representing total spending for the [product] as flow
     */
    fun totalSpentFlow(product: Product): Flow<Long>

    /**
     * @param product [Product] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(product: Product): Flow<List<ItemSpentByTime>>

    /**
     * @param product [Product] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(product: Product): Flow<List<ItemSpentByTime>>

    /**
     * @param product [Product] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(product: Product): Flow<List<ItemSpentByTime>>

    /**
     * @param product [Product] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(product: Product): Flow<List<ItemSpentByTime>>

    /**
     * @param product [Product] to match the items to
     */
    fun fullItemsPagedFlow(product: Product): Flow<PagingData<FullItem>>

    /**
     * @param product [Product] to match the [Item] with
     * @return newest [Item] that matches [product], null if none match
     */
    suspend fun newestItem(product: Product): Item?

    /**
     * @return list of all [ProductWithAltNames] as flow
     */
    fun allWithAltNamesFlow(): Flow<List<ProductWithAltNames>>

    /**
     * @param product [Product] to match the data with
     * @return list of [ProductPriceByShopByTime] representing the average price of [product] groupped by variant, shop and month as flow
     */
    fun averagePriceByVariantByShopByMonthFlow(product: Product): Flow<List<ProductPriceByShopByTime>>

    /**
     * @return list of all [Product] as flow
     */
    fun allFlow(): Flow<List<Product>>
}