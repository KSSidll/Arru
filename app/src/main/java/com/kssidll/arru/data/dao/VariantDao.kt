package com.kssidll.arru.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductVariant
import kotlinx.coroutines.flow.Flow

@Dao
interface VariantDao {
    // Create

    @Insert
    suspend fun insert(variant: ProductVariant): Long

    // Update

    @Update
    suspend fun update(variant: ProductVariant)

    // Delete

    @Delete
    suspend fun delete(variant: ProductVariant)

    // Helper

    @Query("SELECT product.* FROM product WHERE product.id = :productId")
    suspend fun getProduct(productId: Long): Product?

    @Query("SELECT ItemEntity.* FROM ItemEntity WHERE ItemEntity.variantId = :variantId")
    suspend fun getItems(variantId: Long): List<ItemEntity>

    @Update
    suspend fun updateItems(entities: List<ItemEntity>)

    @Delete
    suspend fun deleteItems(entities: List<ItemEntity>)

    // Read

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun get(variantId: Long): ProductVariant?

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.id = :variantId")
    fun getFlow(variantId: Long): Flow<ProductVariant?>

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.name = :name")
    fun byName(
        name: String
    ): Flow<List<ProductVariant>>

    @Query("SELECT productvariant.* FROM productvariant WHERE ((:includeGlobal AND productvariant.productId IS NULL) OR (productvariant.productId = :productId)) AND productvariant.name = :name")
    suspend fun byProductAndName(
        productId: Long?,
        name: String,
        includeGlobal: Boolean
    ): ProductVariant?

    @Query("SELECT productvariant.* FROM productvariant WHERE (:includeGlobal AND productvariant.productId IS NULL) OR productvariant.productId = :productId")
    fun byProductFlow(productId: Long, includeGlobal: Boolean): Flow<List<ProductVariant>>

    @Query("SELECT COUNT(*) FROM productvariant")
    suspend fun totalCount(): Int

    @Query("SELECT productvariant.* FROM productvariant ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<ProductVariant>
}