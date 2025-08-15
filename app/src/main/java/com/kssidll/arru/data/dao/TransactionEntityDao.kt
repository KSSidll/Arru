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
import com.kssidll.arru.data.data.TransactionSpentByTime
import kotlinx.coroutines.flow.Flow
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
        val transactionBasket = get(transactionBasketId) ?: return emptyList()

        val items = itemsByTransactionBasketId(transactionBasketId)

        if (items.isEmpty()) return emptyList()

        return items.map { item ->
            val product = productById(item.productId)!!
            val variant = item.variantId?.let { variantById(it) }
            val category = categoryById(product.categoryId)!!
            val producer = product.producerId?.let { producerById(it) }
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

    fun fullItemsByTransactionBasketIdFlow(transactionBasketId: Long): Flow<List<FullItem>> {
        val itemsFlow = itemsByTransactionBasketIdFlow(transactionBasketId)

        return itemsFlow.map { items ->
            val transactionBasket = get(transactionBasketId) ?: return@map emptyList()

            items.map { item ->
                val product = productById(item.productId)!!
                val variant = item.variantId?.let { variantById(it) }
                val category = categoryById(product.categoryId)!!
                val producer = product.producerId?.let { producerById(it) }
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

    @Query("SELECT * FROM TransactionEntity WHERE TransactionEntity.id = :entityId")
    suspend fun get(entityId: Long): TransactionEntity?

    @Query("SELECT * FROM TransactionEntity ORDER BY id DESC LIMIT 1")
    suspend fun newest(): TransactionEntity?

    @Query("SELECT COUNT(*) FROM TransactionEntity")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM TransactionEntity WHERE id < :entityId")
    suspend fun countBefore(entityId: Long): Int

    @Query("SELECT COUNT(*) FROM TransactionEntity WHERE id > :entityId")
    suspend fun countAfter(entityId: Long): Int

    @Query("SELECT * FROM TransactionEntity WHERE TransactionEntity.id = :entityId")
    fun getFlow(entityId: Long): Flow<TransactionEntity?>

    @Query("SELECT SUM(TransactionEntity.totalCost) FROM TransactionEntity")
    fun totalSpent(): Long?

    @Query("SELECT SUM(TransactionEntity.totalCost) FROM TransactionEntity")
    fun totalSpentFlow(): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(TransactionEntity.date) AS start_date,
                   UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000 AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), items AS (
            SELECT (TransactionEntity.date / 86400000) AS transaction_time, SUM(TransactionEntity.totalCost) AS item_total
            FROM TransactionEntity
            GROUP BY transaction_time
        )
        SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(item_total, 0) AS total
        FROM date_series
        LEFT JOIN items ON (date_series.start_date / 86400000) = transaction_time
        WHERE time IS NOT NULL
        GROUP BY time
        ORDER BY time
    """
    )
    fun totalSpentByDayFlow(): Flow<List<TransactionSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT (((MIN(TransactionEntity.date) / 86400000) - ((MIN(TransactionEntity.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 ((UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000) - 604800000) AS end_date
        FROM TransactionEntity
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), items AS (
        SELECT ((TransactionEntity.date - 345600000) / 604800000) AS items_time, SUM(TransactionEntity.totalCost) AS item_total
        FROM TransactionEntity
        GROUP BY items_time
    )
    SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(item_total, 0) AS total
    FROM date_series
    LEFT JOIN items ON (date_series.start_date / 604800000) = items_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByWeekFlow(): Flow<List<TransactionSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of month') AS end_date
        FROM TransactionEntity
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS items_time, SUM(TransactionEntity.totalCost) AS item_total
        FROM TransactionEntity
        GROUP BY items_time
    )
    SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(item_total, 0) AS total
    FROM date_series
    LEFT JOIN items ON STRFTIME('%Y-%m', date_series.start_date) = items_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByMonthFlow(): Flow<List<TransactionSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of year') AS end_date
        FROM TransactionEntity
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS items_time, SUM(TransactionEntity.totalCost) AS item_total
        FROM TransactionEntity
        GROUP BY items_time
    )
    SELECT STRFTIME('%Y', date_series.start_date) AS time, COALESCE(item_total, 0) AS total
    FROM date_series
    LEFT JOIN items ON STRFTIME('%Y', date_series.start_date) = items_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByYearFlow(): Flow<List<TransactionSpentByTime>>

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
            val items = fullItemsByTransactionBasketId(basket.id)

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
        return getFlow(transactionEntityId).map { basket ->
            if (basket != null) {
                val shop = basket.shopEntityId?.let { shopById(it) }
                val items = fullItemsByTransactionBasketId(basket.id)

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

    @Query("SELECT COUNT(*) FROM TransactionEntity")
    suspend fun totalCount(): Int

    @Query("SELECT TransactionEntity.* FROM TransactionEntity ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<TransactionEntity>
}