package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.paging.*
import kotlinx.coroutines.flow.*

class ProducerRepository(private val dao: ProducerDao): ProducerRepositorySource {
    // Create

    override suspend fun insert(producer: ProductProducer): Long {
        return dao.insert(producer)
    }

    // Update

    override suspend fun update(producer: ProductProducer) {
        dao.update(producer)
    }

    override suspend fun update(producers: List<ProductProducer>) {
        dao.update(producers)
    }

    // Delete

    override suspend fun delete(producer: ProductProducer) {
        dao.delete(producer)
    }

    override suspend fun delete(producers: List<ProductProducer>) {
        dao.delete(producers)
    }

    // Read

    override suspend fun get(producerId: Long): ProductProducer? {
        return dao.get(producerId)
    }

    override fun totalSpentFlow(producer: ProductProducer): Flow<Long> {
        return dao.totalSpentFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByDayFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByDayFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByWeekFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByWeekFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByMonthFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByMonthFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByYearFlow(producer: ProductProducer): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByYearFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun fullItemsPagedFlow(producer: ProductProducer): Flow<PagingData<FullItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            producer.id,
                            loadSize,
                            start
                        )
                    }
                )
            }
        )
            .flow
    }

    override fun allFlow(): Flow<List<ProductProducer>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
    }
}
