package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ProductEntityDao
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.data.ProductPriceByShopByVariantByProducerByTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map

class ProductRepository(private val dao: ProductEntityDao) : ProductRepositorySource {
    // Create

    override suspend fun insert(entity: ProductEntity): Long = dao.insert(entity)

    // Update

    override suspend fun update(entity: ProductEntity) = dao.update(entity)

    override suspend fun update(entity: List<ProductEntity>) = dao.update(entity)

    // Delete

    override suspend fun delete(entity: ProductEntity) = dao.delete(entity)

    override suspend fun delete(entity: List<ProductEntity>) = dao.delete(entity)

    // Read

    override fun get(id: Long): Flow<ProductEntity?> = dao.get(id).cancellable()

    override fun byName(name: String): Flow<ProductEntity?> = dao.byName(name).cancellable()

    override fun byProductCategory(id: Long): Flow<ImmutableList<ProductEntity>> =
        dao.byProductCategory(id).cancellable().map { it.toImmutableList() }

    override fun byProductProducer(id: Long): Flow<ImmutableList<ProductEntity>> =
        dao.byProductProducer(id).cancellable().map { it.toImmutableList() }

    override fun all(): Flow<ImmutableList<ProductEntity>> =
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

    override fun averagePriceByShopByVariantByProducerByDay(
        id: Long
    ): Flow<ImmutableList<ProductPriceByShopByVariantByProducerByTime>> =
        dao.averagePriceByShopByVariantByProducerByDay(id).cancellable().map {
            it.toImmutableList()
        }
}
