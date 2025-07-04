package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ProducerDao
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.paging.FullItemPagingSource
import com.kssidll.arru.data.repository.ProducerRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProducerRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ProducerRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ProducerRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

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

        if (other != null && other.id != producer.id) {
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

        if (!force && (products.isNotEmpty() || productAltNames.isNotEmpty() || items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
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

    override fun getFlow(producerId: Long): Flow<Data<ProductProducer?>> {
        return dao.getFlow(producerId)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<ProductProducer?>() }
    }

    override fun totalSpentFlow(producer: ProductProducer): Flow<Data<Float?>> {
        return dao.totalSpentFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map {
                Data.Loaded(
                    it?.toFloat()
                        ?.div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
                )
            }
            .onStart { Data.Loading<Long>() }
    }

    override fun totalSpentByDayFlow(producer: ProductProducer): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByDayFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun totalSpentByWeekFlow(producer: ProductProducer): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByWeekFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun totalSpentByMonthFlow(producer: ProductProducer): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByMonthFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun totalSpentByYearFlow(producer: ProductProducer): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByYearFlow(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun fullItemsPagedFlow(producer: ProductProducer): Flow<PagingData<FullItem>> {
        return Pager(
            config = PagingConfig(pageSize = 3),
            initialKey = 0,
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            producer.id,
                            loadSize,
                            start
                        )
                    },
                    itemsBefore = {
                        dao.countItemsBefore(
                            it,
                            producer.id
                        )
                    },
                    itemsAfter = {
                        dao.countItemsAfter(
                            it,
                            producer.id
                        )
                    },
                )
            }
        )
            .flow
    }

    override fun allFlow(): Flow<Data<ImmutableList<ProductProducer>>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ProductProducer>>() }
    }

    override suspend fun totalCount(): Int {
        return dao.totalCount()
    }

    override suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<ProductProducer> {
        return dao.getPagedList(
            limit,
            offset
        ).toImmutableList()
    }
}
