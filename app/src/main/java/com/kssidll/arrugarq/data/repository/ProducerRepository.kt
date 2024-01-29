package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import kotlinx.coroutines.flow.*

class ProducerRepository(private val dao: ProducerDao): ProducerRepositorySource {
    // Create

    override suspend fun insert(producer: ProductProducer): Long {
        TODO("Not yet implemented")
    }

    // Update

    override suspend fun update(producer: ProductProducer) {
        TODO("Not yet implemented")
    }

    override suspend fun update(producers: List<ProductProducer>) {
        TODO("Not yet implemented")
    }

    // Delete

    override suspend fun delete(producer: ProductProducer) {
        TODO("Not yet implemented")
    }

    override suspend fun delete(producers: List<ProductProducer>) {
        TODO("Not yet implemented")
    }

    // Read

    override suspend fun get(producerId: Long): ProductProducer? {
        TODO("Not yet implemented")
    }

    override fun totalSpentFlow(producer: ProductProducer): Flow<Float> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByDayFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByWeekFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByMonthFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>> {
        TODO("Not yet implemented")
    }

    override fun totalSpentByYearFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>> {
        TODO("Not yet implemented")
    }

    override suspend fun fullItems(
        producer: ProductProducer,
        count: Int,
        offset: Int
    ): List<FullItem> {
        TODO("Not yet implemented")
    }

    override fun allFlow(): Flow<List<ProductProducer>> {
        TODO("Not yet implemented")
    }
}
