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
import com.kssidll.arru.data.data.ProductPriceByShopByTime
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

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

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.productEntityId = :productId AND ProductVariantEntity.name = :variantName")
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
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
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

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.productEntityId = :productId")
    suspend fun variants(productId: Long): List<ProductVariantEntity>

    @Query(
        """
        SELECT ItemEntity.*
        FROM ItemEntity
        JOIN ProductEntity ON ProductEntity.id = ItemEntity.productEntityId
        WHERE ProductEntity.id = :productId
    """
    )
    suspend fun getItems(productId: Long): List<ItemEntity>

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    @Delete
    suspend fun deleteVariants(entities: List<ProductVariantEntity>)

    @Update
    suspend fun updateVariants(entities: List<ProductVariantEntity>)

    @Update
    suspend fun updateItems(entities: List<ItemEntity>)

    @Query(
        """
        SELECT COUNT(*)
        FROM ItemEntity
        WHERE ItemEntity.id < :itemEntityId AND ItemEntity.productEntityId = :productId
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
        WHERE ItemEntity.id > :itemEntityId AND ItemEntity.productEntityId = :productId
    """
    )
    suspend fun countItemsAfter(
        itemEntityId: Long,
        productId: Long
    ): Int

    // Read

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.id = :id")
    fun get(id: Long): Flow<ProductEntity?>

    @Query(
        """
        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
        FROM ItemEntity
        WHERE ItemEntity.productEntityId = :id
    """
    )
    fun totalSpent(id: Long): Flow<Long?>

    @Query("SELECT ItemView.* FROM ItemView WHERE ItemView.productId = :id")
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
            WHERE ItemEntity.productEntityId = :id
            UNION ALL
            SELECT DATE(day, '+7 days') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+7 days') <= date_series.end_date
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
            WHERE ItemEntity.productEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 month') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 month') <= date_series.end_date
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
            WHERE ItemEntity.productEntityId = :id
            UNION ALL
            SELECT DATE(day, '+1 year') AS day, end_date
            FROM date_series
            WHERE DATE(date_series.day, '+1 year') <= date_series.end_date
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
        ORDER BY date ASC
    """
    )
    fun totalSpentByYear(id: Long): Flow<List<ItemSpentChartData>>








    @Query("SELECT ProductEntity.* FROM ProductEntity ORDER BY ProductEntity.id DESC")
    fun all(): Flow<List<ProductEntity>>

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.name = :name")
    fun byName(name: String): Flow<ProductEntity?>

    @Transaction
    suspend fun fullItems(
        entityId: Long,
        count: Int,
        offset: Int
    ): List<FullItem> {
        val product = get(entityId).first() ?: return emptyList()

        val itemEntities = itemsByProduct(
            entityId,
            count,
            offset
        )

        if (itemEntities.isEmpty()) return emptyList()

        return itemEntities.map { entity ->
            val transactionEntity = transactionEntityByItemEntityId(entity.id)
            val variantEntity = entity.productVariantEntityId?.let { variantById(it) }
            val productCategoryEntity = categoryById(product.productCategoryEntityId)!!
            val productProducerEntity = product.productProducerEntityId?.let { producerById(it) }
            val shopEntity = transactionEntity.shopEntityId?.let { shopById(it) }

            FullItem(
                id = entity.id,
                quantity = entity.quantity,
                price = entity.price,
                product = product,
                variant = variantEntity,
                category = productCategoryEntity,
                producer = productProducerEntity,
                date = transactionEntity.date,
                shop = shopEntity,
            )
        }
    }

    @Query("SELECT ItemEntity.* FROM ProductEntity JOIN ItemEntity ON ItemEntity.productEntityId = ProductEntity.id WHERE ProductEntity.id = :entityId ORDER BY ItemEntity.id DESC LIMIT 1")
    suspend fun newestItem(entityId: Long): ItemEntity?

    @Query(
        """
        WITH date_series AS (
            SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
                   DATE(current_timestamp, 'localtime', 'start of month') AS end_date
            FROM ItemEntity
            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionEntityId
            WHERE productEntityId = :entityId
            UNION ALL
            SELECT DATE(start_date, '+1 month') AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        )
        SELECT ProductEntity.*, AVG(ItemEntity.price) AS price, ShopEntity.name AS shopName, ProductVariantEntity.name as variantName, ProductProducerEntity.name as producerName, STRFTIME('%Y-%m', date_series.start_date) AS time
        FROM date_series
        LEFT JOIN TransactionEntity ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch'))
        JOIN ItemEntity ON ItemEntity.transactionEntityId = TransactionEntity.id
            AND ItemEntity.productEntityId = :entityId
        LEFT JOIN ShopEntity ON TransactionEntity.shopEntityId = ShopEntity.id
        LEFT JOIN ProductVariantEntity ON ItemEntity.productVariantEntityId = ProductVariantEntity.id
        LEFT JOIN ProductEntity ON ItemEntity.productEntityId = ProductEntity.id
        LEFT JOIN ProductProducerEntity ON ProductEntity.productProducerEntityId = ProductProducerEntity.id
        WHERE time IS NOT NULL
        GROUP BY time, shopEntityId, productVariantEntityId, productProducerEntityId
        ORDER BY time
    """
    )
    fun averagePriceByVariantByShopByMonth(entityId: Long): Flow<List<ProductPriceByShopByTime>>
}