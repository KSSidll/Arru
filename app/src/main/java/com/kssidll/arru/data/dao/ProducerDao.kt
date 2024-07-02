package com.kssidll.arru.data.dao

import androidx.room.*
import com.kssidll.arru.data.data.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProducerDao {
    // Create

    @Insert
    suspend fun insert(producer: ProductProducer): Long

    // Update

    @Update
    suspend fun update(producer: ProductProducer)

    // Delete

    @Delete
    suspend fun delete(producer: ProductProducer)

    // Helper

    @Query("SELECT * FROM shop WHERE shop.id = :shopId")
    suspend fun shopById(shopId: Long): Shop

    @Query("SELECT * FROM product WHERE product.id = :productId")
    suspend fun productById(productId: Long): Product

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
        WHERE product.producerId = :producerId
        ORDER BY date DESC
        LIMIT :count
        OFFSET :offset
    """
    )
    suspend fun itemsByProducer(
        producerId: Long,
        count: Int,
        offset: Int
    ): List<Item>

    @Query(
        """
        SELECT product.*
        FROM product
        JOIN productproducer ON productproducer.id = product.producerId
        WHERE productproducer.id = :producerId
    """
    )
    suspend fun getProducts(producerId: Long): List<Product>

    @Query(
        """
        SELECT productvariant.*
        FROM productvariant
        JOIN product ON product.id = productvariant.productId
        JOIN productproducer ON productproducer.id = product.producerId
        WHERE productproducer.id = :producerId
    """
    )
    suspend fun getProductsVariants(producerId: Long): List<ProductVariant>

    @Query(
        """
        SELECT productaltname.*
        FROM productaltname
        JOIN product ON product.id = productaltname.productId
        JOIN productproducer ON productproducer.id = product.producerId
        WHERE productproducer.id = :producerId
    """
    )
    suspend fun getProductsAltNames(producerId: Long): List<ProductAltName>

    @Query(
        """
        SELECT item.*
        FROM item
        JOIN product ON product.id = item.productId
        JOIN productproducer ON productproducer.id = product.producerId
        WHERE productproducer.id = :producerId
    """
    )
    suspend fun getItems(producerId: Long): List<Item>

    @Delete
    suspend fun deleteProducts(products: List<Product>)

    @Delete
    suspend fun deleteProductVariants(productVariants: List<ProductVariant>)

    @Delete
    suspend fun deleteProductAltNames(productAltNames: List<ProductAltName>)

    @Delete
    suspend fun deleteItems(items: List<Item>)

    @Update
    suspend fun updateProducts(products: List<Product>)

    @Query(
        """
        SELECT COUNT(*)
        FROM item
        JOIN product ON product.id = item.productId
        WHERE item.id < :itemId AND product.producerId = :producerId
    """
    )
    suspend fun countItemsBefore(
        itemId: Long,
        producerId: Long
    ): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM item
        JOIN product ON product.id = item.productId
        WHERE item.id > :itemId AND product.producerId = :producerId
    """
    )
    suspend fun countItemsAfter(
        itemId: Long,
        producerId: Long
    ): Int

    // Read

    @Query("SELECT productproducer.* FROM productproducer WHERE productproducer.id = :producerId")
    suspend fun get(producerId: Long): ProductProducer?

    @Query("SELECT productproducer.* FROM productproducer WHERE productproducer.id = :producerId")
    fun getFlow(producerId: Long): Flow<ProductProducer?>

    @Query("SELECT productproducer.* FROM productproducer WHERE productproducer.name = :name")
    suspend fun byName(name: String): ProductProducer?

    @Query(
        """
        SELECT SUM(item.price * item.quantity)
        FROM item
        JOIN product ON product.id = item.productId
        WHERE product.producerId = :producerId
    """
    )
    fun totalSpentFlow(producerId: Long): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(transactionbasket.date) AS start_date,
                   MAX(transactionbasket.date) AS end_date
            FROM item
            JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
            INNER JOIN product ON product.id = item.productId
                AND producerId = :producerId
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), items AS (
            SELECT (transactionbasket.date / 86400000) AS transaction_time, SUM(item.price * item.quantity) AS item_total
            FROM item
            JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
            INNER JOIN product ON product.id = item.productId
                AND producerId = :producerId
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
    fun totalSpentByDayFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT (((MIN(transactionbasket.date) / 86400000) - ((MIN(transactionbasket.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 (MAX(transactionbasket.date) - 604800000) AS end_date
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        INNER JOIN product ON product.id = item.productId
            AND producerId = :producerId
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), items AS (
        SELECT ((transactionbasket.date - 345600000) / 604800000) AS items_time, SUM(item.price * item.quantity) AS item_total
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        INNER JOIN product ON product.id = item.productId
            AND producerId = :producerId
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
    fun totalSpentByWeekFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(MAX(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS end_date
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        INNER JOIN product ON product.id = item.productId
            AND producerId = :producerId
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        INNER JOIN product ON product.id = item.productId
            AND producerId = :producerId
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
    fun totalSpentByMonthFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(MAX(transactionbasket.date) / 1000, 'unixepoch', 'start of year') AS end_date
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        INNER JOIN product ON product.id = item.productId
            AND producerId = :producerId
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y', DATE(transactionbasket.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
        FROM item
        JOIN transactionbasket ON transactionBasket.id = item.transactionBasketId
        INNER JOIN product ON product.id = item.productId
            AND producerId = :producerId
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
    fun totalSpentByYearFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Transaction
    suspend fun fullItems(
        producerId: Long,
        count: Int,
        offset: Int
    ): List<FullItem> {
        val producer = get(producerId) ?: return emptyList()

        val items = itemsByProducer(
            producerId,
            count,
            offset
        )

        if (items.isEmpty()) return emptyList()

        return items.map { item ->
            val transactionBasket = transactionBasketByItemId(item.id)
            val product = productById(item.productId)
            val variant = item.variantId?.let { variantById(it) }
            val category = categoryById(product.categoryId)
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

    @Query("SELECT productproducer.* FROM productproducer ORDER BY productproducer.id DESC")
    fun allFlow(): Flow<List<ProductProducer>>
}
