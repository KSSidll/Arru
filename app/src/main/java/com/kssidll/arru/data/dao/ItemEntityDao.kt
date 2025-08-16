package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.data.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemEntityDao {
    // Create

    @Insert
    suspend fun insert(entity: ItemEntity): Long

    // Update

    @Update
    suspend fun update(entity: ItemEntity)

    // Delete

    @Delete
    suspend fun delete(entity: ItemEntity)

    // Helper

    @Query("SELECT TransactionEntity.* FROM TransactionEntity WHERE TransactionEntity.id = :transactionId")
    suspend fun getTransactionBasket(transactionId: Long): TransactionEntity?

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.id = :productId")
    suspend fun getProduct(productId: Long): ProductEntity?

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.id = :variantId")
    suspend fun getVariant(variantId: Long): ProductVariantEntity?

    // Read

    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE ItemEntity.id = :id")
    fun get(id: Long): Flow<ItemEntity?>









    @Query("SELECT ItemEntity.* FROM ItemEntity ORDER BY id DESC LIMIT 1")
    fun newest(): Flow<ItemEntity?>
}