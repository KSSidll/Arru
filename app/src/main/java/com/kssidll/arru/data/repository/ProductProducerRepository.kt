package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ProductProducerEntityDao
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.repository.ProductProducerRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProductProducerRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ProductProducerRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ProductProducerRepositorySource.Companion.UpdateResult
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ProductProducerRepository(private val dao: ProductProducerEntityDao) :
    ProductProducerRepositorySource {
    // Create

    override suspend fun insert(name: String): InsertResult {
        val producer = ProductProducerEntity(name.trim())

        if (producer.validName().not()) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byName(producer.name)

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(producer))
    }

    // Update

    override suspend fun update(id: Long, name: String): UpdateResult {
        val producer =
            dao.get(id).first()?.copy(name = name)
                ?: return UpdateResult.Error(UpdateResult.InvalidId)

        if (producer.validName().not()) {
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
        mergingInto: ProductProducerEntity,
    ): MergeResult {
        if (dao.get(entity.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidProducer)
        }

        if (dao.get(mergingInto.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val products =
            dao.getProducts(entity.id).map { it.copy(productProducerEntityId = mergingInto.id) }

        dao.updateProducts(products)

        dao.delete(entity)

        return MergeResult.Success
    }

    // Delete

    override suspend fun delete(id: Long, force: Boolean): DeleteResult {
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

    override fun all(): Flow<ImmutableList<ProductProducerEntity>> =
        dao.all().cancellable().map { it.toImmutableList() }

    override fun totalSpent(id: Long): Flow<Float?> =
        dao.totalSpent(id).cancellable().map {
            it?.toFloat()?.div(ItemEntity.PRICE_DIVISOR * ItemEntity.QUANTITY_DIVISOR)
        }

    override fun itemsFor(id: Long): Flow<PagingData<Item>> =
        Pager(
                config = PagingConfig(pageSize = 8, enablePlaceholders = true),
                pagingSourceFactory = { dao.itemsFor(id) },
            )
            .flow
            .cancellable()

    override fun totalSpentByDay(id: Long): Flow<ImmutableList<ItemSpentChartData>> =
        dao.totalSpentByDay(id).cancellable().map { it.toImmutableList() }

    override fun totalSpentByWeek(id: Long): Flow<ImmutableList<ItemSpentChartData>> =
        dao.totalSpentByWeek(id).cancellable().map { it.toImmutableList() }

    override fun totalSpentByMonth(id: Long): Flow<ImmutableList<ItemSpentChartData>> =
        dao.totalSpentByMonth(id).cancellable().map { it.toImmutableList() }

    override fun totalSpentByYear(id: Long): Flow<ImmutableList<ItemSpentChartData>> =
        dao.totalSpentByYear(id).cancellable().map { it.toImmutableList() }
}
