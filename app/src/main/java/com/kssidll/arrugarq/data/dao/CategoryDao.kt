package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface CategoryDao {
    // Create

    @Insert
    suspend fun insert(productCategory: ProductCategory): Long

    @Insert
    suspend fun insertAltName(alternativeName: ProductCategoryAltName): Long

    // Update

    @Update
    suspend fun update(productCategory: ProductCategory)

    @Update
    suspend fun update(productCategories: List<ProductCategory>)

    @Update
    suspend fun updateAltName(alternativeName: ProductCategoryAltName)

    // Delete

    @Delete
    suspend fun delete(productCategory: ProductCategory)

    @Delete
    suspend fun delete(productCategories: List<ProductCategory>)

    @Delete
    suspend fun deleteAltName(alternativeName: ProductCategoryAltName)

    @Delete
    suspend fun deleteAltName(alternativeNames: List<ProductCategoryAltName>)

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

    @Query(
        """
        SELECT transactionbasket.*
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
            AND transactionbasketitem.itemId = :itemId
    """
    )
    suspend fun transactionBasketByItemId(itemId: Long): TransactionBasket

    @Query(
        """
        SELECT item.*
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
        JOIN item ON item.id = transactionbasketitem.itemId
        JOIN product ON product.id = item.productId
        WHERE product.categoryId = :categoryId
        ORDER BY date DESC
        LIMIT :count
        OFFSET :offset
    """
    )
    suspend fun itemsByCategory(
        categoryId: Long,
        count: Int,
        offset: Int
    ): List<Item>

    @Query("SELECT productcategoryaltname.* FROM productcategoryaltname WHERE productcategoryaltname.productCategoryId = :categoryId")
    suspend fun altNames(categoryId: Long): List<ProductCategoryAltName>

    // Read

    @Query("SELECT productcategory.* FROM productcategory WHERE productcategory.id = :categoryId")
    suspend fun get(categoryId: Long): ProductCategory?

    @Query(
        """
        SELECT SUM(item.price * item.quantity)
        FROM item
        JOIN product ON product.id = item.productId
        JOIN productcategory ON productcategory.id = product.categoryId
        WHERE productcategory.id = :categoryId
    """
    )
    fun totalSpentFlow(categoryId: Long): Flow<Long>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(transactionbasket.date) AS start_date,
                   MAX(transactionbasket.date) AS end_date
            FROM transactionbasket
            JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
            JOIN item ON item.id = transactionbasketitem.itemId
            INNER JOIN product ON product.id = item.productId
                AND categoryId = :categoryId
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), items AS (
            SELECT (transactionbasket.date / 86400000) AS transaction_time, SUM(item.price * item.quantity) AS item_total
            FROM transactionbasket
            JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
            JOIN item ON item.id = transactionbasketitem.itemId
            INNER JOIN product ON product.id = item.productId
                AND categoryId = :categoryId
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
    fun totalSpentByDayFlow(categoryId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT (((MIN(transactionbasket.date) / 86400000) - ((MIN(transactionbasket.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 (MAX(transactionbasket.date) - 604800000) AS end_date
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
        JOIN item ON item.id = transactionbasketitem.itemId
        INNER JOIN product ON product.id = item.productId
            AND categoryId = :categoryId
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), items AS (
        SELECT ((transactionbasket.date - 345600000) / 604800000) AS items_time, SUM(item.price * item.quantity) AS item_total
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
        JOIN item ON item.id = transactionbasketitem.itemId
        INNER JOIN product ON product.id = item.productId
            AND categoryId = :categoryId
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
    fun totalSpentByWeekFlow(categoryId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(MAX(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS end_date
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
        JOIN item ON item.id = transactionbasketitem.itemId
        INNER JOIN product ON product.id = item.productId
            AND categoryId = :categoryId
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
        JOIN item ON item.id = transactionbasketitem.itemId
        INNER JOIN product ON product.id = item.productId
            AND categoryId = :categoryId
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
    fun totalSpentByMonthFlow(categoryId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(MAX(transactionbasket.date) / 1000, 'unixepoch', 'start of year') AS end_date
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
        JOIN item ON item.id = transactionbasketitem.itemId
        INNER JOIN product ON product.id = item.productId
            AND categoryId = :categoryId
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y', DATE(transactionbasket.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
        JOIN item ON item.id = transactionbasketitem.itemId
        INNER JOIN product ON product.id = item.productId
            AND categoryId = :categoryId
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
    fun totalSpentByYearFlow(categoryId: Long): Flow<List<ItemSpentByTime>>

    @Transaction
    suspend fun fullItems(
        categoryId: Long,
        count: Int,
        offset: Int
    ): List<FullItem> {
        val category = get(categoryId) ?: return emptyList()

        val items = itemsByCategory(
            categoryId,
            count,
            offset
        )

        if (items.isEmpty()) return emptyList()

        return items.map { item ->
            val transactionBasket = transactionBasketByItemId(item.id)
            val product = productById(item.productId)
            val variant = item.variantId?.let { variantById(it) }
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

    @Query(
        """
        SELECT productcategory.*, SUM(item.price * item.quantity) as total
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
        JOIN item ON item.id = transactionbasketitem.itemId
        JOIN product ON product.id = item.productId
        JOIN productcategory ON productcategory.id = product.categoryId
        GROUP BY productcategory.id
    """
    )
    fun totalSpentByCategoryFlow(): Flow<List<ItemSpentByCategory>>

    @Query(
        """
        SELECT productcategory.*, SUM(item.price * item.quantity) as total
        FROM transactionbasket
        JOIN transactionbasketitem ON transactionbasketitem.transactionBasketId = transactionbasket.id
        JOIN item ON item.id = transactionbasketitem.itemId
        INNER JOIN product ON item.productId = product.id
        INNER JOIN productcategory ON product.categoryId = productcategory.id
        WHERE STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch')) = :date
        GROUP BY productcategory.id
    """
    )
    fun totalSpentByCategoryByMonthFlow(date: String): Flow<List<ItemSpentByCategory>>

    @Query("SELECT productcategory.* FROM productcategory ORDER BY productcategory.id DESC")
    fun allFlow(): Flow<List<ProductCategory>>

    fun allWithAltNamesFlow(): Flow<List<ProductCategoryWithAltNames>> {
        return allFlow().map { list ->
            list.map { item ->
                ProductCategoryWithAltNames(
                    category = item,
                    alternativeNames = altNames(item.id),
                )
            }
        }
    }
}