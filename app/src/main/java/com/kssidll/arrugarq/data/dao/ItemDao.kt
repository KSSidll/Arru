package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface ItemDao {
    // Create

    @Insert
    suspend fun insert(item: Item): Long

    // Update

    @Update
    suspend fun update(item: Item)

    // Delete

    @Delete
    suspend fun delete(item: Item)

    // Helper

    @Query("SELECT product.* FROM product WHERE product.id = :productId")
    suspend fun getProduct(productId: Long): Product?

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun getVariant(variantId: Long): ProductVariant?

    @Query(
        """
        SELECT transactionbasketitem.*
        FROM transactionbasketitem
        JOIN item ON item.id = transactionbasketitem.itemId
        WHERE item.id = :itemId
    """
    )
    suspend fun getTransactionBasketItems(itemId: Long): List<TransactionBasketItem>

    @Delete
    suspend fun deleteTransactionBasketItems(items: List<TransactionBasketItem>)

    // Read

    @Query("SELECT item.* FROM item WHERE item.id = :itemId")
    suspend fun get(itemId: Long): Item?

    @Query("SELECT item.* FROM item ORDER BY id DESC LIMIT 1")
    suspend fun newest(): Item?

    @Query("SELECT item.* FROM item ORDER BY id DESC LIMIT 1")
    fun newestFlow(): Flow<Item>
}