package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.flow.*

class ProducerRepository(private val dao: ProducerDao): IProducerRepository {
    override suspend fun getAll(): List<ProductProducer> {
        return dao.getAll()
    }

    override fun getAllFlow(): Flow<List<ProductProducer>> {
        return dao.getAllFlow()
    }

    override suspend fun get(id: Long): ProductProducer? {
        return dao.get(id)
    }

    override fun getFlow(id: Long): Flow<ProductProducer> {
        return dao.getFlow(id)
    }

    override suspend fun getByName(name: String): ProductProducer? {
        return dao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<ProductProducer> {
        return dao.getByNameFlow(name)
    }

    override suspend fun insert(producer: ProductProducer): Long {
        return dao.insert(producer)
    }

    override suspend fun update(producer: ProductProducer) {
        dao.update(producer)
    }

    override suspend fun delete(producer: ProductProducer) {
        dao.delete(producer)
    }
}
