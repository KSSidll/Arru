package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop ORDER BY id ASC")
    suspend fun getAll(): List<Shop>

    @Query("SELECT * FROM shop ORDER BY id ASC")
    fun getAllFlow(): Flow<List<Shop>>

    @Query("SELECT * FROM shop WHERE id == :id")
    suspend fun get(id: Long): Shop?

    @Query("SELECT * FROM shop WHERE id == :id")
    fun getFlow(id: Long): Flow<Shop>

    @Query("SELECT * FROM shop WHERE name == :name")
    suspend fun getByName(name: String): Shop?

    @Query("SELECT * FROM shop WHERE name == :name")
    fun getByNameFlow(name: String): Flow<Shop>

    @Insert
    suspend fun insert(shop: Shop): Long

    @Update
    suspend fun update(shop: Shop)

    @Delete
    suspend fun delete(shop: Shop)
}