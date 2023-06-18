package com.kssidll.arrugarq.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arrugarq.data.data.ProductCategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductCategoryTypeDao {
    @Query("SELECT * FROM productcategorytype ORDER BY id ASC")
    suspend fun getAll(): List<ProductCategoryType>

    @Query("SELECT * FROM productcategorytype ORDER BY id ASC")
    fun getAllFlow(): Flow<List<ProductCategoryType>>

    @Query("SELECT * FROM productcategorytype WHERE id == :id")
    suspend fun get(id: Long): ProductCategoryType

    @Query("SELECT * FROM productcategorytype WHERE id == :id")
    fun getFlow(id: Long): Flow<ProductCategoryType>

    @Query("SELECT * FROM productcategorytype WHERE name == :name")
    suspend fun getByName(name: String): ProductCategoryType

    @Query("SELECT * FROM productcategorytype WHERE name == :name")
    fun getByNameFlow(name: String): Flow<ProductCategoryType>

    @Insert
    suspend fun insert(productCategoryType: ProductCategoryType): Long

    @Update
    suspend fun update(productCategoryType: ProductCategoryType)

    @Delete
    suspend fun delete(productCategoryType: ProductCategoryType)
}