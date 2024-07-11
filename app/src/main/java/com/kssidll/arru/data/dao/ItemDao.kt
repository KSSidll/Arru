package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Query

@Dao
interface ItemDao {
    @Query("SELECT 1 FROM ItemEntity")
    suspend fun get(): Long
//    // Create
//
//    @Insert
//    suspend fun insert(item: ItemEntity): Long
//
//    // Update
//
//    @Update
//    suspend fun update(item: ItemEntity)
//
//    // Delete
//
//    @Delete
//    suspend fun delete(item: ItemEntity)
//
//    // Helper
//
//    @Query("SELECT TransactionEntity.* FROM TransactionEntity WHERE TransactionEntity.id = :transactionId")
//    suspend fun getTransactionBasket(transactionId: Long): TransactionEntity?
//
//    @Query("SELECT product.* FROM product WHERE product.id = :productId")
//    suspend fun getProduct(productId: Long): Product?
//
//    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.id = :variantId")
//    suspend fun getVariant(variantId: Long): ProductVariant?
//
//    // Read
//
//    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE ItemEntity.id = :itemId")
//    suspend fun get(itemId: Long): ItemEntity?
//
//    @Query("SELECT ItemEntity.* FROM ItemEntity ORDER BY id DESC LIMIT 1")
//    suspend fun newest(): ItemEntity?
//
//    @Query("SELECT ItemEntity.* FROM ItemEntity ORDER BY id DESC LIMIT 1")
//    fun newestFlow(): Flow<ItemEntity?>
}