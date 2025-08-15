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
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionEntity
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
        SELECT TransactionEntity.*
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        WHERE ItemEntity.id = :itemEntityId
    """
    )
    suspend fun transactionEntityByItemEntityId(itemEntityId: Long): TransactionEntity

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        JOIN product ON product.id = ItemEntity.productId
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
    ): List<ItemEntity>

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
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        JOIN productproducer ON productproducer.id = product.producerId
        WHERE productproducer.id = :producerId
    """
    )
    suspend fun getItems(producerId: Long): List<ItemEntity>

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
        WHERE ItemEntity.id < :itemEntityId AND product.producerId = :producerId
    """
    )
    suspend fun countItemsBefore(
        itemEntityId: Long,
        producerId: Long
    ): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        WHERE ItemEntity.id > :itemEntityId AND product.producerId = :producerId
    """
    )
    suspend fun countItemsAfter(
        itemEntityId: Long,
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
        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        WHERE product.producerId = :producerId
    """
    )
    fun totalSpentFlow(producerId: Long): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(TransactionEntity.date) AS start_date,
                   UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000 AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN product ON product.id = ItemEntity.productId
                AND producerId = :producerId
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), ItemEntities AS (
            SELECT (TransactionEntity.date / 86400000) AS transaction_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN product ON product.id = ItemEntity.productId
                AND producerId = :producerId
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
    fun totalSpentByDayFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT (((MIN(TransactionEntity.date) / 86400000) - ((MIN(TransactionEntity.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 ((UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000) - 604800000) AS end_date
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND producerId = :producerId
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), ItemEntities AS (
        SELECT ((TransactionEntity.date - 345600000) / 604800000) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND producerId = :producerId
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
    fun totalSpentByWeekFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of month') AS end_date
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND producerId = :producerId
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND producerId = :producerId
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
    fun totalSpentByMonthFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of year') AS end_date
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND producerId = :producerId
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN product ON product.id = ItemEntity.productId
            AND producerId = :producerId
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
    fun totalSpentByYearFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Transaction
    suspend fun fullItems(
        producerId: Long,
        count: Int,
        offset: Int
    ): List<FullItem> {
        val producer = get(producerId) ?: return emptyList()

        val itemEntities = itemsByProducer(
            producerId,
            count,
            offset
        )

        if (itemEntities.isEmpty()) return emptyList()

        return itemEntities.map { entity ->
            val transactionEntity = transactionEntityByItemEntityId(entity.id)
            val product = productById(entity.productId)
            val variant = entity.variantId?.let { variantById(it) }
            val category = categoryById(product.categoryId)
            val shop = transactionEntity.shopId?.let { shopById(it) }

            FullItem(
                id = entity.id,
                quantity = entity.quantity,
                price = entity.price,
                product = product,
                variant = variant,
                category = category,
                producer = producer,
                date = transactionEntity.date,
                shop = shop,
            )
        }
    }

    @Query("SELECT productproducer.* FROM productproducer ORDER BY productproducer.id DESC")
    fun allFlow(): Flow<List<ProductProducer>>

    @Query("SELECT COUNT(*) FROM productproducer")
    suspend fun totalCount(): Int

    @Query("SELECT productproducer.* FROM productproducer ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<ProductProducer>
}
