package com.kssidll.arru.data.dao

import androidx.room.*
import com.kssidll.arru.data.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    // Create

    @Insert
    suspend fun insert(shop: Shop): Long

    // Update

    @Update
    suspend fun update(shop: Shop)

    // Delete

    @Delete
    suspend fun delete(shop: Shop)

    // Helper

    @Query("SELECT * FROM product WHERE product.id = :productId")
    suspend fun productById(productId: Long): Product

    @Query("SELECT * FROM productproducer WHERE productproducer.id = :producerId")
    suspend fun producerById(producerId: Long): ProductProducer

    @Query("SELECT * FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariant

    @Query("SELECT * FROM productcategory WHERE productcategory.id = :categoryId")
    suspend fun categoryById(categoryId: Long): ProductCategory

    @Query(
        """
        SELECT transactionbasket.*
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        WHERE item.id = :itemId
    """
    )
    suspend fun transactionBasketByItemId(itemId: Long): TransactionBasket

    @Query(
        """
        SELECT item.*
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        JOIN product ON product.id = item.productId
        WHERE transactionbasket.shopId = :shopId
        ORDER BY date DESC
        LIMIT :count
        OFFSET :offset
    """
    )
    suspend fun itemsByShop(
        shopId: Long,
        count: Int,
        offset: Int
    ): List<Item>

    @Query(
        """
        SELECT item.*
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        WHERE shopId = :shopId
    """
    )
    suspend fun getItems(shopId: Long): List<Item>

    @Query("SELECT transactionbasket.* FROM transactionbasket WHERE transactionbasket.shopId = :shopId")
    suspend fun getTransactionBaskets(shopId: Long): List<TransactionBasket>

    @Update
    suspend fun updateTransactionBaskets(baskets: List<TransactionBasket>)

    @Delete
    suspend fun deleteTransactionBaskets(baskets: List<TransactionBasket>)

    @Delete
    suspend fun deleteItems(items: List<Item>)

    @Query(
        """
        SELECT COUNT(*)
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        WHERE item.id < :itemId AND transactionbasket.shopId = :shopId
    """
    )
    suspend fun countItemsBefore(
        itemId: Long,
        shopId: Long
    ): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        WHERE item.id > :itemId AND transactionbasket.shopId = :shopId
    """
    )
    suspend fun countItemsAfter(
        itemId: Long,
        shopId: Long
    ): Int

    // Read

    @Query("SELECT shop.* FROM shop WHERE shop.id = :shopId")
    suspend fun get(shopId: Long): Shop?

    @Query("SELECT shop.* FROM shop WHERE shop.id = :shopId")
    fun getFlow(shopId: Long): Flow<Shop?>

    @Query("SELECT shop.* FROM shop WHERE shop.name = :name")
    suspend fun byName(name: String): Shop?

    @Query(
        """
        SELECT SUM(transactionbasket.totalCost)
        FROM transactionbasket
        WHERE transactionbasket.shopId = :shopId
    """
    )
    fun totalSpentFlow(shopId: Long): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(transactionbasket.date) AS start_date,
                   MAX(transactionbasket.date) AS end_date
            FROM transactionbasket
                WHERE transactionbasket.shopId = :shopId
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
                WHERE date_series.end_date > date_series.start_date
        ), items AS (
            SELECT (transactionbasket.date / 86400000) AS transaction_time, SUM(transactionbasket.totalCost) AS transaction_total
            FROM transactionbasket
            WHERE transactionbasket.shopId = :shopId
            GROUP BY transaction_time
        )
        SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(transaction_total, 0) AS total
        FROM date_series
        LEFT JOIN items ON (date_series.start_date / 86400000) = transaction_time
        WHERE time IS NOT NULL
        GROUP BY time
        ORDER BY time
    """
    )
    fun totalSpentByDayFlow(shopId: Long): Flow<List<TransactionTotalSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT (((MIN(transactionbasket.date) / 86400000) - ((MIN(transactionbasket.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 (MAX(transactionbasket.date) - 604800000) AS end_date
        FROM transactionbasket
            WHERE transactionbasket.shopId = :shopId
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
            WHERE date_series.end_date >= date_series.start_date
    ), items AS (
        SELECT ((transactionbasket.date - 345600000) / 604800000) AS transaction_time, SUM(transactionbasket.totalCost) AS transaction_total
        FROM transactionbasket
        JOIN item ON item.transactionBasketId = transactionbasket.id
            AND transactionbasket.shopId = :shopId
        GROUP BY transaction_time
    )
    SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(transaction_total, 0) AS total
    FROM date_series
    LEFT JOIN items ON (date_series.start_date / 604800000) = transaction_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByWeekFlow(shopId: Long): Flow<List<TransactionTotalSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(MAX(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS end_date
        FROM transactionbasket
            WHERE transactionbasket.shopId = :shopId
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
            WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch')) AS transaction_time, SUM(transactionbasket.totalCost) AS transaction_total
        FROM transactionbasket
        WHERE transactionbasket.shopId = :shopId
        GROUP BY transaction_time
    )
    SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(transaction_total, 0) AS total
    FROM date_series
    LEFT JOIN items ON STRFTIME('%Y-%m', date_series.start_date) = transaction_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByMonthFlow(shopId: Long): Flow<List<TransactionTotalSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(MAX(transactionbasket.date) / 1000, 'unixepoch', 'start of year') AS end_date
        FROM transactionbasket
            WHERE transactionbasket.shopId = :shopId
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
            WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y', DATE(transactionbasket.date / 1000, 'unixepoch')) AS transaction_time, SUM(transactionbasket.totalCost) AS transaction_total
        FROM transactionbasket
        WHERE transactionbasket.shopId = :shopId
        GROUP BY transaction_time
    )
    SELECT STRFTIME('%Y', date_series.start_date) AS time, COALESCE(transaction_total, 0) AS total
    FROM date_series
    LEFT JOIN items ON STRFTIME('%Y', date_series.start_date) = transaction_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByYearFlow(shopId: Long): Flow<List<TransactionTotalSpentByTime>>

    @Transaction
    suspend fun fullItems(
        shopId: Long,
        count: Int,
        offset: Int
    ): List<FullItem> {
        val shop = get(shopId) ?: return emptyList()

        val items = itemsByShop(
            shopId,
            count,
            offset
        )

        if (items.isEmpty()) return emptyList()

        return items.map { item ->
            val transactionBasket = transactionBasketByItemId(item.id)
            val product = productById(item.productId)
            val variant = item.variantId?.let { variantById(it) }
            val category = categoryById(product.categoryId)
            val producer = product.producerId?.let { producerById(it) }

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

    @Query(
        """
        SELECT shop.*, SUM(transactionbasket.totalCost) as total
        FROM transactionbasket
        JOIN shop ON shop.id = transactionbasket.shopId
        GROUP BY shop.id
    """
    )
    fun totalSpentByShopFlow(): Flow<List<TransactionTotalSpentByShop>>

    @Query(
        """
        SELECT shop.*, SUM(transactionbasket.totalCost) as total
        FROM transactionbasket
        JOIN shop ON shop.id = transactionbasket.shopId
        WHERE STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch')) = :date
        GROUP BY shop.id
    """
    )
    fun totalSpentByShopByMonthFlow(date: String): Flow<List<TransactionTotalSpentByShop>>

    @Query("SELECT shop.* FROM shop ORDER BY shop.id DESC")
    fun allFlow(): Flow<List<Shop>>

    @Query("SELECT COUNT(*) FROM shop")
    suspend fun totalCount(): Int

    @Query("SELECT shop.* FROM shop ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<Shop>
}