package com.kssidll.arru.data.dao

import androidx.room.Dao

@Dao
interface ProductDao {
//    // Create
//
//    @Insert
//    suspend fun insert(product: Product): Long
//
//    @Insert
//    suspend fun insertAltName(alternativeName: ProductAltName): Long
//
//    // Update
//
//    @Update
//    suspend fun update(product: Product)
//
//    @Update
//    suspend fun updateAltName(alternativeName: ProductAltName)
//
//    // Delete
//
//    @Delete
//    suspend fun delete(product: Product)
//
//    @Delete
//    suspend fun deleteAltName(alternativeName: ProductAltName)
//
//    @Delete
//    suspend fun deleteAltName(alternativeNames: List<ProductAltName>)
//
//
//    // Helper
//
//    @Query("SELECT * FROM shop WHERE shop.id = :shopId")
//    suspend fun shopById(shopId: Long): Shop
//
//    @Query("SELECT * FROM productproducer WHERE productproducer.id = :producerId")
//    suspend fun producerById(producerId: Long): ProductProducer?
//
//    @Query("SELECT * FROM productvariant WHERE productvariant.id = :variantId")
//    suspend fun variantById(variantId: Long): ProductVariant?
//
//    @Query("SELECT * FROM productvariant WHERE productvariant.productId = :productId AND productvariant.name = :variantName")
//    suspend fun variantByName(
//        productId: Long,
//        variantName: String
//    ): ProductVariant?
//
//    @Query("SELECT * FROM productcategory WHERE productcategory.id = :categoryId")
//    suspend fun categoryById(categoryId: Long): ProductCategory?
//
//    @Query(
//        """
//        SELECT TransactionEntity.*
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        WHERE ItemEntity.id = :itemId
//    """
//    )
//    suspend fun transactionEntityByItemEntityId(itemId: Long): TransactionEntity
//
//    @Query(
//        """
//        SELECT ItemEntity.*
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        JOIN product ON product.id = ItemEntity.productId
//        WHERE product.id = :productId
//        ORDER BY date DESC
//        LIMIT :count
//        OFFSET :offset
//    """
//    )
//    suspend fun itemsByProduct(
//        productId: Long,
//        count: Int,
//        offset: Int
//    ): List<ItemEntity>
//
//    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.productId = :productId")
//    suspend fun variants(productId: Long): List<ProductVariant>
//
//    @Query("SELECT productaltname.* FROM productaltname WHERE productaltname.productId = :productId")
//    suspend fun altNames(productId: Long): List<ProductAltName>
//
//    @Query(
//        """
//        SELECT ItemEntity.*
//        FROM ItemEntity
//        JOIN product ON product.id = ItemEntity.productId
//        WHERE product.id = :productId
//    """
//    )
//    suspend fun getItems(productId: Long): List<ItemEntity>
//
//    @Delete
//    suspend fun deleteItems(items: List<ItemEntity>)
//
//    @Delete
//    suspend fun deleteVariants(variants: List<ProductVariant>)
//
//    @Update
//    suspend fun updateVariants(variants: List<ProductVariant>)
//
//    @Update
//    suspend fun updateItems(items: List<ItemEntity>)
//
//    @Query(
//        """
//        SELECT COUNT(*)
//        FROM ItemEntity
//        WHERE ItemEntity.id < :itemId AND ItemEntity.productId = :productId
//    """
//    )
//    suspend fun countItemsBefore(
//        itemId: Long,
//        productId: Long
//    ): Int
//
//    @Query(
//        """
//        SELECT COUNT(*)
//        FROM ItemEntity
//        WHERE ItemEntity.id > :itemId AND ItemEntity.productId = :productId
//    """
//    )
//    suspend fun countItemsAfter(
//        itemId: Long,
//        productId: Long
//    ): Int
//
//    // Read
//
//    @Query("SELECT product.* FROM product WHERE product.id = :productId")
//    suspend fun get(productId: Long): Product?
//
//    @Query("SELECT product.* FROM product WHERE product.id = :productId")
//    fun getFlow(productId: Long): Flow<Product?>
//
//    @Query("SELECT productaltname.* FROM productaltname WHERE productaltname.id = :altNameId")
//    suspend fun getAltName(altNameId: Long): ProductAltName?
//
//    @Query("SELECT product.* FROM product WHERE product.name = :name")
//    suspend fun byName(name: String): Product?
//
//    @Query(
//        """
//        SELECT SUM(ItemEntity.price * ItemEntity.quantity)
//        FROM ItemEntity
//        JOIN product ON product.id = ItemEntity.productId
//        WHERE product.id = :productId
//    """
//    )
//    fun totalSpentFlow(productId: Long): Flow<Long?>
//
//    @Query(
//        """
//        WITH date_series AS (
//            SELECT MIN(TransactionEntity.date) AS start_date,
//                   MAX(TransactionEntity.date) AS end_date
//            FROM ItemEntity
//            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//            INNER JOIN product ON product.id = ItemEntity.productId
//                AND productId = :productId
//            UNION ALL
//            SELECT (start_date + 86400000) AS start_date, end_date
//            FROM date_series
//            WHERE date_series.end_date > date_series.start_date
//        ), items AS (
//            SELECT (TransactionEntity.date / 86400000) AS transaction_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
//            FROM ItemEntity
//            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//            INNER JOIN product ON product.id = ItemEntity.productId
//                AND productId = :productId
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
//    fun totalSpentByDayFlow(productId: Long): Flow<List<ItemSpentByTime>>
//
//    @Query(
//        """
//        WITH date_series AS (
//        SELECT (((MIN(TransactionEntity.date) / 86400000) - ((MIN(TransactionEntity.date - 345600000) / 86400000) % 7 )) * 86400000) AS start_date,
//                 (MAX(TransactionEntity.date) - 604800000) AS end_date
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//              AND productId = :productId
//        UNION ALL
//        SELECT (start_date + 604800000) AS start_date, end_date
//        FROM date_series
//        WHERE date_series.end_date >= date_series.start_date
//    ), items AS (
//        SELECT ((TransactionEntity.date - 345600000) / 604800000) AS items_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND productId = :productId
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
//    fun totalSpentByWeekFlow(productId: Long): Flow<List<ItemSpentByTime>>
//
//    @Query(
//        """
//        WITH date_series AS (
//        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
//               DATE(MAX(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS end_date
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND productId = :productId
//        UNION ALL
//        SELECT DATE(start_date, '+1 month') AS start_date, end_date
//        FROM date_series
//        WHERE date_series.end_date > date_series.start_date
//    ), items AS (
//        SELECT STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS items_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND productId = :productId
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
//    fun totalSpentByMonthFlow(productId: Long): Flow<List<ItemSpentByTime>>
//
//    @Query(
//        """
//        WITH date_series AS (
//        SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS start_date,
//               DATE(MAX(TransactionEntity.date) / 1000, 'unixepoch', 'start of year') AS end_date
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND productId = :productId
//        UNION ALL
//        SELECT DATE(start_date, '+1 year') AS start_date, end_date
//        FROM date_series
//        WHERE date_series.end_date > date_series.start_date
//    ), items AS (
//        SELECT STRFTIME('%Y', DATE(TransactionEntity.date / 1000, 'unixepoch')) AS items_time, SUM(ItemEntity.price * ItemEntity.quantity) AS ItemEntity_total
//        FROM ItemEntity
//        JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//        INNER JOIN product ON product.id = ItemEntity.productId
//            AND productId = :productId
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
//    fun totalSpentByYearFlow(productId: Long): Flow<List<ItemSpentByTime>>
//
//    @Transaction
//    suspend fun fullItems(
//        productId: Long,
//        count: Int,
//        offset: Int
//    ): List<Item> {
//        val product = get(productId) ?: return emptyList()
//
//        val items = itemsByProduct(
//            productId,
//            count,
//            offset
//        )
//
//        if (items.isEmpty()) return emptyList()
//
//        return items.map { itemEntity ->
//            val transactionEntity = transactionEntityByItemEntityId(itemEntity.id)
//            val variant = itemEntity.variantId?.let { variantById(it) }
//            val category = categoryById(product.categoryId)!!
//            val producer = product.producerId?.let { producerById(it) }
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
//    @Query("SELECT ItemEntity.* FROM product JOIN ItemEntity ON ItemEntity.productId = product.id WHERE product.id = :productId ORDER BY ItemEntity.id DESC LIMIT 1")
//    suspend fun newestItem(productId: Long): ItemEntity?
//
//    fun allWithAltNamesFlow(): Flow<List<ProductWithAltNames>> {
//        return allFlow().map { list ->
//            list.map { itemEntity ->
//                ProductWithAltNames(
//                    product = itemEntity,
//                    alternativeNames = altNames(itemEntity.id)
//                )
//            }
//        }
//    }
//
//    @Query(
//        """
//        WITH date_series AS (
//            SELECT DATE(MIN(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS start_date,
//                   DATE(MAX(TransactionEntity.date) / 1000, 'unixepoch', 'start of month') AS end_date
//            FROM ItemEntity
//            JOIN TransactionEntity ON TransactionEntity.id = ItemEntity.transactionId
//            WHERE productId = :productId
//            UNION ALL
//            SELECT DATE(start_date, '+1 month') AS start_date, end_date
//            FROM date_series
//            WHERE date_series.end_date > date_series.start_date
//        )
//        SELECT product.*, AVG(ItemEntity.price) AS price, shop.name AS shopName, productvariant.name as variantName, productproducer.name as producerName, STRFTIME('%Y-%m', date_series.start_date) AS time
//        FROM date_series
//        LEFT JOIN TransactionEntity ON STRFTIME('%Y-%m', date_series.start_date) = STRFTIME('%Y-%m', DATE(TransactionEntity.date / 1000, 'unixepoch'))
//        JOIN ItemEntity ON ItemEntity.transactionId = TransactionEntity.id
//            AND ItemEntity.productId = :productId
//        LEFT JOIN shop ON TransactionEntity.shopId = shop.id
//        LEFT JOIN productvariant ON ItemEntity.variantId = productvariant.id
//        LEFT JOIN product ON ItemEntity.productId = product.id
//        LEFT JOIN productproducer ON product.producerId = productproducer.id
//        WHERE time IS NOT NULL
//        GROUP BY time, shopId, variantId, producerId
//        ORDER BY time
//    """
//    )
//    fun averagePriceByVariantByShopByMonthFlow(productId: Long): Flow<List<ProductPriceByShopByTime>>
//
//    @Query("SELECT product.* FROM product ORDER BY product.id DESC")
//    fun allFlow(): Flow<List<Product>>
}