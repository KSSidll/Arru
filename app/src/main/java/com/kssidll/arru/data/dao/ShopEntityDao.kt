package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.data.TransactionTotalSpentByTime
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopEntityDao {
    // Create

    @Insert
    suspend fun insert(entity: ShopEntity): Long

    // Update

    @Update
    suspend fun update(entity: ShopEntity)

    // Delete

    @Delete
    suspend fun delete(entity: ShopEntity)

    // Helper

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.id = :productId")
    suspend fun productById(productId: Long): ProductEntity

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.id = :producerId")
    suspend fun producerById(producerId: Long): ProductProducerEntity

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
        WHERE TransactionEntity.shopEntityId = :entityId
        ORDER BY date DESC
        LIMIT :count
        OFFSET :offset
    """
    )
    suspend fun itemsByShop(
        entityId: Long,
        count: Int,
        offset: Int
    ): List<ItemEntity>

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        WHERE shopEntityId = :entityId
    """
    )
    suspend fun getItems(entityId: Long): List<ItemEntity>

    @Query("SELECT TransactionEntity.* FROM TransactionEntity WHERE TransactionEntity.shopEntityId = :entityId")
    suspend fun getTransactionBaskets(entityId: Long): List<TransactionEntity>

    @Update
    suspend fun updateTransactionBaskets(baskets: List<TransactionEntity>)

    @Delete
    suspend fun deleteTransactionBaskets(baskets: List<TransactionEntity>)

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    @Query(
        """
        SELECT COUNT(*)
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        WHERE ItemEntity.id < :itemEntityId AND TransactionEntity.shopEntityId = :entityId
    """
    )
    suspend fun countItemsBefore(
        itemEntityId: Long,
        entityId: Long
    ): Int

    @Query(
        """
        SELECT COUNT(*)
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
        WHERE ItemEntity.id > :itemEntityId AND TransactionEntity.shopEntityId = :entityId
    """
    )
    suspend fun countItemsAfter(
        itemEntityId: Long,
        entityId: Long
    ): Int

    // Read

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.id = :entityId")
    suspend fun get(entityId: Long): ShopEntity?

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.id = :entityId")
    fun getFlow(entityId: Long): Flow<ShopEntity?>

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.name = :name")
    suspend fun byName(name: String): ShopEntity?

    @Query(
        """
        SELECT SUM(TransactionEntity.totalCost)
        FROM TransactionEntity
        WHERE TransactionEntity.shopEntityId = :entityId
    """
    )
    fun totalSpentFlow(entityId: Long): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(TransactionEntity.date) AS start_date,
                   UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000 AS end_date
            FROM TransactionEntity
                WHERE TransactionEntity.shopEntityId = :entityId
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
                WHERE date_series.end_date > date_series.start_date
        ), ItemEntities AS (
            SELECT (TransactionEntity.date / 86400000) AS transaction_time, SUM(TransactionEntity.totalCost) AS transaction_total
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :entityId
            GROUP BY transaction_time
        )
        SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(transaction_total, 0) AS total
        FROM date_series
        LEFT JOIN ItemEntities ON (date_series.start_date / 86400000) = transaction_time
        WHERE time IS NOT NULL
        GROUP BY time
        ORDER BY time
    """
    )
    fun totalSpentByDayFlow(entityId: Long): Flow<List<TransactionTotalSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT (((MIN(TransactionEntity.date) / 86400000) - ((MIN(TransactionEntity.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 ((UNIXEPOCH(DATE(current_timestamp, 'localtime')) * 1000) - 604800000) AS end_date
        FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :entityId
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
            WHERE date_series.end_date >= date_series.start_date
    ), ItemEntities AS (
        SELECT ((TransactionEntity.date - 345600000) / 604800000) AS transaction_time, SUM(TransactionEntity.totalCost) AS transaction_total
        FROM TransactionEntity
        JOIN ItemEntity ON ItemEntity.transactionEntityId = TransactionEntity.id
            AND TransactionEntity.shopEntityId = :entityId
        GROUP BY transaction_time
    )
    SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(transaction_total, 0) AS total
    FROM date_series
    LEFT JOIN ItemEntities ON (date_series.start_date / 604800000) = transaction_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByWeekFlow(entityId: Long): Flow<List<TransactionTotalSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of month') AS end_date
        FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :entityId
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
            WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS transaction_time, SUM(TransactionEntity.totalCost) AS transaction_total
        FROM TransactionEntity
        WHERE TransactionEntity.shopEntityId = :entityId
        GROUP BY transaction_time
    )
    SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(transaction_total, 0) AS total
    FROM date_series
    LEFT JOIN ItemEntities ON STRFTIME('%Y-%m', date_series.start_date) = transaction_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByMonthFlow(entityId: Long): Flow<List<TransactionTotalSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(current_timestamp, 'localtime', 'start of year') AS end_date
        FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :entityId
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
            WHERE date_series.end_date > date_series.start_date
    ), ItemEntities AS (
        SELECT STRFTIME('%Y', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS transaction_time, SUM(TransactionEntity.totalCost) AS transaction_total
        FROM TransactionEntity
        WHERE TransactionEntity.shopEntityId = :entityId
        GROUP BY transaction_time
    )
    SELECT STRFTIME('%Y', date_series.start_date) AS time, COALESCE(transaction_total, 0) AS total
    FROM date_series
    LEFT JOIN ItemEntities ON STRFTIME('%Y', date_series.start_date) = transaction_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByYearFlow(entityId: Long): Flow<List<TransactionTotalSpentByTime>>

    @Transaction
    suspend fun fullItems(
        entityId: Long,
        count: Int,
        offset: Int
    ): List<FullItem> {
        val shop = get(entityId) ?: return emptyList()

        val itemEntities = itemsByShop(
            entityId,
            count,
            offset
        )

        if (itemEntities.isEmpty()) return emptyList()

        return itemEntities.map { entity ->
            val transactionEntity = transactionEntityByItemEntityId(entity.id)
            val productEntity = productById(entity.productEntityId)
            val productVariantEntity = entity.variantEntityId?.let { variantById(it) }
            val productCategoryEntity = categoryById(productEntity.categoryEntityId)
            val productProducerEntity = productEntity.producerEntityId?.let { producerById(it) }

            FullItem(
                id = entity.id,
                quantity = entity.quantity,
                price = entity.price,
                product = productEntity,
                variant = productVariantEntity,
                category = productCategoryEntity,
                producer = productProducerEntity,
                date = transactionEntity.date,
                shop = shop,
            )
        }
    }

    @Query(
        """
        SELECT ShopEntity.*, SUM(TransactionEntity.totalCost) as total
        FROM TransactionEntity
        JOIN ShopEntity ON ShopEntity.id = TransactionEntity.shopEntityId
        GROUP BY ShopEntity.id
    """
    )
    fun totalSpentByShopFlow(): Flow<List<TransactionTotalSpentByShop>>

    @Query(
        """
        SELECT ShopEntity.*, SUM(TransactionEntity.totalCost) as total
        FROM TransactionEntity
        JOIN ShopEntity ON ShopEntity.id = TransactionEntity.shopEntityId
        WHERE STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) = :date
        GROUP BY ShopEntity.id
    """
    )
    fun totalSpentByShopByMonthFlow(date: String): Flow<List<TransactionTotalSpentByShop>>

    @Query("SELECT ShopEntity.* FROM ShopEntity ORDER BY ShopEntity.id DESC")
    fun allFlow(): Flow<List<ShopEntity>>

    @Query("SELECT COUNT(*) FROM ShopEntity")
    suspend fun totalCount(): Int

    @Query("SELECT ShopEntity.* FROM ShopEntity ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<ShopEntity>
}