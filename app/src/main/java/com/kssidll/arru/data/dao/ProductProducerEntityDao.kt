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
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface ProductProducerEntityDao {
    // Create

    @Insert
    suspend fun insert(entity: ProductProducerEntity): Long

    // Update

    @Update
    suspend fun update(entity: ProductProducerEntity)

    // Delete

    @Delete
    suspend fun delete(entity: ProductProducerEntity)

    // Helper

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.id = :shopId")
    suspend fun shopById(shopId: Long): ShopEntity

    @Query("SELECT * FROM ProductEntity WHERE ProductEntity.id = :productId")
    suspend fun productById(productId: Long): ProductEntity

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariantEntity

    @Query("SELECT ProductCategoryEntity.* FROM ProductCategoryEntity WHERE ProductCategoryEntity.id = :categoryId")
    suspend fun categoryById(categoryId: Long): ProductCategoryEntity

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
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        WHERE ProductEntity.productProducerEntityId = :producerId
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
        SELECT ProductEntity.*
        FROM ProductEntity
        JOIN ProductProducerEntity ON ProductProducerEntity.id = ProductEntity.productProducerEntityId
        WHERE ProductProducerEntity.id = :producerId
    """
    )
    suspend fun getProducts(producerId: Long): List<ProductEntity>

    @Query(
        """
        SELECT ProductVariantEntity.*
        FROM ProductVariantEntity
        JOIN ProductEntity ON ProductEntity.id = ProductVariantEntity.productEntityId
        JOIN ProductProducerEntity ON ProductProducerEntity.id = ProductEntity.productProducerEntityId
        WHERE ProductProducerEntity.id = :producerId
    """
    )
    suspend fun getProductsVariants(producerId: Long): List<ProductVariantEntity>

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        JOIN ProductProducerEntity ON ProductProducerEntity.id = ProductEntity.productProducerEntityId
        WHERE ProductProducerEntity.id = :producerId
    """
    )
    suspend fun getItems(producerId: Long): List<ItemEntity>

    @Delete
    suspend fun deleteProducts(entities: List<ProductEntity>)

    @Delete
    suspend fun deleteProductVariants(entities: List<ProductVariantEntity>)

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    @Update
    suspend fun updateProducts(entities: List<ProductEntity>)

    @Query(
        """
        SELECT COUNT(*)
        FROM ItemEntity
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        WHERE ItemEntity.id < :itemEntityId AND ProductEntity.productProducerEntityId = :producerId
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
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        WHERE ItemEntity.id > :itemEntityId AND ProductEntity.productProducerEntityId = :producerId
    """
    )
    suspend fun countItemsAfter(
        itemEntityId: Long,
        producerId: Long
    ): Int

    // Read

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.id = :id")
    fun get(id: Long): Flow<ProductProducerEntity?>














    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity ORDER BY ProductProducerEntity.id DESC")
    fun all(): Flow<List<ProductProducerEntity>>

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.name = :name")
    suspend fun byName(name: String): ProductProducerEntity?

    @Query(
        """
        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
        FROM ItemEntity
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        WHERE ProductEntity.productProducerEntityId = :entityId
    """
    )
    fun totalSpent(entityId: Long): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(TransactionEntity.date) AS start_date,
                   STRFTIME('%s', DATE(current_timestamp, 'localtime')) * 1000 AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
                AND productProducerEntityId = :entityId
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), ItemEntities AS (
            SELECT (TransactionEntity.date / 86400000) AS transaction_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
                AND productProducerEntityId = :entityId
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
    fun totalSpentByDay(entityId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT (((MIN(TransactionEntity.date) / 86400000) - ((MIN(TransactionEntity.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 ((STRFTIME('%s', DATE(current_timestamp, 'localtime')) * 1000) - 604800000) AS end_date
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
            AND productProducerEntityId = :entityId
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), ItemEntities AS (
        SELECT ((TransactionEntity.date - 345600000) / 604800000) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
            AND productProducerEntityId = :entityId
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
    fun totalSpentByWeek(entityId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of month') AS end_date
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
            AND productProducerEntityId = :entityId
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
            AND productProducerEntityId = :entityId
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
    fun totalSpentByMonth(entityId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of year') AS end_date
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
            AND productProducerEntityId = :entityId
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS ItemEntities_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        INNER JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
            AND productProducerEntityId = :entityId
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
    fun totalSpentByYear(entityId: Long): Flow<List<ItemSpentByTime>>

    @Transaction
    suspend fun fullItems(
        entityId: Long,
        count: Int,
        offset: Int
    ): List<FullItem> {
        val producer = get(entityId).first() ?: return emptyList()

        val itemEntities = itemsByProducer(
            entityId,
            count,
            offset
        )

        if (itemEntities.isEmpty()) return emptyList()

        return itemEntities.map { entity ->
            val transactionEntity = transactionEntityByItemEntityId(entity.id)
            val productEntity = productById(entity.productEntityId)
            val productVariantEntity = entity.productVariantEntityId?.let { variantById(it) }
            val productCategoryEntity = categoryById(productEntity.productCategoryEntityId)
            val shopEntity = transactionEntity.shopEntityId?.let { shopById(it) }

            FullItem(
                id = entity.id,
                quantity = entity.quantity,
                price = entity.price,
                product = productEntity,
                variant = productVariantEntity,
                category = productCategoryEntity,
                producer = producer,
                date = transactionEntity.date,
                shop = shopEntity,
            )
        }
    }
}
