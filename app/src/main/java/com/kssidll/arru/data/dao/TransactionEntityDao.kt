package com.kssidll.arru.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Dao
interface TransactionEntityDao {
    // Create

    @Insert
    suspend fun insert(entity: TransactionEntity): Long

    // Update

    @Update
    suspend fun update(entity: TransactionEntity)

    // Delete

    @Delete
    suspend fun delete(entity: TransactionEntity)

    // Helper

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.id = :shopId")
    suspend fun shopById(shopId: Long): ShopEntity?

    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE ItemEntity.id = :itemId")
    suspend fun itemById(itemId: Long): ItemEntity?

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.id = :productId")
    suspend fun productById(productId: Long): ProductEntity?

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariantEntity?

    @Query("SELECT ProductCategoryEntity.* FROM ProductCategoryEntity WHERE ProductCategoryEntity.id = :categoryId")
    suspend fun categoryById(categoryId: Long): ProductCategoryEntity?

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.id = :producerId")
    suspend fun producerById(producerId: Long): ProductProducerEntity?

    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE transactionEntityId = :transactionBasketId ORDER BY id DESC")
    suspend fun itemsByTransactionBasketId(transactionBasketId: Long): List<ItemEntity>

    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE transactionEntityId = :transactionBasketId ORDER BY id DESC")
    fun itemsByTransactionBasketIdFlow(transactionBasketId: Long): Flow<List<ItemEntity>>

    @Transaction
    suspend fun fullItemsByTransactionBasketId(transactionBasketId: Long): List<FullItem> {
        val transactionBasket = get(transactionBasketId).first() ?: return emptyList()

        val itemEntities = itemsByTransactionBasketId(transactionBasketId)

        if (itemEntities.isEmpty()) return emptyList()

        return itemEntities.map { item ->
            val productEntity = productById(item.productEntityId)!!
            val productVariantEntity = item.productVariantEntityId?.let { variantById(it) }
            val productCategoryEntity = categoryById(productEntity.productCategoryEntityId)!!
            val productProducerEntity = productEntity.productProducerEntityId?.let { producerById(it) }
            val shopEntity = transactionBasket.shopEntityId?.let { shopById(it) }

            FullItem(
                id = item.id,
                quantity = item.quantity,
                price = item.price,
                product = productEntity,
                variant = productVariantEntity,
                category = productCategoryEntity,
                producer = productProducerEntity,
                date = transactionBasket.date,
                shop = shopEntity,
            )
        }
    }

    @Query("SELECT ItemView.* FROM ItemView WHERE ItemView.transactionId = :id")
    suspend fun _itemsByTransactionBasketId(id: Long): List<Item>

    fun fullItemsByTransactionBasketIdFlow(transactionBasketId: Long): Flow<List<FullItem>> {
        val itemsFlow = itemsByTransactionBasketIdFlow(transactionBasketId)

        return itemsFlow.map { items ->
            val transactionBasket = get(transactionBasketId).first() ?: return@map emptyList()

            items.map { item ->
                val product = productById(item.productEntityId)!!
                val variant = item.productVariantEntityId?.let { variantById(it) }
                val category = categoryById(product.productCategoryEntityId)!!
                val producer = product.productProducerEntityId?.let { producerById(it) }
                val shop = transactionBasket.shopEntityId?.let { shopById(it) }

                FullItem(
                    id = item.id,
                    quantity = item.quantity,
                    price = item.price,
                    product = product,
                    variant = variant,
                    category = category,
                    producer = producer,
                    date = transactionBasket.date,
                    shop = shop,
                )
            }
        }
    }

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    // Read

    @Query("SELECT * FROM TransactionEntity WHERE TransactionEntity.id = :id")
    fun get(id: Long): Flow<TransactionEntity?>

    @Query("SELECT SUM(TransactionEntity.totalCost) FROM TransactionEntity")
    fun totalSpent(): Flow<Long?>

    @Query("SELECT ItemView.* FROM ItemView ORDER BY date DESC")
    fun items(): PagingSource<Int, Item>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT DATE(day, '+1 day') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
                date_series.day AS date, 
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                ROW_NUMBER() OVER (ORDER BY date ASC) data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByDay(): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'weekday 1') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT DATE(day, '+7 days') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+7 days') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'weekday 1') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
                date_series.day AS date, 
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                ROW_NUMBER() OVER (ORDER BY date ASC) data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByWeek(): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of month') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT DATE(day, '+1 month') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 month') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of month') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
            STRFTIME('%Y-%m', date_series.day) AS date, 
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                ROW_NUMBER() OVER (ORDER BY date ASC) data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByMonth(): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of year') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT DATE(day, '+1 year') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 year') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of year') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
            STRFTIME('%Y', date_series.day) AS date, 
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                ROW_NUMBER() OVER (ORDER BY date ASC) data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByYear(): Flow<List<TransactionSpentChartData>>











    @Query("SELECT COUNT(*) FROM TransactionEntity")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM TransactionEntity WHERE id < :entityId")
    suspend fun countBefore(entityId: Long): Int

    @Query("SELECT COUNT(*) FROM TransactionEntity WHERE id > :entityId")
    suspend fun countAfter(entityId: Long): Int

    @Query("SELECT * FROM TransactionEntity ORDER BY id DESC LIMIT 1")
    fun newest(): Flow<TransactionEntity?>

    @Query("SELECT TransactionEntity.* FROM TransactionEntity ORDER BY date DESC LIMIT :count OFFSET :startPosition")
    suspend fun partDateDesc(
        startPosition: Int,
        count: Int
    ): List<TransactionEntity>

    suspend fun transactionBasketsWithItems(
        startPosition: Int,
        count: Int
    ): List<TransactionBasketWithItems> {
        return partDateDesc(
            startPosition,
            count
        ).map { basket ->
            val shop = basket.shopEntityId?.let { shopById(it) }
            val items = _itemsByTransactionBasketId(basket.id)

            TransactionBasketWithItems(
                id = basket.id,
                date = basket.date,
                shop = shop,
                totalCost = basket.totalCost,
                items = items,
                note = basket.note
            )
        }
    }

    @Query("SELECT TransactionEntity.* FROM TransactionEntity ORDER BY date DESC")
    fun allPaged(): PagingSource<Int, TransactionEntity>

    fun transactionBasketWithItems(transactionEntityId: Long): Flow<TransactionBasketWithItems?> {
        return get(transactionEntityId).map { basket ->
            if (basket != null) {
                val shop = basket.shopEntityId?.let { shopById(it) }
                val items = _itemsByTransactionBasketId(basket.id)

                return@map TransactionBasketWithItems(
                    id = basket.id,
                    date = basket.date,
                    shop = shop,
                    totalCost = basket.totalCost,
                    items = items,
                    note = basket.note
                )
            } else return@map null
        }
    }
}