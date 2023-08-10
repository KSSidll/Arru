package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class ProductProducerRepository(private val productProducerDao: ProductProducerDao): IProductProducerRepository {
    override suspend fun getAll(): List<ProductProducer> {
        return productProducerDao.getAll()
    }

    override fun getAllFlow(): Flow<List<ProductProducer>> {
        return productProducerDao.getAllFlow()
    }

    override suspend fun get(id: Long): ProductProducer? {
        return productProducerDao.get(id)
    }

    override fun getFlow(id: Long): Flow<ProductProducer> {
        return productProducerDao.getFlow(id)
    }

    override suspend fun getByName(name: String): ProductProducer? {
        return productProducerDao.getByName(name)
    }

    override fun getByNameFlow(name: String): Flow<ProductProducer> {
        return productProducerDao.getByNameFlow(name)
    }

    override suspend fun insert(producer: ProductProducer): Long {
        return productProducerDao.insert(producer)
    }

    override suspend fun update(producer: ProductProducer) {
        productProducerDao.update(producer)
    }

    override suspend fun delete(producer: ProductProducer) {
        productProducerDao.delete(producer)
    }
}
