package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface ItemDao {
    // Create

    @Insert
    suspend fun insert(item: Item): Long

    // Update

    @Update
    suspend fun update(item: Item)

    @Update
    suspend fun update(items: List<Item>)

    // Delete

    @Delete
    suspend fun delete(item: Item)

    @Delete
    suspend fun delete(items: List<Item>)

    // Helper


    // Read

    @Query("SELECT item.* FROM item WHERE item.id = :itemId")
    suspend fun get(itemId: Long): Item?

    @Query("SELECT item.* FROM item ORDER BY id DESC LIMIT 1")
    suspend fun newest(): Item?

    @Query("SELECT item.* FROM item ORDER BY id DESC LIMIT 1")
    fun newestFlow(): Flow<Item>
}