package com.kssidll.arrugarq.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.kssidll.arrugarq.data.data.ProductProducer
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductProducerDao {
    @Query("SELECT * FROM productproducer ORDER BY id ASC")
    suspend fun getAll(): List<ProductProducer>

    @Query("SELECT * FROM productproducer ORDER BY id ASC")
    fun getAllFlow(): Flow<List<ProductProducer>>

    @Query("SELECT * FROM productproducer WHERE id == :id")
    suspend fun get(id: Long): ProductProducer?

    @Query("SELECT * FROM productproducer WHERE id == :id")
    fun getFlow(id: Long): Flow<ProductProducer>

    @Query("SELECT * FROM productproducer WHERE name == :name")
    suspend fun getByName(name: String): ProductProducer?

    @Query("SELECT * FROM productproducer WHERE name == :name")
    fun getByNameFlow(name: String): Flow<ProductProducer>

    @Insert
    suspend fun insert(producer: ProductProducer): Long

    @Update
    suspend fun update(producer: ProductProducer)

    @Delete
    suspend fun delete(producer: ProductProducer)
}
