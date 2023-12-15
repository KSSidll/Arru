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

    @Query("SELECT item.* FROM item LEFT JOIN product ON item.productId = product.id WHERE categoryId = :categoryId")
    suspend fun allByCategoryId(categoryId: Long): List<Item>

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
    @Query("SELECT * FROM item WHERE productId = :productId ORDER BY date DESC LIMIT :count OFFSET :offset")
    suspend fun getEmbeddedItemsByProductSorted(
        offset: Int,
        count: Int,
        productId: Long,
    ): List<EmbeddedItem>

    @Transaction
    @Query("SELECT * FROM item WHERE productId = :productId ORDER BY date DESC LIMIT :count OFFSET :offset")
    fun getEmbeddedItemsByProductSortedFlow(
        offset: Int,
        count: Int,
        productId: Long,
    ): Flow<List<EmbeddedItem>>

    @Transaction
    @Query("SELECT * FROM item INNER JOIN product ON product.id = item.productId WHERE product.producerId = :producerId ORDER BY date DESC LIMIT :count OFFSET :offset")
    suspend fun getEmbeddedItemsByProducerSorted(
        offset: Int,
        count: Int,
        producerId: Long,
    ): List<EmbeddedItem>

    @Transaction
    @Query("SELECT * FROM item INNER JOIN product ON product.id = item.productId WHERE product.producerId = :producerId ORDER BY date DESC LIMIT :count OFFSET :offset")
    fun getEmbeddedItemsByProducerSortedFlow(
        offset: Int,
        count: Int,
        producerId: Long,
    ): Flow<List<EmbeddedItem>>

    @Transaction
    @Query("SELECT * FROM item INNER JOIN product ON product.id = item.productId WHERE product.categoryId = :categoryId ORDER BY date DESC LIMIT :count OFFSET :offset")
    suspend fun getEmbeddedItemsByCategorySorted(
        offset: Int,
        count: Int,
        categoryId: Long,
    ): List<EmbeddedItem>

    @Transaction
    @Query("SELECT * FROM item INNER JOIN product ON product.id = item.productId WHERE product.categoryId = :categoryId ORDER BY date DESC LIMIT :count OFFSET :offset")
    fun getEmbeddedItemsByCategorySortedFlow(
        offset: Int,
        count: Int,
        categoryId: Long,
    ): Flow<List<EmbeddedItem>>

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
    @Query("SELECT * FROM product WHERE id = :productId")
    suspend fun getItemEmbeddedProduct(productId: Long): EmbeddedProduct

    @Transaction
    @Query("SELECT * FROM product WHERE id = :productId")
    fun getItemEmbeddedProductFlow(productId: Long): Flow<EmbeddedProduct>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    WHERE productId = :productId
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT product.*, AVG(item.price) AS price, shop.name AS shopName, productvariant.name as variantName, STRFTIME('%Y-%m', date_series.start_date) AS time
FROM date_series
LEFT JOIN item ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch'))
    AND item.productId = :productId
LEFT JOIN shop ON item.shopId = shop.id
LEFT JOIN productvariant ON item.variantId = productvariant.id
LEFT JOIN product ON item.productId = product.id
WHERE time IS NOT NULL
GROUP BY time, shopId, variantId
ORDER BY time
    """
    )
    fun getProductsAveragePriceByVariantByShopByMonthSortedFlow(productId: Long): Flow<List<ProductPriceByShopByTime>>

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

    suspend fun getFullItemsByProduct(
        offset: Int,
        count: Int,
        productId: Long,
    ): List<FullItem> {
        val items = getEmbeddedItemsByProductSorted(
            offset = offset,
            count = count,
            productId = productId,
        )
        return items.map {
            FullItem(
                embeddedItem = it,
                embeddedProduct = getItemEmbeddedProduct(it.product.id),
            )
        }
    }

    fun getFullItemsByProductFlow(
        offset: Int,
        count: Int,
        productId: Long,
    ): Flow<List<FullItem>> {
        val items = getEmbeddedItemsByProductSortedFlow(
            offset = offset,
            count = count,
            productId = productId,
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

    suspend fun getFullItemsByProducer(
        offset: Int,
        count: Int,
        producerId: Long,
    ): List<FullItem> {
        val items = getEmbeddedItemsByProducerSorted(
            offset = offset,
            count = count,
            producerId = producerId,
        )
        return items.map {
            FullItem(
                embeddedItem = it,
                embeddedProduct = getItemEmbeddedProduct(it.product.id),
            )
        }
    }

    fun getFullItemsByProducerFlow(
        offset: Int,
        count: Int,
        producerId: Long,
    ): Flow<List<FullItem>> {
        val items = getEmbeddedItemsByProducerSortedFlow(
            offset = offset,
            count = count,
            producerId = producerId,
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

    suspend fun getFullItemsByCategory(
        offset: Int,
        count: Int,
        categoryId: Long,
    ): List<FullItem> {
        val items = getEmbeddedItemsByCategorySorted(
            offset = offset,
            count = count,
            categoryId = categoryId,
        )
        return items.map {
            FullItem(
                embeddedItem = it,
                embeddedProduct = getItemEmbeddedProduct(it.product.id),
            )
        }
    }

    fun getFullItemsByCategoryFlow(
        offset: Int,
        count: Int,
        categoryId: Long,
    ): Flow<List<FullItem>> {
        val items = getEmbeddedItemsByCategorySortedFlow(
            offset = offset,
            count = count,
            categoryId = categoryId,
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

    @Query("SELECT shop.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN shop ON item.shopId = shop.id GROUP BY item.shopId")
    suspend fun getShopTotalSpent(): List<ItemSpentByShop>

    @Query("SELECT shop.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN shop ON item.shopId = shop.id GROUP BY item.shopId")
    fun getShopTotalSpentFlow(): Flow<List<ItemSpentByShop>>

    @Query("SELECT shop.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN shop ON item.shopId = shop.id WHERE STRFTIME('%Y-%m', DATE(date / 1000, 'unixepoch')) = :date GROUP BY item.shopId")
    fun getShopTotalSpentFlowByMonth(date: String): Flow<List<ItemSpentByShop>>

    @Query("SELECT productcategory.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN product ON item.productId = product.id INNER JOIN productcategory ON product.categoryId = productcategory.id GROUP BY productcategory.id")
    suspend fun getCategoryTotalSpent(): List<ItemSpentByCategory>

    @Query("SELECT productcategory.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN product ON item.productId = product.id INNER JOIN productcategory ON product.categoryId = productcategory.id GROUP BY productcategory.id")
    fun getCategoryTotalSpentFlow(): Flow<List<ItemSpentByCategory>>

    @Query("SELECT productcategory.*, SUM(item.price * item.quantity) as total FROM item INNER JOIN product ON item.productId = product.id INNER JOIN productcategory ON product.categoryId = productcategory.id WHERE STRFTIME('%Y-%m', DATE(date / 1000, 'unixepoch')) = :date GROUP BY productcategory.id")
    fun getCategoryTotalSpentFlowByMonth(date: String): Flow<List<ItemSpentByCategory>>

    @Query("SELECT SUM(price * quantity) AS total FROM item")
    suspend fun getTotalSpent(): Long

    @Query("SELECT SUM(price * quantity) AS total FROM item")
    fun getTotalSpentFlow(): Flow<Long>

    @Query("SELECT SUM(price * quantity) AS total FROM item WHERE shopId = :shopId")
    suspend fun getTotalSpentByShop(shopId: Long): Long

    @Query("SELECT SUM(price * quantity) AS total FROM item WHERE shopId = :shopId")
    fun getTotalSpentByShopFlow(shopId: Long): Flow<Long>

    @Query("SELECT SUM(price * quantity) AS total FROM item WHERE productId = :productId")
    suspend fun getTotalSpentByProduct(productId: Long): Long

    @Query("SELECT SUM(price * quantity) AS total FROM item WHERE productId = :productId")
    fun getTotalSpentByProductFlow(productId: Long): Flow<Long>

    @Query("SELECT SUM(price * quantity) AS total FROM item INNER JOIN product ON product.id = item.productId WHERE producerId = :producerId")
    suspend fun getTotalSpentByProducer(producerId: Long): Long

    @Query("SELECT SUM(price * quantity) AS total FROM item INNER JOIN product ON product.id = item.productId WHERE producerId = :producerId")
    fun getTotalSpentByProducerFlow(producerId: Long): Flow<Long>

    @Query("SELECT SUM(price * quantity) AS total FROM item INNER JOIN product ON product.id = item.productId WHERE categoryId = :categoryId")
    suspend fun getTotalSpentByCategory(categoryId: Long): Long

    @Query("SELECT SUM(price * quantity) AS total FROM item INNER JOIN product ON product.id = item.productId WHERE categoryId = :categoryId")
    fun getTotalSpentByCategoryFlow(categoryId: Long): Flow<Long>

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
WHERE time IS NOT NULL
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
WHERE time IS NOT NULL
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
    WHERE item.productId = :productId
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 86400000) = ((item.date) / 86400000)
    AND item.productId = :productId
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
"""
    )
    suspend fun getTotalSpentByProductByDay(productId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT MIN(item.date) AS start_date,
           MAX(item.date) AS end_date
    FROM item
    WHERE item.productId = :productId
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 86400000) = ((item.date) / 86400000)
    AND item.productId = :productId
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
"""
    )
    fun getTotalSpentByProductByDayFlow(productId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT MIN(item.date) AS start_date,
           MAX(item.date) AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 86400000) = ((item.date) / 86400000)
LEFT JOIN product ON product.id = item.productId
WHERE producerId = :producerId
    AND time IS NOT NULL
GROUP BY time
ORDER BY time
"""
    )
    suspend fun getTotalSpentByProducerByDay(producerId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT MIN(item.date) AS start_date,
           MAX(item.date) AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT (date / 86400000) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
    GROUP BY items_time
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(item_total, 0) AS total
FROM date_series
LEFT JOIN items ON (date_series.start_date / 86400000) = items_time
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
"""
    )
    fun getTotalSpentByProducerByDayFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT MIN(item.date) AS start_date,
           MAX(item.date) AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT (date / 86400000) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    GROUP BY items_time
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(item_total, 0) AS total
FROM date_series
LEFT JOIN items ON (date_series.start_date / 86400000) = items_time
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
"""
    )
    suspend fun getTotalSpentByCategoryByDay(categoryId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT MIN(item.date) AS start_date,
           MAX(item.date) AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    UNION ALL
    SELECT (start_date + 86400000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT (date / 86400000) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    GROUP BY items_time
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(item_total, 0) AS total
FROM date_series
LEFT JOIN items ON (date_series.start_date / 86400000) = items_time
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
"""
    )
    fun getTotalSpentByCategoryByDayFlow(categoryId: Long): Flow<List<ItemSpentByTime>>

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
WHERE time IS NOT NULL
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
WHERE time IS NOT NULL
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
    WHERE date_series.end_date >= date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
    AND item.shopId = :shopId
WHERE time IS NOT NULL
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
    WHERE date_series.end_date >= date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
    AND item.shopId = :shopId
WHERE time IS NOT NULL
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
    WHERE productId = :productId
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date >= date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
    AND item.productId = :productId
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByProductByWeek(productId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    WHERE productId = :productId
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date >= date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
    AND item.productId = :productId
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByProductByWeekFlow(productId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date >= date_series.start_date
), items AS (
    SELECT ((date - 345600000) / 604800000) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
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
    suspend fun getTotalSpentByProducerByWeek(producerId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date >= date_series.start_date
), items AS (
    SELECT ((date - 345600000) / 604800000) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
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
    fun getTotalSpentByProducerByWeekFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date >= date_series.start_date
), items AS (
    SELECT ((date - 345600000) / 604800000) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
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
    suspend fun getTotalSpentByCategoryByWeek(categoryId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date >= date_series.start_date
), items AS (
    SELECT ((date - 345600000) / 604800000) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
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
    fun getTotalSpentByCategoryByWeekFlow(categoryId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT (((MIN(item.date) / 86400000) - ((MIN(item.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
             (MAX(item.date) - 604800000) AS end_date
    FROM item
    UNION ALL
    SELECT (start_date + 604800000) AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date >= date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
WHERE time IS NOT NULL
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
    WHERE date_series.end_date >= date_series.start_date
)
SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON (date_series.start_date / 604800000) = ((item.date - 345600000) / 604800000)
WHERE time IS NOT NULL
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
WHERE time IS NOT NULL
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
WHERE time IS NOT NULL
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
    WHERE productId = :productId
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch'))
    AND item.productId = :productId
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByProductByMonth(productId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    WHERE productId = :productId
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch'))
    AND item.productId = :productId
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByProductByMonthFlow(productId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
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
    suspend fun getTotalSpentByProducerByMonth(producerId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
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
    fun getTotalSpentByProducerByMonthFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
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
    suspend fun getTotalSpentByCategoryByMonth(categoryId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of month') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of month') AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    UNION ALL
    SELECT DATE(start_date, '+1 month') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT STRFTIME('%Y-%m', DATE(item.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
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
    fun getTotalSpentByCategoryByMonthFlow(categoryId: Long): Flow<List<ItemSpentByTime>>

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
WHERE time IS NOT NULL
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
WHERE time IS NOT NULL
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
WHERE time IS NOT NULL
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
WHERE time IS NOT NULL
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
    WHERE productId = :productId
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y', date_series.start_date) as time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y', date_series.start_date) = STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch'))
    AND item.productId = :productId
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
    """
    )
    suspend fun getTotalSpentByProductByYear(productId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of year') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of year') AS end_date
    FROM item
    WHERE productId = :productId
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
)
SELECT STRFTIME('%Y', date_series.start_date) as time, COALESCE(SUM(item.price * item.quantity), 0) AS total
FROM date_series
LEFT JOIN item ON STRFTIME('%Y', date_series.start_date) = STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch'))
    AND item.productId = :productId
WHERE time IS NOT NULL
GROUP BY time
ORDER BY time
    """
    )
    fun getTotalSpentByProductByYearFlow(productId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of year') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of year') AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
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
    suspend fun getTotalSpentByProducerByYear(producerId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of year') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of year') AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND producerId = :producerId
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
    fun getTotalSpentByProducerByYearFlow(producerId: Long): Flow<List<ItemSpentByTime>>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of year') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of year') AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
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
    suspend fun getTotalSpentByCategoryByYear(categoryId: Long): List<ItemSpentByTime>

    @Query(
        """
WITH date_series AS (
    SELECT DATE(MIN(item.date) / 1000, 'unixepoch', 'start of year') AS start_date,
           DATE(MAX(item.date) / 1000, 'unixepoch', 'start of year') AS end_date
    FROM item
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
    UNION ALL
    SELECT DATE(start_date, '+1 year') AS start_date, end_date
    FROM date_series
    WHERE date_series.end_date > date_series.start_date
), items AS (
    SELECT STRFTIME('%Y', DATE(item.date / 1000, 'unixepoch')) AS items_time, SUM(item.price * item.quantity) AS item_total
    FROM item 
    INNER JOIN product ON product.id = item.productId
        AND categoryId = :categoryId
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
    fun getTotalSpentByCategoryByYearFlow(categoryId: Long): Flow<List<ItemSpentByTime>>

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
WHERE time IS NOT NULL
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
WHERE time IS NOT NULL
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
    suspend fun getByVariantId(variantId: Long): List<Item>

    @Query("SELECT * FROM item WHERE variantId == :variantId")
    fun getByVariantIdFlow(variantId: Long): Flow<List<Item>>

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

    @Update
    suspend fun update(items: List<Item>)

    @Delete
    suspend fun delete(item: Item)

    @Delete
    suspend fun delete(items: List<Item>)
}