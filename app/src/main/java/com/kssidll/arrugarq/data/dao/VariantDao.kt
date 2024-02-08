package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface VariantDao {
    // Create

    @Insert
    suspend fun insert(variant: ProductVariant): Long

    // Update

    @Update
    suspend fun update(variant: ProductVariant)

    @Update
    suspend fun update(variants: List<ProductVariant>)

    // Delete

    @Delete
    suspend fun delete(variant: ProductVariant)

    @Delete
    suspend fun delete(variants: List<ProductVariant>)

    // Helper

    @Query("SELECT product.* FROM product WHERE product.id = :productId")
    suspend fun getProduct(productId: Long): Product?

    // Read

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.id = :variantId")
    suspend fun get(variantId: Long): ProductVariant?

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.productId = :productId AND productvariant.name = :name")
    suspend fun byProductAndName(
        productId: Long,
        name: String
    ): ProductVariant?

    @Query("SELECT productvariant.* FROM productvariant WHERE productvariant.productId == :productId")
    fun byProductFlow(productId: Long): Flow<List<ProductVariant>>
}