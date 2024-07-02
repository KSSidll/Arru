package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.data.TransactionBasket
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT transactionbasket.* FROM transactionbasket WHERE transactionbasket.id = :transactionId")
    suspend fun getTransactionBasket(transactionId: Long): TransactionBasket?

    @Query("SELECT product.* FROM product WHERE product.id = :productId")
    suspend fun getProduct(productId: Long): Product?

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun getVariant(variantId: Long): ProductVariant?

    // Read

    @Query("SELECT item.* FROM item WHERE item.id = :itemId")
    suspend fun get(itemId: Long): Item?

    @Query("SELECT item.* FROM item ORDER BY id DESC LIMIT 1")
    suspend fun newest(): Item?

    @Query("SELECT item.* FROM item ORDER BY id DESC LIMIT 1")
    fun newestFlow(): Flow<Item?>

    @Query("SELECT COUNT(*) FROM item")
    suspend fun totalCount(): Int

    @Query("SELECT item.* FROM item ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<Item>

    @Query("SELECT item.* FROM item WHERE item.transactionBasketId = :transactionId")
    suspend fun getByTransaction(transactionId: Long): List<Item>
}