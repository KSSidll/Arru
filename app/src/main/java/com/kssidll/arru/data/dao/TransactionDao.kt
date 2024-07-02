package com.kssidll.arru.data.dao

import androidx.room.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.data.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface TransactionDao {
    // Create

    @Insert
    suspend fun insert(transactionEntity: TransactionEntity): Long

    // Update

    @Update
    suspend fun update(transactionEntity: TransactionEntity)

    // Delete

    @Delete
    suspend fun delete(transactionEntity: TransactionEntity)

    // Helper

    @Query("SELECT * FROM shop WHERE shop.id = :shopId")
    suspend fun shopById(shopId: Long): Shop?

    @Query("SELECT * FROM ItemEntity WHERE ItemEntity.id = :itemId")
    suspend fun itemById(itemId: Long): ItemEntity?

    @Query("SELECT * FROM product WHERE product.id = :productId")
    suspend fun productById(productId: Long): Product?

    @Query("SELECT * FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun variantById(variantId: Long): ProductVariant?

    @Query("SELECT * FROM productcategory WHERE productcategory.id = :categoryId")
    suspend fun categoryById(categoryId: Long): ProductCategory?

    @Query("SELECT * FROM productproducer WHERE productproducer.id = :producerId")
    suspend fun producerById(producerId: Long): ProductProducer?

    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE transactionId = :transactionId ORDER BY id DESC")
    suspend fun itemsByTransactionEntityId(transactionId: Long): List<ItemEntity>

    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE transactionId = :transactionId ORDER BY id DESC")
    fun itemsByTransactionEntityIdFlow(transactionId: Long): Flow<List<ItemEntity>>

    @androidx.room.Transaction
    suspend fun fullItemsByTransactionEntityId(transactionEntityId: Long): List<Item> {
        val transactionEntity = get(transactionEntityId) ?: return emptyList()

        val items = itemsByTransactionEntityId(transactionEntityId)

        if (items.isEmpty()) return emptyList()

        return items.map { item ->
            val product = productById(item.productId)!!
            val variant = item.variantId?.let { variantById(it) }
            val category = categoryById(product.categoryId)!!
            val producer = product.producerId?.let { producerById(it) }
            val shop = transactionEntity.shopId?.let { shopById(it) }

            Item(
                id = item.id,
                quantity = item.quantity,
                price = item.price,
                product = product,
                variant = variant,
                category = category,
                producer = producer,
                date = transactionEntity.date,
                shop = shop,
            )
        }
    }

    fun fullItemsByTransactionEntityIdFlow(transactionEntityId: Long): Flow<List<Item>> {
        val itemsFlow = itemsByTransactionEntityIdFlow(transactionEntityId)

        return itemsFlow.map { items ->
            val transactionEntity = get(transactionEntityId) ?: return@map emptyList()

            items.map { item ->
                val product = productById(item.productId)!!
                val variant = item.variantId?.let { variantById(it) }
                val category = categoryById(product.categoryId)!!
                val producer = product.producerId?.let { producerById(it) }
                val shop = transactionEntity.shopId?.let { shopById(it) }

                Item(
                    id = item.id,
                    quantity = item.quantity,
                    price = item.price,
                    product = product,
                    variant = variant,
                    category = category,
                    producer = producer,
                    date = transactionEntity.date,
                    shop = shop,
                )
            }
        }
    }

    @Delete
    suspend fun deleteItems(items: List<ItemEntity>)

    // Read

    @Query("SELECT * FROM TransactionEntity WHERE TransactionEntity.id = :transactionEntityId")
    suspend fun get(transactionEntityId: Long): TransactionEntity?

    @Query("SELECT * FROM TransactionEntity ORDER BY id DESC LIMIT 1")
    suspend fun newest(): TransactionEntity?

    @Query("SELECT COUNT(*) FROM TransactionEntity")
    suspend fun count(): Int

    @Query("SELECT COUNT(*) FROM TransactionEntity WHERE id < :transactionEntityId")
    suspend fun countBefore(transactionEntityId: Long): Int

    @Query("SELECT COUNT(*) FROM TransactionEntity WHERE id > :transactionEntityId")
    suspend fun countAfter(transactionEntityId: Long): Int

    @Query("SELECT * FROM TransactionEntity WHERE TransactionEntity.id = :transactionEntityId")
    fun getFlow(transactionEntityId: Long): Flow<TransactionEntity?>

    @Query("SELECT SUM(TransactionEntity.totalCost) FROM TransactionEntity")
    fun totalSpent(): Long?

    @Query("SELECT SUM(TransactionEntity.totalCost) FROM TransactionEntity")
    fun totalSpentFlow(): Flow<Long?>

    @Query(
        """
        WITH date_series AS (
            SELECT MIN(TransactionEntity.date) AS start_date,
                   MAX(TransactionEntity.date) AS end_date
            FROM TransactionEntity
            UNION ALL
            SELECT (start_date + 86400000) AS start_date, end_date
            FROM date_series
            WHERE date_series.end_date > date_series.start_date
        ), items AS (
            SELECT (TransactionEntity.date / 86400000) AS transaction_time, SUM(TransactionEntity.totalCost) AS item_total
            FROM TransactionEntity
            GROUP BY transaction_time
        )
        SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(item_total, 0) AS total
        FROM date_series
        LEFT JOIN items ON (date_series.start_date / 86400000) = transaction_time
        WHERE time IS NOT NULL
        GROUP BY time
        ORDER BY time
    """
    )
    fun totalSpentByDayFlow(): Flow<List<TransactionSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT (((MIN(TransactionEntity.date) / 86400000) - ((MIN(TransactionEntity.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
                 (MAX(TransactionEntity.date) - 604800000) AS end_date
        FROM TransactionEntity
        UNION ALL
        SELECT (start_date + 604800000) AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date >= date_series.start_date
    ), items AS (
        SELECT ((TransactionEntity.date - 345600000) / 604800000) AS items_time, SUM(TransactionEntity.totalCost) AS item_total
        FROM TransactionEntity
        GROUP BY items_time
    )
    SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(item_total, 0) AS total
    FROM date_series
    LEFT JOIN items ON (date_series.start_date / 604800000) = items_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByWeekFlow(): Flow<List<TransactionSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
               DATE(MAX(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS end_date
        FROM TransactionEntity
        UNION ALL
        SELECT DATE(start_date, '+1 month') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS items_time, SUM(TransactionEntity.totalCost) AS item_total
        FROM TransactionEntity
        GROUP BY items_time
    )
    SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(item_total, 0) AS total
    FROM date_series
    LEFT JOIN items ON STRFTIME('%Y-%m', date_series.start_date) = items_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByMonthFlow(): Flow<List<TransactionSpentByTime>>

    @Query(
        """
        WITH date_series AS (
        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS start_date,
               DATE(MAX(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS end_date
        FROM TransactionEntity
        UNION ALL
        SELECT DATE(start_date, '+1 year') AS start_date, end_date
        FROM date_series
        WHERE date_series.end_date > date_series.start_date
    ), items AS (
        SELECT STRFTIME('%Y', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS items_time, SUM(TransactionEntity.totalCost) AS item_total
        FROM TransactionEntity
        GROUP BY items_time
    )
    SELECT STRFTIME('%Y', date_series.start_date) AS time, COALESCE(item_total, 0) AS total
    FROM date_series
    LEFT JOIN items ON STRFTIME('%Y', date_series.start_date) = items_time
    WHERE time IS NOT NULL
    GROUP BY time
    ORDER BY time
    """
    )
    fun totalSpentByYearFlow(): Flow<List<TransactionSpentByTime>>

    @Query("SELECT TransactionEntity.* FROM TransactionEntity ORDER BY date DESC LIMIT :count OFFSET :startPosition")
    suspend fun partDateDesc(
        startPosition: Int,
        count: Int
    ): List<TransactionEntity>

    suspend fun transactionEntitiesWithItems(
        startPosition: Int,
        count: Int
    ): List<Transaction> {
        return partDateDesc(
            startPosition,
            count
        ).map { basket ->
            val shop = basket.shopId?.let { shopById(it) }
            val items = fullItemsByTransactionEntityId(basket.id)

            Transaction(
                id = basket.id,
                date = basket.date,
                shop = shop,
                totalCost = basket.totalCost,
                items = items,
            )
        }
    }

    fun transactionEntityWithItems(transactionEntityId: Long): Flow<Transaction?> {
        return getFlow(transactionEntityId).map { basket ->
            if (basket != null) {
                val shop = basket.shopId?.let { shopById(it) }
                val items = fullItemsByTransactionEntityId(basket.id)

                return@map Transaction(
                    id = basket.id,
                    date = basket.date,
                    shop = shop,
                    totalCost = basket.totalCost,
                    items = items,
                )
            } else return@map null
        }
    }
}