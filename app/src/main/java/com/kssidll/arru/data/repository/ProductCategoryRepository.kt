package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ProductCategoryEntityDao
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.data.TotalSpentByCategory
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map

class ProductCategoryRepository(private val dao: ProductCategoryEntityDao) :
    ProductCategoryRepositorySource {
    // Create

    override suspend fun insert(entity: ProductCategoryEntity): Long = dao.insert(entity)

    // Update

    override suspend fun update(entity: ProductCategoryEntity) = dao.update(entity)

    // Delete

    override suspend fun delete(entity: ProductCategoryEntity) = dao.delete(entity)

    // Read

    override fun get(id: Long): Flow<ProductCategoryEntity?> = dao.get(id).cancellable()

    override fun byName(name: String): Flow<ProductCategoryEntity?> = dao.byName(name).cancellable()

    override fun all(): Flow<ImmutableList<ProductCategoryEntity>> {
        return dao.all().cancellable().map { it.toImmutableList() }
    }

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

    override fun totalSpentByCategory(): Flow<ImmutableList<TotalSpentByCategory>> {
        return dao.totalSpentByCategory().cancellable().map { it.toImmutableList() }
    }

    override fun totalSpentByCategoryByMonth(
        year: Int,
        month: Int,
    ): Flow<ImmutableList<TotalSpentByCategory>> {
        val date: String = buildString {
            append(year)
            append("-")

            val monthStr: String =
                if (month < 10) {
                    "0$month"
                } else {
                    month.toString()
                }
            append(monthStr)
        }

        return dao.totalSpentByCategoryByMonth(date).cancellable().map { it.toImmutableList() }
    }
}
