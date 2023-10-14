package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface ItemDao {
    @Query("SELECT * FROM item ORDER BY id ASC")
    suspend fun getAll(): List<Item>

    @Query("SELECT * FROM item ORDER BY id ASC")
    fun getAllFlow(): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE id == :id")
    suspend fun get(id: Long): Item?

    @Query("SELECT * FROM item WHERE id == :id")
    fun getFlow(id: Long): Flow<Item>

    @Query("SELECT * FROM item ORDER BY id DESC LIMIT 1")
    suspend fun getLast(): Item?

    @Query("SELECT * FROM item ORDER BY id DESC LIMIT 1")
    fun getLastFlow(): Flow<Item>

    @Transaction
    @Query("SELECT * FROM item ORDER BY date DESC")
    suspend fun getAllEmbeddedItemSorted(): List<EmbeddedItem>

    @Transaction
    @Query("SELECT * FROM item ORDER BY date DESC")
    fun getAllEmbeddedItemSortedFlow(): Flow<List<EmbeddedItem>>

    @Transaction
    @Query("SELECT * FROM item ORDER BY date DESC LIMIT :count OFFSET :offset")
    suspend fun getEmbeddedItemsSorted(
        offset: Int,
        count: Int
    ): List<EmbeddedItem>

    @Transaction
    @Query("SELECT * FROM item ORDER BY date DESC LIMIT :count OFFSET :offset")
    fun getEmbeddedItemsSortedFlow(
        offset: Int,
        count: Int
    ): Flow<List<EmbeddedItem>>

    @Transaction
    @Query("SELECT * FROM item WHERE shopId = :shopId ORDER BY date DESC LIMIT :count OFFSET :offset")
    suspend fun getEmbeddedItemsByShopSorted(
        offset: Int,
        count: Int,
        shopId: Long,
    ): List<EmbeddedItem>

    @Transaction
    @Query("SELECT * FROM item WHERE shopId = :shopId ORDER BY date DESC LIMIT :count OFFSET :offset")
    fun getEmbeddedItemsByShopSortedFlow(
        offset: Int,
        count: Int,
        shopId: Long,
    ): Flow<List<EmbeddedItem>>

    @Transaction
    @Query("SELECT * FROM product WHERE id = :productId")
    suspend fun getItemEmbeddedProduct(productId: Long): EmbeddedProduct

    @Transaction
    @Query("SELECT * FROM product WHERE id = :productId")
    fun getItemEmbeddedProductFlow(productId: Long): Flow<EmbeddedProduct>

    suspend fun getFullItems(
        offset: Int,
        count: Int
    ): List<FullItem> {
        val items = getEmbeddedItemsSorted(
            offset = offset,
            count = count,
        )
        return items.map {
            FullItem(
                embeddedItem = it,
                embeddedProduct = getItemEmbeddedProduct(it.product.id),
            )
        }
    }

    fun getFullItemsFlow(
        offset: Int,
        count: Int
    ): Flow<List<FullItem>> {
        val items = getEmbeddedItemsSortedFlow(
            offset = offset,
            count = count,
        )
        return items.map {
            it.map { item ->
                FullItem(
                    embeddedItem = item,
                    embeddedProduct = getItemEmbeddedProduct(item.product.id),
                )
            }
        }
    }

    suspend fun getFullItemsByShop(
        offset: Int,
        count: Int,
        shopId: Long,
    ): List<FullItem> {
        val items = getEmbeddedItemsByShopSorted(
            offset = offset,
            count = count,
            shopId = shopId,
        )
        return items.map {
            FullItem(
                embeddedItem = it,
                embeddedProduct = getItemEmbeddedProduct(it.product.id),
            )
        }
    }

    fun getFullItemsByShopFlow(
        offset: Int,
        count: Int,
        shopId: Long,
    ): Flow<List<FullItem>> {
        val items = getEmbeddedItemsByShopSortedFlow(
            offset = offset,
            count = count,
            shopId = shopId,
        )
        return items.map {
            it.map { item ->
                FullItem(
                    embeddedItem = item,
                    embeddedProduct = getItemEmbeddedProduct(item.product.id),
                )
            }
        }
    }

    @Query("SELECT shop.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN shop ON item.shopId = shop.id GROUP BY item.shopId")
    suspend fun getShopTotalSpent(): List<ItemSpentByShop>

    @Query("SELECT shop.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN shop ON item.shopId = shop.id GROUP BY item.shopId")
    fun getShopTotalSpentFlow(): Flow<List<ItemSpentByShop>>

    @Query("SELECT productcategory.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN product ON item.productId = product.id INNER JOIN productcategory ON product.categoryId = productcategory.id GROUP BY productcategory.id")
    suspend fun getCategoryTotalSpent(): List<ItemSpentByCategory>

    @Query("SELECT productcategory.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN product ON item.productId = product.id INNER JOIN productcategory ON product.categoryId = productcategory.id GROUP BY productcategory.id")
    fun getCategoryTotalSpentFlow(): Flow<List<ItemSpentByCategory>>

    @Query("SELECT SUM(price * quantity) AS total FROM item")
    suspend fun getTotalSpent(): Long

    @Query("SELECT SUM(price * quantity) AS total FROM item")
    fun getTotalSpentFlow(): Flow<Long>

    @Query("SELECT SUM(price * quantity) AS total FROM item WHERE shopId = :shopId")
    suspend fun getTotalSpentByShop(shopId: Long): Long

    @Query("SELECT SUM(price * quantity) AS total FROM item WHERE shopId = :shopId")
    fun getTotalSpentByShopFlow(shopId: Long): Flow<Long>

    @Query(
        """
WITH date_series AS (
    SELECT MIN(item.date) AS start_date,
           MAX(item.date) AS end_date
    FROM item
    WHERE item.shopId = :shopId
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 86400000) = ((item.date) / 86400000)
    AND item.shopId = :shopId
GROUP BY time
ORDER BY time
"""
    )
    suspend fun getTotalSpentByShopByDay(shopId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT MIN(item.date) AS start_date,
           MAX(item.date) AS end_date
    FROM item
    WHERE item.shopId = :shopId
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 86400000) = ((item.date) / 86400000)
    AND item.shopId = :shopId
GROUP BY time
ORDER BY time
"""
    )
    fun getTotalSpentByShopByDayFlow(shopId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT MIN(item.date) AS start_date,
           MAX(item.date) AS end_date
    FROM item
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 86400000) = ((item.date) / 86400000)
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByDay(): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT MIN(item.date) AS start_date,
           MAX(item.date) AS end_date
    FROM item
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 86400000) = ((item.date) / 86400000)
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByDayFlow(): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    WHERE shopId = :shopId
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
    AND item.shopId = :shopId
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByShopByWeek(shopId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    WHERE shopId = :shopId
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
    AND item.shopId = :shopId
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByShopByWeekFlow(shopId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByWeek(): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByWeekFlow(): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    WHERE shopId = :shopId
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch'))
    AND item.shopId = :shopId
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByShopByMonth(shopId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    WHERE shopId = :shopId
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch'))
    AND item.shopId = :shopId
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByShopByMonthFlow(shopId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch'))
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByMonth(): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch'))
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByMonthFlow(): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of year') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of year') AS end_date
    FROM item
    WHERE shopId = :shopId
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y', date_series.start_date) as time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y', date_series.start_date) = STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch'))
    AND item.shopId = :shopId
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByShopByYear(shopId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of year') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of year') AS end_date
    FROM item
    WHERE shopId = :shopId
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y', date_series.start_date) as time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y', date_series.start_date) = STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch'))
    AND item.shopId = :shopId
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByShopByYearFlow(shopId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of year') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of year') AS end_date
    FROM item
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y', date_series.start_date) as time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y', date_series.start_date) = STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch'))
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByYear(): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of year') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of year') AS end_date
    FROM item
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y', date_series.start_date) as time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y', date_series.start_date) = STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch'))
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByYearFlow(): Flow<List<ItemSpentByTime>>

    @Query("SELECT * FROM item WHERE productId == :productId")
    suspend fun getByProductId(productId: Long): List<Item>

    @Query("SELECT * FROM item WHERE productId == :productId")
    fun getByProductIdFlow(productId: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE productId == :productId ORDER BY id DESC LIMIT 1")
    suspend fun getLastByProductId(productId: Long): Item?

    @Query("SELECT * FROM item WHERE productId == :productId ORDER BY id DESC LIMIT 1")
    fun getLastByProductIdFlow(productId: Long): Flow<Item?>

    @Query("SELECT * FROM item WHERE variantId == :variantId")
    suspend fun getByVariant(variantId: Long): List<Item>

    @Query("SELECT * FROM item WHERE variantId == :variantId")
    fun getByVariantFlow(variantId: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE shopId == :shopId")
    suspend fun getByShopId(shopId: Long): List<Item>

    @Query("SELECT * FROM item WHERE shopId == :shopId")
    fun getByShopIdFlow(shopId: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE date > :date")
    suspend fun getNewerThan(date: Long): List<Item>

    @Query("SELECT * FROM item WHERE date > :date")
    fun getNewerThanFlow(date: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE date < :date")
    suspend fun getOlderThan(date: Long): List<Item>

    @Query("SELECT * FROM item WHERE date < :date")
    fun getOlderThanFlow(date: Long): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE date > :lowerBoundDate AND date < :higherBoundDate")
    suspend fun getBetweenDates(
        lowerBoundDate: Long,
        higherBoundDate: Long
    ): List<Item>

    @Query("SELECT * FROM item WHERE date > :lowerBoundDate AND date < :higherBoundDate")
    fun getBetweenDatesFlow(
        lowerBoundDate: Long,
        higherBoundDate: Long
    ): Flow<List<Item>>

    @Insert
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)
}