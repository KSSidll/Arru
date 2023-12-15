package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface ProductDao {
    @Query("SELECT * FROM product ORDER BY id ASC")
    suspend fun getAll(): List<Product>

    @Query("SELECT * FROM product ORDER BY id ASC")
    fun getAllFlow(): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE id == :id")
    suspend fun get(id: Long): Product?

    @Query("SELECT * FROM product WHERE id == :id")
    fun getFlow(id: Long): Flow<Product>

    @Query("SELECT * FROM product WHERE categoryId == :categoryId")
    suspend fun getByCategoryId(categoryId: Long): List<Product>

    @Query("SELECT * FROM product WHERE categoryId == :categoryId")
    fun getByCategoryIdFlow(categoryId: Long): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE producerId == :producerId")
    suspend fun getByProducerId(producerId: Long): List<Product>

    @Query("SELECT * FROM product WHERE producerId == :producerId")
    fun getByProducerIdFlow(producerId: Long): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE name == :name")
    suspend fun getByName(name: String): Product?

    @Query("SELECT * FROM product WHERE name == :name")
    fun getByNameFlow(name: String): Flow<Product>

    @Query("SELECT * FROM product WHERE name = :name AND (producerId = :producerId OR (:producerId IS NULL AND producerId IS NULL))")
    suspend fun getByNameAndProducerId(
        name: String,
        producerId: Long?
    ): Product?

    @Query("SELECT product.* FROM product LEFT JOIN productaltname ON product.id = productaltname.productId WHERE product.name LIKE '%' || :name || '%' OR productaltname.name LIKE '%' || :name || '%' OR :name = ''")
    suspend fun findLike(name: String): List<Product>

    @Query("SELECT product.* FROM product LEFT JOIN productaltname ON product.id = productaltname.productId WHERE product.name LIKE '%' || :name || '%' OR productaltname.name LIKE '%' || :name || '%' OR :name = ''")
    fun findLikeFlow(name: String): Flow<List<Product>>

    @Transaction
    @Query("SELECT * FROM product")
    suspend fun getAllWithAltNames(): List<ProductWithAltNames>

    @Transaction
    @Query("SELECT * FROM product")
    fun getAllWithAltNamesFlow(): Flow<List<ProductWithAltNames>>

    @Insert
    suspend fun insert(product: Product): Long

    @Insert
    suspend fun addAltName(alternativeName: ProductAltName): Long

    @Update
    suspend fun update(product: Product)

    @Update
    suspend fun update(products: List<Product>)

    @Update
    suspend fun updateAltName(alternativeName: ProductAltName)

    @Delete
    suspend fun delete(product: Product)

    @Delete
    suspend fun delete(products: List<Product>)

    @Delete
    suspend fun deleteAltName(alternativeName: ProductAltName)
}