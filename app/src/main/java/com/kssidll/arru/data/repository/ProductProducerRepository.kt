package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ProductProducerEntityDao
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.paging.FullItemPagingSource
import com.kssidll.arru.data.repository.ProductProducerRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProductProducerRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ProductProducerRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ProductProducerRepositorySource.Companion.UpdateResult
import com.kssidll.arru.data.view.Item
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ProductProducerRepository(private val dao: ProductProducerEntityDao): ProductProducerRepositorySource {
    // Create

    override suspend fun insert(name: String): InsertResult {
        val producer = ProductProducerEntity(name.trim())

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
        id: Long,
        name: String
    ): UpdateResult {
        val producer = dao.get(id).first() ?: return UpdateResult.Error(UpdateResult.InvalidId)

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
        entity: ProductProducerEntity,
        mergingInto: ProductProducerEntity
    ): MergeResult {
        if (dao.get(entity.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidProducer)
        }

        if (dao.get(mergingInto.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val products = dao.getProducts(entity.id)
        products.forEach { it.productProducerEntityId = mergingInto.id }
        dao.updateProducts(products)

        dao.delete(entity)

        return MergeResult.Success
    }

    // Delete

    override suspend fun delete(
        id: Long,
        force: Boolean
    ): DeleteResult {
        val producer = dao.get(id).first() ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val products = dao.getProducts(id)
        val productVariants = dao.getProductsVariants(id)
        val items = dao.getItems(id)

        if (!force && (products.isNotEmpty() || items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteItems(items)
            dao.deleteProductVariants(productVariants)
            dao.deleteProducts(products)
            dao.delete(producer)
        }

        return DeleteResult.Success
    }

    // Read

    override fun get(id: Long): Flow<ProductProducerEntity?> = dao.get(id).cancellable()

    override fun itemsFor(id: Long): Flow<PagingData<Item>> =
        Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { dao.itemsFor(id) }
        ).flow.cancellable()









    override fun totalSpent(producer: ProductProducerEntity): Flow<Float?> {
        return dao.totalSpent(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map {
                it?.toFloat()?.div(ItemEntity.PRICE_DIVISOR * ItemEntity.QUANTITY_DIVISOR)
            }
    }

    override fun totalSpentByDay(producer: ProductProducerEntity): Flow<ImmutableList<ItemSpentByTime>> {
        return dao.totalSpentByDay(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByWeek(producer: ProductProducerEntity): Flow<ImmutableList<ItemSpentByTime>> {
        return dao.totalSpentByWeek(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByMonth(producer: ProductProducerEntity): Flow<ImmutableList<ItemSpentByTime>> {
        return dao.totalSpentByMonth(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByYear(producer: ProductProducerEntity): Flow<ImmutableList<ItemSpentByTime>> {
        return dao.totalSpentByYear(producer.id)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun fullItemsPaged(producer: ProductProducerEntity): Flow<PagingData<FullItem>> {
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

    override fun all(): Flow<ImmutableList<ProductProducerEntity>> {
        return dao.all()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }
}
