package com.kssidll.arrugarq.data.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arrugarq.data.data.Item
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDao {
    @Query("SELECT * FROM item ORDER BY id ASC")
    suspend fun getAll(): List<Item>

    @Query("SELECT * FROM item ORDER BY id ASC")
    fun getAllFlow(): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE id == :id")
    suspend fun get(id: Long): Item

    @Query("SELECT * FROM item WHERE id == :id")
    fun getFlow(id: Long): Flow<Item>

    @Query("SELECT * FROM item WHERE name == :name")
    suspend fun getByName(name: String): List<Item>

    @Query("SELECT * FROM item WHERE name == :name")
    fun getByNameFlow(name: String): Flow<List<Item>>

    @Insert
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete (item: Item)
}