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
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductPriceByShopByTime
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductEntityDao {
    // Create

    @Insert
    suspend fun insert(entity: ProductEntity): Long

    // Update

    @Update
    suspend fun update(entity: ProductEntity)

    // Delete

    @Delete
    suspend fun delete(entity: ProductEntity)

    // Helper

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.id = :shopId")
    suspend fun shopById(shopId: Long): ShopEntity

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.id = :producerId")
    suspend fun producerById(producerId: Long): ProductProducerEntity?

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariantEntity?

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.productId = :productId AND ProductVariantEntity.name = :variantName")
    suspend fun variantByName(
        productId: Long,
        variantName: String
    ): ProductVariantEntity?

    @Query("SELECT ProductCategoryEntity.* FROM ProductCategoryEntity WHERE ProductCategoryEntity.id = :categoryId")
    suspend fun categoryById(categoryId: Long): ProductCategoryEntity?

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
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
        WHERE ProductEntity.id = :productId
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

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.productId = :productId")
    suspend fun variants(productId: Long): List<ProductVariantEntity>

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
        WHERE ProductEntity.id = :productId
    """
    )
    suspend fun getItems(productId: Long): List<ItemEntity>

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    @Delete
    suspend fun deleteVariants(variants: List<ProductVariantEntity>)

    @Update
    suspend fun updateVariants(variants: List<ProductVariantEntity>)

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

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.id = :productId")
    suspend fun get(productId: Long): ProductEntity?

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.id = :productId")
    fun getFlow(productId: Long): Flow<ProductEntity?>

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.name = :name")
    suspend fun byName(name: String): ProductEntity?

    @Query(
        """
        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
        FROM ItemEntity
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
        WHERE ProductEntity.id = :productId
    """
    )
    fun totalSpentFlow(productId: Long): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(TransactionEntity.date) AS start_date,
                   UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000 AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
                AND productId = :productId
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), ItemEntities AS (
            SELECT (TransactionEntity.date / 86400000) AS transaction_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
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
        SELECT (((MIN(TransactionEntity.date) / 86400000) - ((MIN(TransactionEntity.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 ((UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000) - 604800000) AS end_date
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
              AND productId = :productId
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), ItemEntities AS (
        SELECT ((TransactionEntity.date - 345600000) / 604800000) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
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
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of month') AS end_date
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
            AND productId = :productId
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
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
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of year') AS end_date
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
            AND productId = :productId
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productId
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
            val transactionEntity = transactionEntityByItemEntityId(entity.id)
            val variant = entity.variantId?.let { variantById(it) }
            val category = categoryById(product.categoryId)!!
            val producer = product.producerId?.let { producerById(it) }
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

    @Query("SELECT ItemEntity.* FROM ProductEntity JOIN ItemEntity ON ItemEntity.productId = ProductEntity.id WHERE ProductEntity.id = :productId ORDER BY ItemEntity.id DESC LIMIT 1")
    suspend fun newestItem(productId: Long): ItemEntity?

    @Query(
        """
        WITH date_series AS (
            SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
                   DATE(current_timestamp, 'localtime', 'start of month') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE productId = :productId
            UNION ALL
            SELECT DATE(start_date, '+1 month') AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        )
        SELECT ProductEntity.*, AVG(ItemEntity.price) AS price, ShopEntity.name AS shopName, ProductVariantEntity.name as variantName, ProductProducerEntity.name as producerName, STRFTIME('%Y-%m', date_series.start_date) AS time
        FROM date_series
        LEFT JOIN TransactionEntity ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch'))
        JOIN ItemEntity ON ItemEntity.transactionEntityId = TransactionEntity.id
            AND ItemEntity.productId = :productId
        LEFT JOIN ShopEntity ON TransactionEntity.shopEntityId = ShopEntity.id
        LEFT JOIN ProductVariantEntity ON ItemEntity.variantId = ProductVariantEntity.id
        LEFT JOIN ProductEntity ON ItemEntity.productId = ProductEntity.id
        LEFT JOIN ProductProducerEntity ON ProductEntity.producerId = ProductProducerEntity.id
        WHERE time IS NOT NULL
        GROUP BY time, shopEntityId, variantId, producerId
        ORDER BY time
    """
    )
    fun averagePriceByVariantByShopByMonthFlow(productId: Long): Flow<List<ProductPriceByShopByTime>>

    @Query("SELECT ProductEntity.* FROM ProductEntity ORDER BY ProductEntity.id DESC")
    fun allFlow(): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM ProductEntity")
    suspend fun totalCount(): Int

    @Query("SELECT ProductEntity.* FROM ProductEntity ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<ProductEntity>
}