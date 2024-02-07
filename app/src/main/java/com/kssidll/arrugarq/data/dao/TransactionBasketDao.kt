package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface TransactionBasketDao {
    // Create

    @Insert
    suspend fun insert(transactionBasket: TransactionBasket): Long

    @Insert
    suspend fun insertTransactionBasketItem(transactionBasketItem: TransactionBasketItem): Long

    // Update

    @Update
    suspend fun update(transactionBasket: TransactionBasket)

    @Update
    suspend fun update(transactionBaskets: List<TransactionBasket>)

    // Delete

    @Delete
    suspend fun delete(transactionBasket: TransactionBasket)

    @Delete
    suspend fun delete(transactionBaskets: List<TransactionBasket>)

    @Delete
    suspend fun deleteTransactionBasketItem(transactionBasketItem: TransactionBasketItem)

    @Delete
    suspend fun deleteTransactionBasketItem(transactionBasketItems: List<TransactionBasketItem>)

    // Helper

    @Query("SELECT * FROM shop WHERE shop.id = :shopId")
    suspend fun shopById(shopId: Long): Shop

    @Query("SELECT * FROM product WHERE product.id = :productId")
    suspend fun productById(productId: Long): Product

    @Query("SELECT * FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariant

    @Query("SELECT * FROM productcategory WHERE productcategory.id = :categoryId")
    suspend fun categoryById(categoryId: Long): ProductCategory

    @Query("SELECT * FROM productproducer WHERE productproducer.id = :producerId")
    suspend fun producerById(producerId: Long): ProductProducer

    @Query("SELECT item.* FROM transactionbasketitem JOIN item ON item.id = transactionbasketitem.itemId WHERE transactionbasketitem.transactionBasketId = :transactionBasketId")
    suspend fun itemsByTransactionBasketId(transactionBasketId: Long): List<Item>

    @Transaction
    suspend fun fullItemsByTransactionBasketId(transactionBasketId: Long): List<FullItem> {
        val transactionBasket = get(transactionBasketId) ?: return emptyList()

        val items = itemsByTransactionBasketId(transactionBasketId)

        if (items.isEmpty()) return emptyList()

        return items.map { item ->
            val product = productById(item.productId)
            val variant = item.variantId?.let { variantById(it) }
            val category = categoryById(product.categoryId)
            val producer = product.producerId?.let { producerById(it) }
            val shop = transactionBasket.shopId?.let { shopById(it) }

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

    // Read

    @Query("SELECT * FROM transactionbasket WHERE transactionbasket.id = :transactionBasketId")
    suspend fun get(transactionBasketId: Long): TransactionBasket?

    @Query("SELECT SUM(transactionbasket.totalCost) FROM transactionbasket")
    fun totalSpentFlow(): Flow<Long>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(transactionbasket.date) AS start_date,
                   MAX(transactionbasket.date) AS end_date
            FROM transactionbasket
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), items AS (
            SELECT (transactionbasket.date / 86400000) AS transaction_time, SUM(transactionbasket.totalCost) AS item_total
            FROM transactionbasket
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
        SELECT (((MIN(transactionbasket.date) / 86400000) - ((MIN(transactionbasket.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 (MAX(transactionbasket.date) - 604800000) AS end_date
        FROM transactionbasket
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), items AS (
        SELECT ((transactionbasket.date - 345600000) / 604800000) AS items_time, SUM(transactionbasket.totalCost) AS item_total
        FROM transactionbasket
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
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(MAX(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS end_date
        FROM transactionbasket
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch')) AS items_time, SUM(transactionbasket.totalCost) AS item_total
        FROM transactionbasket
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
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(MAX(transactionbasket.date) / 1000, 'unixepoch', 'start of year') AS end_date
        FROM transactionbasket
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y', DATE(transactionbasket.date / 1000, 'unixepoch')) AS items_time, SUM(transactionbasket.totalCost) AS item_total
        FROM transactionbasket
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

    @Query("SELECT transactionbasket.* FROM transactionbasket ORDER BY id DESC LIMIT :count OFFSET :startPosition")
    suspend fun partDesc(
        startPosition: Int,
        count: Int
    ): List<TransactionBasket>

    suspend fun transactionBasketsWithItems(
        startPosition: Int,
        count: Int
    ): List<TransactionBasketWithItems> {
        return partDesc(
            startPosition,
            count
        ).map { basket ->
            val shop = basket.shopId?.let { shopById(it) }
            val items = fullItemsByTransactionBasketId(basket.id)

            TransactionBasketWithItems(
                id = basket.id,
                date = basket.date,
                shop = shop,
                totalCost = basket.totalCost,
                items = items,
            )
        }
    }
}