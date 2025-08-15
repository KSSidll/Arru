package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductPriceByShopByTime
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionBasket
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    // Create

    @Insert
    suspend fun insert(product: Product): Long

    // Update

    @Update
    suspend fun update(product: Product)

    // Delete

    @Delete
    suspend fun delete(product: Product)

    // Helper

    @Query("SELECT * FROM shop WHERE shop.id = :shopId")
    suspend fun shopById(shopId: Long): Shop

    @Query("SELECT * FROM productproducer WHERE productproducer.id = :producerId")
    suspend fun producerById(producerId: Long): ProductProducer?

    @Query("SELECT * FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariant?

    @Query("SELECT * FROM productvariant WHERE productvariant.productId = :productId AND productvariant.name = :variantName")
    suspend fun variantByName(
        productId: Long,
        variantName: String
    ): ProductVariant?

    @Query("SELECT * FROM productcategory WHERE productcategory.id = :categoryId")
    suspend fun categoryById(categoryId: Long): ProductCategory?

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
        WHERE product.id = :productId
        ORDER BY date DESC
        LIMIT :count
        OFFSET :offset
    """
    )
    suspend fun itemsByProduct(
        productId: Long,
        count: Int,
        offset: Int
    ): List<ItemEntity>

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.productId = :productId")
    suspend fun variants(productId: Long): List<ProductVariant>

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        WHERE product.id = :productId
    """
    )
    suspend fun getItems(productId: Long): List<ItemEntity>

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    @Delete
    suspend fun deleteVariants(variants: List<ProductVariant>)

    @Update
    suspend fun updateVariants(variants: List<ProductVariant>)

    @Update
    suspend fun updateItems(entities: List<ItemEntity>)

    @Query(
        """
        SELECT COUNT(*)
        FROM ItemEntity
        WHERE ItemEntity.id < :itemEntityId AND ItemEntity.productId = :productId
    """
    )
    suspend fun countItemsBefore(
        itemEntityId: Long,
        productId: Long
    ): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM ItemEntity
        WHERE ItemEntity.id > :itemEntityId AND ItemEntity.productId = :productId
    """
    )
    suspend fun countItemsAfter(
        itemEntityId: Long,
        productId: Long
    ): Int

    // Read

    @Query("SELECT product.* FROM product WHERE product.id = :productId")
    suspend fun get(productId: Long): Product?

    @Query("SELECT product.* FROM product WHERE product.id = :productId")
    fun getFlow(productId: Long): Flow<Product?>

    @Query("SELECT product.* FROM product WHERE product.name = :name")
    suspend fun byName(name: String): Product?

    @Query(
        """
        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        WHERE product.id = :productId
    """
    )
    fun totalSpentFlow(productId: Long): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(transactionbasket.date) AS start_date,
                   UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000 AS end_date
            FROM ItemEntity
            JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
            INNER JOIN product ON product.id = ItemEntity.productId
                AND productId = :productId
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), ItemEntities AS (
            SELECT (transactionbasket.date / 86400000) AS transaction_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
            FROM ItemEntity
            JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
            INNER JOIN product ON product.id = ItemEntity.productId
                AND productId = :productId
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
    fun totalSpentByDayFlow(productId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT (((MIN(transactionbasket.date) / 86400000) - ((MIN(transactionbasket.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 ((UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000) - 604800000) AS end_date
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
              AND productId = :productId
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), ItemEntities AS (
        SELECT ((transactionbasket.date - 345600000) / 604800000) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND productId = :productId
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
    fun totalSpentByWeekFlow(productId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of month') AS end_date
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND productId = :productId
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND productId = :productId
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
    fun totalSpentByMonthFlow(productId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of year') AS end_date
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND productId = :productId
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y', DATE(transactionbasket.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND productId = :productId
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
    fun totalSpentByYearFlow(productId: Long): Flow<List<ItemSpentByTime>>

    @Transaction
    suspend fun fullItems(
        productId: Long,
        count: Int,
        offset: Int
    ): List<FullItem> {
        val product = get(productId) ?: return emptyList()

        val itemEntities = itemsByProduct(
            productId,
            count,
            offset
        )

        if (itemEntities.isEmpty()) return emptyList()

        return itemEntities.map { entity ->
            val transactionBasket = transactionBasketByItemEntityId(entity.id)
            val variant = entity.variantId?.let { variantById(it) }
            val category = categoryById(product.categoryId)!!
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

    @Query("SELECT ItemEntity.* FROM product JOIN ItemEntity ON ItemEntity.productId = product.id WHERE product.id = :productId ORDER BY ItemEntity.id DESC LIMIT 1")
    suspend fun newestItem(productId: Long): ItemEntity?

    @Query(
        """
        WITH date_series AS (
            SELECT DATE(MIN(transactionbasket.date) / 1000, 'unixepoch', 'start of month') AS start_date,
                   DATE(current_timestamp, 'localtime', 'start of month') AS end_date
            FROM ItemEntity
            JOIN transactionbasket ON transactionBasket.id = ItemEntity.transactionBasketId
            WHERE productId = :productId
            UNION ALL
            SELECT DATE(start_date, '+1 month') AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        )
        SELECT product.*, AVG(ItemEntity.price) AS price, shop.name AS shopName, productvariant.name as variantName, productproducer.name as producerName, STRFTIME('%Y-%m', date_series.start_date) AS time
        FROM date_series
        LEFT JOIN transactionbasket ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(transactionbasket.date / 1000, 'unixepoch'))
        JOIN ItemEntity ON ItemEntity.transactionBasketId = transactionBasket.id
            AND ItemEntity.productId = :productId
        LEFT JOIN shop ON transactionbasket.shopId = shop.id
        LEFT JOIN productvariant ON ItemEntity.variantId = productvariant.id
        LEFT JOIN product ON ItemEntity.productId = product.id
        LEFT JOIN productproducer ON product.producerId = productproducer.id
        WHERE time IS NOT NULL
        GROUP BY time, shopId, variantId, producerId
        ORDER BY time
    """
    )
    fun averagePriceByVariantByShopByMonthFlow(productId: Long): Flow<List<ProductPriceByShopByTime>>

    @Query("SELECT product.* FROM product ORDER BY product.id DESC")
    fun allFlow(): Flow<List<Product>>

    @Query("SELECT COUNT(*) FROM product")
    suspend fun totalCount(): Int

    @Query("SELECT product.* FROM product ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<Product>
}