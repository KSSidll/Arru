package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface ShopRepositorySource {
    // Create

    /**
     * Inserts [Shop]
     * @param shop [Shop] to insert
     * @return id of newly inserted [Shop]
     */
    suspend fun insert(shop: Shop): Long

    // Update

    /**
     * Updates matching [Shop] to provided [shop]
     *
     * Matches by id
     * @param shop [Shop] to update
     */
    suspend fun update(shop: Shop)

    /**
     * Updates all matching [Shop] to provided [shops]
     *
     * Matches by id
     * @param shops list of [Shop] to update
     */
    suspend fun update(shops: List<Shop>)

    // Delete

    /**
     * Deletes [Shop]
     * @param shop [Shop] to delete
     */
    suspend fun delete(shop: Shop)

    /**
     * Deletes [Shop]
     * @param shops list of [Shop] to delete
     */
    suspend fun delete(shops: List<Shop>)

    // Read

    /**
     * @param shopId id of the [Shop]
     * @return [Shop] matching [shopId] id or null if none match
     */
    suspend fun get(shopId: Long): Shop?

    /**
     * @param shop [Shop] to get the total spending from
     * @return long representing total spending for the [shop] as flow
     */
    fun totalSpentFlow(shop: Shop): Flow<Long>

    /**
     * @param shop [Shop] to get the total spending by day from
     * @return list of [ItemSpentByTime] representing total spending groupped by day as flow
     */
    fun totalSpentByDayFlow(shop: Shop): Flow<List<ItemSpentByTime>>

    /**
     * @param shop [Shop] to get the total spending by week from
     * @return list of [ItemSpentByTime] representing total spending groupped by week as flow
     */
    fun totalSpentByWeekFlow(shop: Shop): Flow<List<ItemSpentByTime>>

    /**
     * @param shop [Shop] to get the total spending by month from
     * @return list of [ItemSpentByTime] representing total spending groupped by month as flow
     */
    fun totalSpentByMonthFlow(shop: Shop): Flow<List<ItemSpentByTime>>

    /**
     * @param shop [Shop] to get the total spending by year from
     * @return list of [ItemSpentByTime] representing total spending groupped by year as flow
     */
    fun totalSpentByYearFlow(shop: Shop): Flow<List<ItemSpentByTime>>

    /**
     * @param shop [Shop] to match the items to
     */
    fun fullItemsPagedFlow(shop: Shop): Flow<PagingData<FullItem>>

    /**
     * @return list of [ItemSpentByShop] representing total spending groupped by shop
     */
    fun totalSpentByShopFlow(): Flow<List<ItemSpentByShop>>

    /**
     * @param year year to match the data to
     * @param month month to match the data to
     * @return list of [ItemSpentByShop] representing total spending groupped by shop in [year] and [month]
     */
    fun totalSpentByShopByMonthFlow(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByShop>>

    /**
     * @return list of all [Shop] as flow
     */
    fun allFlow(): Flow<List<Shop>>
}