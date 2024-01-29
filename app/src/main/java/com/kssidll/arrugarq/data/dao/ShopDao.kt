package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*

@Dao
interface ShopDao {
    // Create

    @Insert
    suspend fun insert(shop: Shop): Long

    // Update

    @Update
    suspend fun update(shop: Shop)

    @Update
    suspend fun update(shops: List<Shop>)

    // Delete

    @Delete
    suspend fun delete(shop: Shop)

    @Delete
    suspend fun delete(shops: List<Shop>)

    // Helper


    // Read

    //    @Query("SELECT * FROM shop ORDER BY id ASC")
    //    suspend fun getAll(): List<Shop>
    //
    //    @Query("SELECT * FROM shop ORDER BY id ASC")
    //    fun getAllFlow(): Flow<List<Shop>>
    //
    //    @Query("SELECT * FROM shop WHERE id == :id")
    //    suspend fun get(id: Long): Shop?
    //
    //    @Query("SELECT * FROM shop WHERE id == :id")
    //    fun getFlow(id: Long): Flow<Shop>
    //
    //    @Query("SELECT * FROM shop WHERE name == :name")
    //    suspend fun getByName(name: String): Shop?
    //
    //    @Query("SELECT * FROM shop WHERE name == :name")
    //    fun getByNameFlow(name: String): Flow<Shop>
}