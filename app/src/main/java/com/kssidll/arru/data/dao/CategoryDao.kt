package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionBasket
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    // Create

    @Insert
    suspend fun insert(productCategory: ProductCategory): Long

    // Update

    @Update
    suspend fun update(productCategory: ProductCategory)

    @Update
    suspend fun update(productCategories: List<ProductCategory>)

    // Delete

    @Delete
    suspend fun delete(productCategory: ProductCategory)

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
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        WHERE ItemEntity.id = :itemEntityId
    """
    )
    suspend fun transactionBasketByItemEntityId(itemEntityId: Long): TransactionBasket

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        JOIN product ON product.id = ItemEntity.productId
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
    ): List<ItemEntity>

    @Query(
        """
        SELECT product.*
        FROM product
        JOIN productcategory ON productcategory.id = product.categoryId
        WHERE productcategory.id = :categoryId
    """
    )
    suspend fun getProducts(categoryId: Long): List<Product>

    @Query(
        """
        SELECT productvariant.*
        FROM productvariant
        JOIN product ON product.id = productvariant.productId
        JOIN productcategory ON productcategory.id = product.categoryId
        WHERE productcategory.id = :categoryId
    """
    )
    suspend fun getProductsVariants(categoryId: Long): List<ProductVariant>

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        JOIN productcategory ON productcategory.id = product.categoryId
        WHERE productcategory.id = :categoryId
    """
    )
    suspend fun getItems(categoryId: Long): List<ItemEntity>

    @Delete
    suspend fun deleteProducts(products: List<Product>)

    @Delete
    suspend fun deleteProductVariants(productVariants: List<ProductVariant>)

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    @Update
    suspend fun updateProducts(products: List<Product>)

    @Query(
        """
        SELECT COUNT(*)
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        WHERE ItemEntity.id < :itemEntityId AND product.categoryId = :categoryId
    """
    )
    suspend fun countItemsBefore(
        itemEntityId: Long,
        categoryId: Long
    ): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        WHERE ItemEntity.id > :itemEntityId AND product.categoryId = :categoryId
    """
    )
    suspend fun countItemsAfter(
        itemEntityId: Long,
        categoryId: Long
    ): Int

    // Read

    @Query("SELECT productcategory.* FROM productcategory WHERE productcategory.id = :categoryId")
    suspend fun get(categoryId: Long): ProductCategory?

    @Query("SELECT productcategory.* FROM productcategory WHERE productcategory.id = :categoryId")
    fun getFlow(categoryId: Long): Flow<ProductCategory?>

    @Query("SELECT productcategory.* FROM productcategory WHERE productcategory.name = :name")
    suspend fun byName(name: String): ProductCategory?

    @Query(
        """
        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        JOIN productcategory ON productcategory.id = product.categoryId
        WHERE productcategory.id = :categoryId
    """
    )
    fun totalSpentFlow(categoryId: Long): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(transactionbasket.date) AS start_date,
                   UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000 AS end_date
            FROM ItemEntity
            JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
            INNER JOIN product ON product.id = ItemEntity.productId
                AND categoryId = :categoryId
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), ItemEntities AS (
            SELECT (transactionbasket.date / 86400000) AS transaction_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
            FROM ItemEntity
            JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
            INNER JOIN product ON product.id = ItemEntity.productId
                AND categoryId = :categoryId
            GROUP BY transaction_time
        )
        SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(ItemEntity_total, 0) AS total
        FROM date_series
        LEFT JOIN ItemEntities ON (date_series.start_date / 86400000) = transaction_time
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
                 ((UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000) - 604800000) AS end_date
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND categoryId = :categoryId
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), ItemEntities AS (
        SELECT ((transactionbasket.date - 345600000) / 604800000) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND categoryId = :categoryId
        GROUP BY ItemEntities_time
    )
    SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(ItemEntity_total, 0) AS total
    FROM date_series
    LEFT JOIN ItemEntities ON (date_series.start_date / 604800000) = ItemEntities_time
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
               DATE(current_timestamp, 'localtime', 'start of month') AS end_date
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND categoryId = :categoryId
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND categoryId = :categoryId
        GROUP BY ItemEntities_time
    )
    SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(ItemEntity_total, 0) AS total
    FROM date_series
    LEFT JOIN ItemEntities ON STRFTIME('%Y-%m', date_series.start_date) = ItemEntities_time
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
               DATE(current_timestamp, 'localtime', 'start of year') AS end_date
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND categoryId = :categoryId
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y', DATE(transactionbasket.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND categoryId = :categoryId
        GROUP BY ItemEntities_time
    )
    SELECT STRFTIME('%Y', date_series.start_date) AS time, COALESCE(ItemEntity_total, 0) AS total
    FROM date_series
    LEFT JOIN ItemEntities ON STRFTIME('%Y', date_series.start_date) = ItemEntities_time
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

        val itemEntities = itemsByCategory(
            categoryId,
            count,
            offset
        )

        if (itemEntities.isEmpty()) return emptyList()

        return itemEntities.map { entity ->
            val transactionBasket = transactionBasketByItemEntityId(entity.id)
            val product = productById(entity.productId)
            val variant = entity.variantId?.let { variantById(it) }
            val producer = product.producerId?.let { producerById(it) }
            val shop = transactionBasket.shopId?.let { shopById(it) }

            FullItem(
                id = entity.id,
                quantity = entity.quantity,
                price = entity.price,
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
        SELECT productcategory.*, SUM(ItemEntity.price * ItemEntity.quantity) as total
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        JOIN product ON product.id = ItemEntity.productId
        JOIN productcategory ON productcategory.id = product.categoryId
        GROUP BY productcategory.id
    """
    )
    fun totalSpentByCategoryFlow(): Flow<List<ItemSpentByCategory>>

    @Query(
        """
        SELECT productcategory.*, SUM(ItemEntity.price * ItemEntity.quantity) as total
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON ItemEntity.productId = product.id
        INNER JOIN productcategory ON product.categoryId = productcategory.id
        WHERE STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch')) = :date
        GROUP BY productcategory.id
    """
    )
    fun totalSpentByCategoryByMonthFlow(date: String): Flow<List<ItemSpentByCategory>>

    @Query("SELECT productcategory.* FROM productcategory ORDER BY productcategory.id DESC")
    fun allFlow(): Flow<List<ProductCategory>>

    @Query("SELECT COUNT(*) FROM productcategory")
    suspend fun totalCount(): Int

    @Query("SELECT productcategory.* FROM productcategory ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<ProductCategory>
}