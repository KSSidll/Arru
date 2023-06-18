package com.kssidll.arrugarq.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arrugarq.data.data.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM product ORDER BY id ASC")
    suspend fun getAll(): List<Product>

    @Query("SELECT * FROM product ORDER BY id ASC")
    fun getAllFlow(): Flow<List<Product>>

    @Query("SELECT * FROM product WHERE id == :id")
    suspend fun get(id: Long): Product

    @Query("SELECT * FROM product WHERE id == :id")
    fun getFlow(id: Long): Flow<Product>

    @Query("SELECT * FROM product WHERE categoryId == :categoryId")
    suspend fun getByCategoryId(categoryId: Long): List<Product>

    @Query("SELECT * FROM product WHERE categoryId == :categoryId")
   fun getByCategoryIdFlow(categoryId: Long): Flow<List<Product>>

   @Query("SELECT * FROM product WHERE name == :name")
   suspend fun getByName(name: String): Product

   @Query("SELECT * FROM product WHERE name == :name")
   fun getByNameFlow(name: String): Flow<Product>

   @Insert
   suspend fun insert(product: Product): Long

   @Update
   suspend fun update(product: Product)

   @Delete
   suspend fun delete(product: Product)
}