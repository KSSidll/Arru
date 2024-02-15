package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.paging.*
import com.kssidll.arrugarq.data.repository.ProducerRepositorySource.Companion.DeleteResult
import com.kssidll.arrugarq.data.repository.ProducerRepositorySource.Companion.InsertResult
import com.kssidll.arrugarq.data.repository.ProducerRepositorySource.Companion.MergeResult
import com.kssidll.arrugarq.data.repository.ProducerRepositorySource.Companion.UpdateResult
import kotlinx.coroutines.flow.*

class ProducerRepository(private val dao: ProducerDao): ProducerRepositorySource {
    // Create

    override suspend fun insert(name: String): InsertResult {
        val producer = ProductProducer(name.trim())

        if (producer.validName()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byName(producer.name)

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(producer))
    }

    // Update

    override suspend fun update(
        producerId: Long,
        name: String
    ): UpdateResult {
        val producer = dao.get(producerId) ?: return UpdateResult.Error(UpdateResult.InvalidId)

        producer.name = name

        if (producer.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byName(producer.name)

        if (other != null) {
            if (other.id == producer.id) {
                return UpdateResult.Success
            }

            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(producer)

        return UpdateResult.Success
    }

    override suspend fun merge(
        producer: ProductProducer,
        mergingInto: ProductProducer
    ): MergeResult {
        if (dao.get(producer.id) == null) {
            return MergeResult.Error(MergeResult.InvalidProducer)
        }

        if (dao.get(mergingInto.id) == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val products = dao.getProducts(producer.id)
        products.forEach { it.producerId = mergingInto.id }
        dao.updateProducts(products)

        dao.delete(producer)

        return MergeResult.Success
    }

    // Delete

    override suspend fun delete(
        producerid: Long,
        force: Boolean
    ): DeleteResult {
        val producer = dao.get(producerid) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val products = dao.getProducts(producerid)
        val productVariants = dao.getProductsVariants(producerid)
        val productAltNames = dao.getProductsAltNames(producerid)
        val items = dao.getItems(producerid)
        val transactionBasketItems = dao.getTransactionBasketItems(producerid)

        if (!force && (products.isNotEmpty() || productAltNames.isNotEmpty() || items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteTransactionBasketItems(transactionBasketItems)
            dao.deleteItems(items)
            dao.deleteProductAltNames(productAltNames)
            dao.deleteProductVariants(productVariants)
            dao.deleteProducts(products)
            dao.delete(producer)
        }

        return DeleteResult.Success
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
