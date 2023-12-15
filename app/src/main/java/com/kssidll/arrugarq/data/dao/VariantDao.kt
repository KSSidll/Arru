package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface VariantDao {
    @Query("SELECT * FROM productvariant ORDER BY id ASC")
    suspend fun getAll(): List<ProductVariant>

    @Query("SELECT * FROM productvariant ORDER BY id ASC")
    fun getAllFlow(): Flow<List<ProductVariant>>

    @Query("SELECT * FROM productvariant WHERE id == :id")
    suspend fun get(id: Long): ProductVariant?

    @Query("SELECT * FROM productvariant WHERE id == :id")
    fun getFlow(id: Long): Flow<ProductVariant>

    @Query("SELECT * FROM productvariant WHERE productId == :productId AND name == :name")
    suspend fun getByProductIdAndName(
        productId: Long,
        name: String
    ): ProductVariant?

    @Query("SELECT * FROM productvariant WHERE productId == :productId")
    suspend fun getByProductId(productId: Long): List<ProductVariant>

    @Query("SELECT * FROM productvariant WHERE productId == :productId")
    fun getByProductIdFlow(productId: Long): Flow<List<ProductVariant>>

    @Query("SELECT * FROM productvariant WHERE name == :name")
    suspend fun getByName(name: String): List<ProductVariant>

    @Query("SELECT * FROM productvariant WHERE name == :name")
    fun getByNameFlow(name: String): Flow<List<ProductVariant>>

    @Insert
    suspend fun insert(variant: ProductVariant): Long

    @Update
    suspend fun update(variant: ProductVariant)

    @Update
    suspend fun update(variants: List<ProductVariant>)

    @Delete
    suspend fun delete(variant: ProductVariant)

    @Delete
    suspend fun delete(variants: List<ProductVariant>)
}