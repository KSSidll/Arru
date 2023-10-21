package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

@Dao
interface ProducerDao {
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
