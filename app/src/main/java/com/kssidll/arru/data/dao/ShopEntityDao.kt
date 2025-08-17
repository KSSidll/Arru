package com.kssidll.arru.data.dao

import androidx.paging.PagingSource
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
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.id = :id")
    fun get(id: Long): Flow<ShopEntity?>

    @Query(
        """
        SELECT SUM(TransactionEntity.totalCost)
        FROM TransactionEntity
        WHERE TransactionEntity.shopEntityId = :id
    """
    )
    fun totalSpent(id: Long): Flow<Long?>

    @Query("SELECT ItemView.* FROM ItemView WHERE ItemView.shopId = :id")
    fun itemsFor(id: Long): PagingSource<Int, Item>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 day') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
                date_series.day AS date, 
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                ROW_NUMBER() OVER (ORDER BY date ASC) data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY date ASC
    """
    )
    fun totalSpentByDay(id: Long): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'weekday 1') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            UNION ALL
            SELECT DATE(day, '+7 days') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+7 days') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'weekday 1') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
                date_series.day AS date, 
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                ROW_NUMBER() OVER (ORDER BY date ASC) data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY date ASC
    """
    )
    fun totalSpentByWeek(id: Long): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of month') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 month') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 month') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of month') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
            STRFTIME('%Y-%m', date_series.day) AS date, 
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                ROW_NUMBER() OVER (ORDER BY date ASC) data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY date ASC
    """
    )
    fun totalSpentByMonth(id: Long): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of year') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 year') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 year') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of year') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
            STRFTIME('%Y', date_series.day) AS date, 
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                ROW_NUMBER() OVER (ORDER BY date ASC) data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY date ASC
    """
    )
    fun totalSpentByYear(id: Long): Flow<List<TransactionSpentChartData>>










    @Query("SELECT ShopEntity.* FROM ShopEntity ORDER BY ShopEntity.id DESC")
    fun all(): Flow<List<ShopEntity>>

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.name = :name")
    fun byName(name: String): Flow<ShopEntity?>

    @Transaction
    suspend fun fullItems(
        entityId: Long,
        count: Int,
        offset: Int
    ): List<FullItem> {
        val shop = get(entityId).first() ?: return emptyList()

        val itemEntities = itemsByShop(
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
            val productProducerEntity = productEntity.productProducerEntityId?.let { producerById(it) }

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
    fun totalSpentByShop(): Flow<List<TransactionTotalSpentByShop>>

    @Query(
        """
        SELECT ShopEntity.*, SUM(TransactionEntity.totalCost) as total
        FROM TransactionEntity
        JOIN ShopEntity ON ShopEntity.id = TransactionEntity.shopEntityId
        WHERE STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) = :date
        GROUP BY ShopEntity.id
    """
    )
    fun totalSpentByShopByMonth(date: String): Flow<List<TransactionTotalSpentByShop>>
}