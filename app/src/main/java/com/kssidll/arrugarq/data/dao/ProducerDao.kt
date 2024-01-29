package com.kssidll.arrugarq.data.dao

import androidx.room.*
import com.kssidll.arrugarq.data.data.*

@Dao
interface ProducerDao {
    // Create

    @Insert
    suspend fun insert(producer: ProductProducer): Long

    // Update

    @Update
    suspend fun update(producer: ProductProducer)

    @Update
    suspend fun update(producers: List<ProductProducer>)

    // Delete

    @Delete
    suspend fun delete(producer: ProductProducer)

    @Delete
    suspend fun delete(producers: List<ProductProducer>)

    // Helper


    // Read

    //    @Query("SELECT * FROM productproducer ORDER BY id ASC")
    //    suspend fun getAll(): List<ProductProducer>
    //
    //    @Query("SELECT * FROM productproducer ORDER BY id ASC")
    //    fun getAllFlow(): Flow<List<ProductProducer>>
    //
    //    @Query("SELECT * FROM productproducer WHERE id == :id")
    //    suspend fun get(id: Long): ProductProducer?
    //
    //    @Query("SELECT * FROM productproducer WHERE id == :id")
    //    fun getFlow(id: Long): Flow<ProductProducer>
    //
    //    @Query("SELECT * FROM productproducer WHERE name == :name")
    //    suspend fun getByName(name: String): ProductProducer?
    //
    //    @Query("SELECT * FROM productproducer WHERE name == :name")
    //    fun getByNameFlow(name: String): Flow<ProductProducer>
}
