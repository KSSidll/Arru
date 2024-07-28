package com.kssidll.arru.data.dao

import androidx.room.*
import com.kssidll.arru.data.data.ItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {

    // Create

    @Insert
    suspend fun insert(item: ItemEntity): Long

    // Update

    @Update
    suspend fun update(item: ItemEntity)

    // Delete

    @Delete
    suspend fun delete(item: ItemEntity)

    // Read

    @Query("SELECT ItemEntity.* FROM ItemEntity ORDER BY id DESC LIMIT 1")
    fun newestFlow(): Flow<ItemEntity?>
}