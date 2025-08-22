package com.kssidll.arru.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kssidll.arru.data.data.IntermediateTransaction
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionEntityDao {
    // Create

    @Insert suspend fun insert(entity: TransactionEntity): Long

    // Update

    @Update suspend fun update(entity: TransactionEntity)

    // Delete

    @Delete suspend fun delete(entity: TransactionEntity)

    // Helper

    @Query("SELECT ShopEntity.* FROM ShopEntity WHERE ShopEntity.id = :shopId")
    suspend fun shopById(shopId: Long): ShopEntity?

    @Query(
        "SELECT ItemEntity.* FROM ItemEntity WHERE transactionEntityId = :transactionBasketId ORDER BY id DESC"
    )
    suspend fun itemsByTransactionBasketId(transactionBasketId: Long): List<ItemEntity>

    @Delete suspend fun deleteItems(entities: List<ItemEntity>)

    // Read

    @Query("SELECT * FROM TransactionEntity WHERE TransactionEntity.id = :id")
    fun get(id: Long): Flow<TransactionEntity?>

    @Query("SELECT SUM(TransactionEntity.totalCost) FROM TransactionEntity")
    fun totalSpent(): Flow<Long?>

    @Transaction
    @Query(
        "SELECT TransactionEntity.* FROM TransactionEntity WHERE TransactionEntity.id = :id ORDER BY date DESC"
    )
    fun intermediateFor(id: Long): Flow<IntermediateTransaction>

    @Transaction
    @Query("SELECT TransactionEntity.* FROM TransactionEntity ORDER BY date DESC")
    fun intermediates(): PagingSource<Int, IntermediateTransaction>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT DATE(day, '+1 day') AS day, end_date
            FROM date_series
            WHERE date_series.day < date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
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
    fun totalSpentByDay(): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'weekday 1') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT DATE(day, '+7 days') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+7 days') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'weekday 1') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
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
    fun totalSpentByWeek(): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of month') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT DATE(day, '+1 month') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 month') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of month') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
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
    fun totalSpentByMonth(): Flow<List<TransactionSpentChartData>>

    @Query(
        """
        WITH date_series AS (
            SELECT 
                DATE(MIN(TransactionEntity.date / 1000), 'unixepoch', 'start of year') AS day,
                DATE(current_timestamp, 'localtime') AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT DATE(day, '+1 year') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 year') <= date_series.end_date
        ), spent_by_day AS (
            SELECT DATE(TransactionEntity.date / 1000, 'unixepoch', 'start of year') AS day, SUM(TransactionEntity.totalCost) AS spent
            FROM TransactionEntity
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
    fun totalSpentByYear(): Flow<List<TransactionSpentChartData>>

    @Query("SELECT * FROM TransactionEntity ORDER BY id DESC LIMIT 1")
    fun newest(): Flow<TransactionEntity?>
}
