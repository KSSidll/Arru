package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductVariantEntityDao {
    // Create

    @Insert
    suspend fun insert(entity: ProductVariantEntity): Long

    // Update

    @Update
    suspend fun update(entity: ProductVariantEntity)

    // Delete

    @Delete
    suspend fun delete(entity: ProductVariantEntity)

    // Helper

    @Query("SELECT ProductEntity.* FROM ProductEntity WHERE ProductEntity.id = :productId")
    suspend fun getProduct(productId: Long): ProductEntity?

    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE ItemEntity.variantId = :variantId")
    suspend fun getItems(variantId: Long): List<ItemEntity>

    @Update
    suspend fun updateItems(entities: List<ItemEntity>)

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    // Read

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.id = :variantId")
    suspend fun get(variantId: Long): ProductVariantEntity?

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.id = :variantId")
    fun getFlow(variantId: Long): Flow<ProductVariantEntity?>

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ProductVariantEntity.name = :name")
    fun byName(
        name: String
    ): Flow<List<ProductVariantEntity>>

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE ((:includeGlobal AND ProductVariantEntity.productId IS NULL) OR (ProductVariantEntity.productId = :productId)) AND ProductVariantEntity.name = :name")
    suspend fun byProductAndName(
        productId: Long?,
        name: String,
        includeGlobal: Boolean
    ): ProductVariantEntity?

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity WHERE (:includeGlobal AND ProductVariantEntity.productId IS NULL) OR ProductVariantEntity.productId = :productId")
    fun byProductFlow(productId: Long, includeGlobal: Boolean): Flow<List<ProductVariantEntity>>

    @Query("SELECT COUNT(*) FROM ProductVariantEntity")
    suspend fun totalCount(): Int

    @Query("SELECT ProductVariantEntity.* FROM ProductVariantEntity ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<ProductVariantEntity>
}