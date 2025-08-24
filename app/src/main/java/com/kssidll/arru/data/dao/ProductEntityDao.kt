package com.kssidll.arru.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.data.ProductPriceByShopByVariantByProducerByTime
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductEntityDao {
    // Create

    @Insert suspend fun insert(entity: ProductEntity): Long

    // Update

    @Update suspend fun update(entity: ProductEntity)

    // Delete

    @Delete suspend fun delete(entity: ProductEntity)

    @Delete suspend fun delete(entity: List<ProductEntity>)

    // Read

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.id = :id")
    fun get(id: Long): Flow<ProductEntity?>

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.name = :name")
    fun byName(name: String): Flow<ProductEntity?>

    @Query("SELECT ProductEntity.* FROM ProductEntity") fun all(): Flow<List<ProductEntity>>

    @Query(
        """
        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
        FROM ItemEntity
        WHERE ItemEntity.productEntityId = :id
    """
    )
    fun totalSpent(id: Long): Flow<Long?>

    @Query("SELECT ItemView.* FROM ItemView WHERE ItemView.productId = :id ORDER BY date DESC")
    fun itemsFor(id: Long): PagingSource<Int, Item>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE ItemEntity.productEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 day') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch') AS day, SUM(ItemEntity.price * ItemEntity.quantity) AS spent
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE ItemEntity.productEntityId = :id
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
                DATE(current_timestamp, 'localtime', 'weekday 1') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE ItemEntity.productEntityId = :id
            UNION ALL
            SELECT DATE(day, '+7 days') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'weekday 1') AS day, SUM(ItemEntity.price * ItemEntity.quantity) AS spent
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE ItemEntity.productEntityId = :id
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
                DATE(current_timestamp, 'localtime', 'start of month') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE ItemEntity.productEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 month') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of month') AS day, SUM(ItemEntity.price * ItemEntity.quantity) AS spent
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE ItemEntity.productEntityId = :id
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
                DATE(current_timestamp, 'localtime', 'start of year') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE ItemEntity.productEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 year') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of year') AS day, SUM(ItemEntity.price * ItemEntity.quantity) AS spent
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE ItemEntity.productEntityId = :id
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
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE ItemEntity.productEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 day') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), date_series_row AS (
            SELECT 
                ROW_NUMBER() OVER (ORDER BY day ASC) data_order,
                day
            FROM date_series
        ), spent_by_day AS (
            SELECT 
                DATE(TransactionEntity.date / 1000, 'unixepoch') AS day,
                AVG(ItemEntity.price) AS spent,
                ShopEntity.name AS shopName,
                ProductVariantEntity.name AS productVariantName,
                ProductProducerEntity.name AS productProducerName
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            LEFT JOIN ShopEntity ON TransactionEntity.shopEntityId = ShopEntity.id
            LEFT JOIN ProductVariantEntity ON ItemEntity.productVariantEntityId = ProductVariantEntity.id
            LEFT JOIN ProductEntity ON ItemEntity.productEntityId = ProductEntity.id
            LEFT JOIN ProductProducerEntity ON ProductEntity.productProducerEntityId = ProductProducerEntity.id
            WHERE ItemEntity.productEntityId = :id
            GROUP BY day, shopName, productVariantName, productProducerName
        ), full_spent_by_day AS (
            SELECT
                date_series_row.data_order,
                date_series_row.day AS date, 
                spent_by_day.spent AS value,
                shopName,
                productVariantName,
                productProducerName
            FROM date_series_row
            LEFT JOIN spent_by_day ON date_series_row.day = spent_by_day.day
            WHERE date_series_row.day IS NOT NULL
        )
        SELECT * FROM full_spent_by_day
        ORDER BY data_order ASC
    """
    )
    fun averagePriceByShopByVariantByProducerByDay(
        id: Long
    ): Flow<List<ProductPriceByShopByVariantByProducerByTime>>
}
