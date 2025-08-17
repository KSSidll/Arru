package com.kssidll.arru.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductCategoryEntityDao {
    // Create

    @Insert
    suspend fun insert(entity: ProductCategoryEntity): Long

    // Update

    @Update
    suspend fun update(entity: ProductCategoryEntity)

    @Update
    suspend fun update(entities: List<ProductCategoryEntity>)

    // Delete

    @Delete
    suspend fun delete(entity: ProductCategoryEntity)

    // Helper

    @Query(
        """
        SELECT ProductEntity.*
        FROM ProductEntity
        JOIN ProductCategoryEntity ON ProductCategoryEntity.id = ProductEntity.productCategoryEntityId
        WHERE ProductCategoryEntity.id = :categoryId
    """
    )
    suspend fun getProducts(categoryId: Long): List<ProductEntity>

    @Query(
        """
        SELECT ProductVariantEntity.*
        FROM ProductVariantEntity
        JOIN ProductEntity ON ProductEntity.id = ProductVariantEntity.productEntityId
        JOIN ProductCategoryEntity ON ProductCategoryEntity.id = ProductEntity.productCategoryEntityId
        WHERE ProductCategoryEntity.id = :categoryId
    """
    )
    suspend fun getProductsVariants(categoryId: Long): List<ProductVariantEntity>

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        JOIN ProductCategoryEntity ON ProductCategoryEntity.id = ProductEntity.productCategoryEntityId
        WHERE ProductCategoryEntity.id = :categoryId
    """
    )
    suspend fun getItems(categoryId: Long): List<ItemEntity>

    @Delete
    suspend fun deleteProducts(entities: List<ProductEntity>)

    @Delete
    suspend fun deleteProductVariants(entities: List<ProductVariantEntity>)

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    @Update
    suspend fun updateProducts(entities: List<ProductEntity>)

    // Read

    @Query("SELECT ProductCategoryEntity.* FROM ProductCategoryEntity WHERE ProductCategoryEntity.id = :id")
    fun get(id: Long): Flow<ProductCategoryEntity?>

    @Query(
        """
        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
        FROM ItemEntity
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        JOIN ProductCategoryEntity ON ProductCategoryEntity.id = ProductEntity.productCategoryEntityId
        WHERE ProductCategoryEntity.id = :id
    """
    )
    fun totalSpent(id: Long): Flow<Long?>

    @Query("SELECT ItemView.* FROM ItemView WHERE ItemView.productCategoryId = :id")
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
                AND ProductEntity.productCategoryEntityId = :id
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
                AND ProductEntity.productCategoryEntityId = :id
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
                AND ProductEntity.productCategoryEntityId = :id
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
                AND ProductEntity.productCategoryEntityId = :id
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
                AND ProductEntity.productCategoryEntityId = :id
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
                AND ProductEntity.productCategoryEntityId = :id
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
                AND ProductEntity.productCategoryEntityId = :id
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
                AND ProductEntity.productCategoryEntityId = :id
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
    fun totalSpentByYear(id: Long): Flow<List<ItemSpentChartData>>








    @Query("SELECT ProductCategoryEntity.* FROM ProductCategoryEntity ORDER BY ProductCategoryEntity.id DESC")
    fun all(): Flow<List<ProductCategoryEntity>>

    @Query("SELECT ProductCategoryEntity.* FROM ProductCategoryEntity WHERE ProductCategoryEntity.name = :name")
    fun byName(name: String): Flow<ProductCategoryEntity?>

    @Query(
        """
        SELECT ProductCategoryEntity.*, SUM(ItemEntity.price * ItemEntity.quantity) as total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.TransactionEntityId
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        JOIN ProductCategoryEntity ON ProductCategoryEntity.id = ProductEntity.productCategoryEntityId
        GROUP BY ProductCategoryEntity.id
    """
    )
    fun totalSpentByCategory(): Flow<List<ItemSpentByCategory>>

    @Query(
        """
        SELECT ProductCategoryEntity.*, SUM(ItemEntity.price * ItemEntity.quantity) as total
        FROM ItemEntity
        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.TransactionEntityId
        INNER JOIN ProductEntity ON ItemEntity.productEntityId = ProductEntity.id
        INNER JOIN ProductCategoryEntity ON ProductEntity.productCategoryEntityId = ProductCategoryEntity.id
        WHERE STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) = :date
        GROUP BY ProductCategoryEntity.id
    """
    )
    fun totalSpentByCategoryByMonth(date: String): Flow<List<ItemSpentByCategory>>
}