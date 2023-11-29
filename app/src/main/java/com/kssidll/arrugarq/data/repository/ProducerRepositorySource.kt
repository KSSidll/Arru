package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

interface ProducerRepositorySource {
    suspend fun getAll(): List<ProductProducer>
    fun getAllFlow(): Flow<List<ProductProducer>>
    suspend fun get(id: Long): ProductProducer?
    fun getFlow(id: Long): Flow<ProductProducer>
    suspend fun getByName(name: String): ProductProducer?
    fun getByNameFlow(name: String): Flow<ProductProducer>
    suspend fun insert(producer: ProductProducer): Long
    suspend fun update(producer: ProductProducer)
    suspend fun delete(producer: ProductProducer)
}