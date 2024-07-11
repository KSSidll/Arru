package com.kssidll.arru.data.dao

import androidx.room.Dao

@Dao
interface ProducerDao {
//    // Create
//
//    @Insert
//    suspend fun insert(producer: ProductProducer): Long
//
//    // Update
//
//    @Update
//    suspend fun update(producer: ProductProducer)
//
//    // Delete
//
//    @Delete
//    suspend fun delete(producer: ProductProducer)
//
//    // Helper
//
//    @Query("SELECT * FROM shop WHERE shop.id = :shopId")
//    suspend fun shopById(shopId: Long): Shop
//
//    @Query("SELECT * FROM product WHERE product.id = :productId")
//    suspend fun productById(productId: Long): Product
//
//    @Query("SELECT * FROM productvariant WHERE productvariant.id = :variantId")
//    suspend fun variantById(variantId: Long): ProductVariant
//
//    @Query("SELECT * FROM productcategory WHERE productcategory.id = :categoryId")
//    suspend fun categoryById(categoryId: Long): ProductCategory
//
//    @Query(
//        """
//        SELECT TransactionEntity.*
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        WHERE ItemEntity.id = :itemId
//    """
//    )
//    suspend fun transactionEntityByitemId(itemId: Long): TransactionEntity
//
//    @Query(
//        """
//        SELECT ItemEntity.*
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        JOIN product ON product.id = ItemEntity.productId
//        WHERE product.producerId = :producerId
//        ORDER BY date DESC
//        LIMIT :count
//        OFFSET :offset
//    """
//    )
//    suspend fun itemsByProducer(
//        producerId: Long,
//        count: Int,
//        offset: Int
//    ): List<ItemEntity>
//
//    @Query(
//        """
//        SELECT product.*
//        FROM product
//        JOIN productproducer ON productproducer.id = product.producerId
//        WHERE productproducer.id = :producerId
//    """
//    )
//    suspend fun getProducts(producerId: Long): List<Product>
//
//    @Query(
//        """
//        SELECT productvariant.*
//        FROM productvariant
//        JOIN product ON product.id = productvariant.productId
//        JOIN productproducer ON productproducer.id = product.producerId
//        WHERE productproducer.id = :producerId
//    """
//    )
//    suspend fun getProductsVariants(producerId: Long): List<ProductVariant>
//
//    @Query(
//        """
//        SELECT productaltname.*
//        FROM productaltname
//        JOIN product ON product.id = productaltname.productId
//        JOIN productproducer ON productproducer.id = product.producerId
//        WHERE productproducer.id = :producerId
//    """
//    )
//    suspend fun getProductsAltNames(producerId: Long): List<ProductAltName>
//
//    @Query(
//        """
//        SELECT ItemEntity.*
//        FROM ItemEntity
//        JOIN product ON product.id = ItemEntity.productId
//        JOIN productproducer ON productproducer.id = product.producerId
//        WHERE productproducer.id = :producerId
//    """
//    )
//    suspend fun getItems(producerId: Long): List<ItemEntity>
//
//    @Delete
//    suspend fun deleteProducts(products: List<Product>)
//
//    @Delete
//    suspend fun deleteProductVariants(productVariants: List<ProductVariant>)
//
//    @Delete
//    suspend fun deleteProductAltNames(productAltNames: List<ProductAltName>)
//
//    @Delete
//    suspend fun deleteItems(items: List<ItemEntity>)
//
//    @Update
//    suspend fun updateProducts(products: List<Product>)
//
//    @Query(
//        """
//        SELECT COUNT(*)
//        FROM ItemEntity
//        JOIN product ON product.id = ItemEntity.productId
//        WHERE ItemEntity.id < :itemId AND product.producerId = :producerId
//    """
//    )
//    suspend fun countItemsBefore(
//        itemId: Long,
//        producerId: Long
//    ): Int
//
//    @Query(
//        """
//        SELECT COUNT(*)
//        FROM ItemEntity
//        JOIN product ON product.id = ItemEntity.productId
//        WHERE ItemEntity.id > :itemId AND product.producerId = :producerId
//    """
//    )
//    suspend fun countItemsAfter(
//        itemId: Long,
//        producerId: Long
//    ): Int
//
//    // Read
//
//    @Query("SELECT productproducer.* FROM productproducer WHERE productproducer.id = :producerId")
//    suspend fun get(producerId: Long): ProductProducer?
//
//    @Query("SELECT productproducer.* FROM productproducer WHERE productproducer.id = :producerId")
//    fun getFlow(producerId: Long): Flow<ProductProducer?>
//
//    @Query("SELECT productproducer.* FROM productproducer WHERE productproducer.name = :name")
//    suspend fun byName(name: String): ProductProducer?
//
//    @Query(
//        """
//        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
//        FROM ItemEntity
//        JOIN product ON product.id = ItemEntity.productId
//        WHERE product.producerId = :producerId
//    """
//    )
//    fun totalSpentFlow(producerId: Long): Flow<Long?>
//
//    @Query(
//        """
//        WITH date_series AS (
//            SELECT MIN(TransactionEntity.date) AS start_date,
//                   MAX(TransactionEntity.date) AS end_date
//            FROM ItemEntity
//            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//            INNER JOIN product ON product.id = ItemEntity.productId
//                AND producerId = :producerId
//            UNION ALL
//            SELECT (start_date + 86400000) AS start_date, end_date
//            FROM date_series
//            WHERE date_series.end_date > date_series.start_date
//        ), items AS (
//            SELECT (TransactionEntity.date / 86400000) AS transaction_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
//            FROM ItemEntity
//            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//            INNER JOIN product ON product.id = ItemEntity.productId
//                AND producerId = :producerId
//            GROUP BY transaction_time
//        )
//        SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(ItemEntity_total, 0) AS total
//        FROM date_series
//        LEFT JOIN items ON (date_series.start_date / 86400000) = transaction_time
//        WHERE time IS NOT NULL
//        GROUP BY time
//        ORDER BY time
//    """
//    )
//    fun totalSpentByDayFlow(producerId: Long): Flow<List<ItemSpentByTime>>
//
//    @Query(
//        """
//        WITH date_series AS (
//        SELECT (((MIN(TransactionEntity.date) / 86400000) - ((MIN(TransactionEntity.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
//                 (MAX(TransactionEntity.date) - 604800000) AS end_date
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND producerId = :producerId
//        UNION ALL
//        SELECT (start_date + 604800000) AS start_date, end_date
//        FROM date_series
//        WHERE date_series.end_date >= date_series.start_date
//    ), items AS (
//        SELECT ((TransactionEntity.date - 345600000) / 604800000) AS items_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND producerId = :producerId
//        GROUP BY items_time
//    )
//    SELECT DATE(date_series.start_date / 1000, 'unixepoch') AS time, COALESCE(ItemEntity_total, 0) AS total
//    FROM date_series
//    LEFT JOIN items ON (date_series.start_date / 604800000) = items_time
//    WHERE time IS NOT NULL
//    GROUP BY time
//    ORDER BY time
//    """
//    )
//    fun totalSpentByWeekFlow(producerId: Long): Flow<List<ItemSpentByTime>>
//
//    @Query(
//        """
//        WITH date_series AS (
//        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
//               DATE(MAX(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS end_date
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND producerId = :producerId
//        UNION ALL
//        SELECT DATE(start_date, '+1 month') AS start_date, end_date
//        FROM date_series
//        WHERE date_series.end_date > date_series.start_date
//    ), items AS (
//        SELECT STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS items_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND producerId = :producerId
//        GROUP BY items_time
//    )
//    SELECT STRFTIME('%Y-%m', date_series.start_date) AS time, COALESCE(ItemEntity_total, 0) AS total
//    FROM date_series
//    LEFT JOIN items ON STRFTIME('%Y-%m', date_series.start_date) = items_time
//    WHERE time IS NOT NULL
//    GROUP BY time
//    ORDER BY time
//    """
//    )
//    fun totalSpentByMonthFlow(producerId: Long): Flow<List<ItemSpentByTime>>
//
//    @Query(
//        """
//        WITH date_series AS (
//        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS start_date,
//               DATE(MAX(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS end_date
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND producerId = :producerId
//        UNION ALL
//        SELECT DATE(start_date, '+1 year') AS start_date, end_date
//        FROM date_series
//        WHERE date_series.end_date > date_series.start_date
//    ), items AS (
//        SELECT STRFTIME('%Y', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS items_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND producerId = :producerId
//        GROUP BY items_time
//    )
//    SELECT STRFTIME('%Y', date_series.start_date) AS time, COALESCE(ItemEntity_total, 0) AS total
//    FROM date_series
//    LEFT JOIN items ON STRFTIME('%Y', date_series.start_date) = items_time
//    WHERE time IS NOT NULL
//    GROUP BY time
//    ORDER BY time
//    """
//    )
//    fun totalSpentByYearFlow(producerId: Long): Flow<List<ItemSpentByTime>>
//
//    @Transaction
//    suspend fun fullItems(
//        producerId: Long,
//        count: Int,
//        offset: Int
//    ): List<Item> {
//        val producer = get(producerId) ?: return emptyList()
//
//        val items = itemsByProducer(
//            producerId,
//            count,
//            offset
//        )
//
//        if (items.isEmpty()) return emptyList()
//
//        return items.map { itemEntity ->
//            val transactionEntity = transactionEntityByitemId(itemEntity.id)
//            val product = productById(itemEntity.productId)
//            val variant = itemEntity.variantId?.let { variantById(it) }
//            val category = categoryById(product.categoryId)
//            val shop = transactionEntity.shopId?.let { shopById(it) }
//
//            Item(
//                id = itemEntity.id,
//                quantity = itemEntity.quantity,
//                price = itemEntity.price,
//                product = product,
//                category = category,
//                producer = producer,
//                date = transactionEntity.date,
//                shop = shop,
//            )
//        }
//    }
//
//    @Query("SELECT productproducer.* FROM productproducer ORDER BY productproducer.id DESC")
//    fun allFlow(): Flow<List<ProductProducer>>
}
