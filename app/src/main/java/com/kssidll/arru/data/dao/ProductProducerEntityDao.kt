package com.kssidll.arru.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductProducerEntityDao {
    // Create

    @Insert suspend fun insert(entity: ProductProducerEntity): Long

    // Update

    @Update suspend fun update(entity: ProductProducerEntity)

    // Delete

    @Delete suspend fun delete(entity: ProductProducerEntity)

    // Helper

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

    @Delete suspend fun deleteProducts(entities: List<ProductEntity>)

    @Delete suspend fun deleteProductVariants(entities: List<ProductVariantEntity>)

    @Delete suspend fun deleteItems(entities: List<ItemEntity>)

    @Update suspend fun updateProducts(entities: List<ProductEntity>)

    // Read

    @Query(
        "SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.id = :id"
    )
    fun get(id: Long): Flow<ProductProducerEntity?>

    @Query("SELECT ProductProducerEntity.* FROM ProductProducerEntity")
    fun all(): Flow<List<ProductProducerEntity>>

    @Query(
        """
        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
        FROM ItemEntity
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        WHERE ProductEntity.productProducerEntityId = :id
    """
    )
    fun totalSpent(id: Long): Flow<Long?>

    @Query(
        "SELECT ItemView.* FROM ItemView WHERE ItemView.productProducerId = :id ORDER BY date DESC"
    )
    fun itemsFor(id: Long): PagingSource<Int, Item>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity 
                ON ProductEntity.id = ItemEntity.productEntityId
                AND ProductEntity.productProducerEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 day') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch') AS day, SUM(ItemEntity.price * ItemEntity.quantity) AS spent
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity 
                ON ProductEntity.id = ItemEntity.productEntityId
                AND ProductEntity.productProducerEntityId = :id
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
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByDay(id: Long): Flow<List<ItemSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'weekday 1') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity 
                ON ProductEntity.id = ItemEntity.productEntityId
                AND ProductEntity.productProducerEntityId = :id
            UNION ALL
            SELECT DATE(day, '+7 days') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+7 days') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'weekday 1') AS day, SUM(ItemEntity.price * ItemEntity.quantity) AS spent
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity 
                ON ProductEntity.id = ItemEntity.productEntityId
                AND ProductEntity.productProducerEntityId = :id
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
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByWeek(id: Long): Flow<List<ItemSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of month') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity 
                ON ProductEntity.id = ItemEntity.productEntityId
                AND ProductEntity.productProducerEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 month') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 month') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of month') AS day, SUM(ItemEntity.price * ItemEntity.quantity) AS spent
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity 
                ON ProductEntity.id = ItemEntity.productEntityId
                AND ProductEntity.productProducerEntityId = :id
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
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByMonth(id: Long): Flow<List<ItemSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of year') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity 
                ON ProductEntity.id = ItemEntity.productEntityId
                AND ProductEntity.productProducerEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 year') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 year') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of year') AS day, SUM(ItemEntity.price * ItemEntity.quantity) AS spent
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            INNER JOIN ProductEntity 
                ON ProductEntity.id = ItemEntity.productEntityId
                AND ProductEntity.productProducerEntityId = :id
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
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByYear(id: Long): Flow<List<ItemSpentChartData>>

    @Query(
        "SELECT ProductProducerEntity.* FROM ProductProducerEntity WHERE ProductProducerEntity.name = :name"
    )
    suspend fun byName(name: String): ProductProducerEntity?
}
