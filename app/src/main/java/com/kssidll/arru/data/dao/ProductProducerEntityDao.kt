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
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductProducerEntityDao {
    // Create

    @Insert
    suspend fun insert(producer: ProductProducerEntity): Long

    // Update

    @Update
    suspend fun update(producer: ProductProducerEntity)

    // Delete

    @Delete
    suspend fun delete(producer: ProductProducerEntity)

    // Helper

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.id = :shopId")
    suspend fun shopById(shopId: Long): ShopEntity

    @Query("SELECT * FROM product WHERE product.id = :productId")
    suspend fun productById(productId: Long): Product

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariantEntity

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
        JOIN ProductProducerEntity ON ProductProducerEntity.id = product.producerId
        WHERE ProductProducerEntity.id = :producerId
    """
    )
    suspend fun getProducts(producerId: Long): List<Product>

    @Query(
        """
        SELECT ProductVariantEntity.*
        FROM ProductVariantEntity
        JOIN product ON product.id = ProductVariantEntity.productId
        JOIN ProductProducerEntity ON ProductProducerEntity.id = product.producerId
        WHERE ProductProducerEntity.id = :producerId
    """
    )
    suspend fun getProductsVariants(producerId: Long): List<ProductVariantEntity>

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN product ON product.id = ItemEntity.productId
        JOIN ProductProducerEntity ON ProductProducerEntity.id = product.producerId
        WHERE ProductProducerEntity.id = :producerId
    """
    )
    suspend fun getItems(producerId: Long): List<ItemEntity>

    @Delete
    suspend fun deleteProducts(products: List<Product>)

    @Delete
    suspend fun deleteProductVariants(entities: List<ProductVariantEntity>)

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

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.id = :producerId")
    suspend fun get(producerId: Long): ProductProducerEntity?

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.id = :producerId")
    fun getFlow(producerId: Long): Flow<ProductProducerEntity?>

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.name = :name")
    suspend fun byName(name: String): ProductProducerEntity?

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
            val shop = transactionEntity.shopEntityId?.let { shopById(it) }

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

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity ORDER BY ProductProducerEntity.id DESC")
    fun allFlow(): Flow<List<ProductProducerEntity>>

    @Query("SELECT COUNT(*) FROM ProductProducerEntity")
    suspend fun totalCount(): Int

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<ProductProducerEntity>
}
