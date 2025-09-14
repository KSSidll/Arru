package com.kssidll.arru.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TotalSpentByShop
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopEntityDao {
    // Create

    @Insert suspend fun insert(entity: ShopEntity): Long

    // Update

    @Update suspend fun update(entity: ShopEntity)

    // Delete

    @Delete suspend fun delete(entity: ShopEntity)

    // Read

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.id = :id")
    fun get(id: Long): Flow<ShopEntity?>

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.name = :name")
    fun byName(name: String): Flow<ShopEntity?>

    @Query("SELECT ShopEntity.* FROM ShopEntity") fun all(): Flow<List<ShopEntity>>

    @Query(
        """
        SELECT SUM(TransactionEntity.totalCost)
        FROM TransactionEntity
        WHERE TransactionEntity.shopEntityId = :id
    """
    )
    fun totalSpent(id: Long): Flow<Long?>

    @Query("SELECT ItemView.* FROM ItemView WHERE ItemView.shopId = :id ORDER BY date DESC")
    fun itemsFor(id: Long): PagingSource<Int, Item>

    @Query(
        """
        WITH date_series AS (
            SELECT
                1 AS data_order,
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            UNION ALL
            SELECT data_order + 1, DATE(day, '+1 day') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
                data_order,
                date_series.day AS date,
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByDay(id: Long): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT
                1 AS data_order,
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'weekday 1') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            UNION ALL
            SELECT data_order + 1, DATE(day, '+7 days') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+7 days') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'weekday 1') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
                data_order,
                date_series.day AS date,
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByWeek(id: Long): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT
                1 AS data_order,
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of month') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            UNION ALL
            SELECT data_order + 1, DATE(day, '+1 month') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 month') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of month') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
                data_order,
                STRFTIME('%Y-%m', date_series.day) AS date,
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByMonth(id: Long): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT
                1 AS data_order,
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of year') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            UNION ALL
            SELECT data_order + 1, DATE(day, '+1 year') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 year') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of year') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
            WHERE TransactionEntity.shopEntityId = :id
            GROUP BY day
        ), full_spent_by_day AS (
            SELECT
                data_order,
                STRFTIME('%Y', date_series.day) AS date,
                COALESCE(spent_by_day.spent, 0) AS spent
            FROM date_series
            LEFT JOIN spent_by_day ON date_series.day = spent_by_day.day
            WHERE date_series.day IS NOT NULL
        ), full_spent_by_day_row AS (
            SELECT 
                data_order,
                date,
                spent AS value
            FROM full_spent_by_day
        )
        SELECT * FROM full_spent_by_day_row
        ORDER BY data_order ASC
    """
    )
    fun totalSpentByYear(id: Long): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        SELECT ShopEntity.*, SUM(TransactionEntity.totalCost) as total
        FROM TransactionEntity
        JOIN ShopEntity ON ShopEntity.id = TransactionEntity.shopEntityId
        GROUP BY ShopEntity.id
    """
    )
    fun totalSpentByShop(): Flow<List<TotalSpentByShop>>

    @Query(
        """
        SELECT ShopEntity.*, SUM(TransactionEntity.totalCost) as total
        FROM TransactionEntity
        JOIN ShopEntity ON ShopEntity.id = TransactionEntity.shopEntityId
        WHERE STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) = :date
        GROUP BY ShopEntity.id
    """
    )
    fun totalSpentByShopByMonth(date: String): Flow<List<TotalSpentByShop>>
}
