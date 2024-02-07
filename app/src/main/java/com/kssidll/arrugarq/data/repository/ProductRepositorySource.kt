package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface ProductRepositorySource {
    // Create

    /**
     * Inserts [Product]
     * @param product [Product] to insert
     * @return id of newly inserted [Product]
     */
    suspend fun insert(product: Product): Long

    /**
     * Inserts [ProductAltName]
     * @param alternativeName [ProductAltName] to insert
     * @return id of newly inserted [ProductAltName]
     */
    suspend fun insertAltName(alternativeName: ProductAltName): Long

    // Update

    /**
     * Updates matching [Product] to provided [product]
     *
     * Matches by id
     * @param product [Product] to update
     */
    suspend fun update(product: Product)

    /**
     * Updates all matching [Product] to provided [products]
     *
     * Matches by id
     * @param products list of [Product] to update
     */
    suspend fun update(products: List<Product>)

    /**
     * Updates matching [ProductAltName] to provided [alternativeName]
     *
     * Matches by id
     * @param alternativeName [ProductAltName] to update
     */
    suspend fun updateAltName(alternativeName: ProductAltName)

    // Delete

    /**
     * Deletes [Product]
     * @param product [Product] to delete
     */
    suspend fun delete(product: Product)

    /**
     * Deletes [Product]
     * @param products list of [Product] to delete
     */
    suspend fun delete(products: List<Product>)

    /**
     * Deletes [ProductAltName]
     * @param alternativeName [ProductAltName] to delete
     */
    suspend fun deleteAltName(alternativeName: ProductAltName)

    /**
     * Deletes [ProductAltName]
     * @param alternativeNames list of [ProductAltName] to delete
     */
    suspend fun deleteAltName(alternativeNames: List<ProductAltName>)

    // Read

    /**
     * @param productId id of the [Product]
     * @return [Product] matching [productId] id or null if none match
     */
    suspend fun get(productId: Long): Product?

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