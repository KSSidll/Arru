package com.kssidll.arru.data.dao

import androidx.room.*
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

    // Read

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun get(variantId: Long): ProductVariant?

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.id = :variantId")
    fun getFlow(variantId: Long): Flow<ProductVariant?>

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.productId = :productId AND productvariant.name = :name")
    suspend fun byProductAndName(
        productId: Long,
        name: String
    ): ProductVariant?

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.productId == :productId")
    fun byProductFlow(productId: Long): Flow<List<ProductVariant>>

    @Query("SELECT COUNT(*) FROM productvariant")
    suspend fun totalCount(): Int

    @Query("SELECT productvariant.* FROM productvariant ORDER BY id LIMIT :limit OFFSET :offset")
    suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): List<ProductVariant>
}