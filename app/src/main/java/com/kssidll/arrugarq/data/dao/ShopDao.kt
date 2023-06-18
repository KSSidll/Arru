package com.kssidll.arrugarq.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arrugarq.data.data.Shop
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopDao {
    @Query("SELECT * FROM shop ORDER BY id ASC")
    suspend fun getAll(): List<Shop>

    @Query("SELECT * FROM shop ORDER BY id ASC")
    fun getAllFlow(): Flow<List<Shop>>

    @Query("SELECT * FROM shop WHERE id == :id")
    suspend fun get(id: Long): Shop

    @Query("SELECT * FROM shop WHERE id == :id")
    fun getFlow(id: Long): Flow<Shop>

    @Query("SELECT * FROM shop WHERE name == :name")
    suspend fun getByName(name: String): Shop

    @Query("SELECT * FROM shop WHERE name == :name")
    fun getByNameFlow(name: String): Flow<Shop>

    @Insert
    suspend fun insert(shop: Shop): Long

    @Update
    suspend fun update(shop: Shop)

    @Delete
    suspend fun delete(shop: Shop)
}