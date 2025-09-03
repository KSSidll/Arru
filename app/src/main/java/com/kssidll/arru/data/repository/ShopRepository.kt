package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ShopEntityDao
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TotalSpentByShop
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map

class ShopRepository(private val dao: ShopEntityDao) : ShopRepositorySource {
    // Create

    override suspend fun insert(entity: ShopEntity): Long = dao.insert(entity)

    // Update

    override suspend fun update(entity: ShopEntity) = dao.update(entity)

    // Delete

    override suspend fun delete(entity: ShopEntity) = dao.delete(entity)

    // Read

    override fun get(id: Long): Flow<ShopEntity?> = dao.get(id).cancellable()

    override fun byName(name: String): Flow<ShopEntity?> = dao.byName(name).cancellable()

    override fun all(): Flow<ImmutableList<ShopEntity>> =
        dao.all().cancellable().map { it.toImmutableList() }

    override fun totalSpent(id: Long): Flow<Float?> =
        dao.totalSpent(id).cancellable().map { it?.toFloat()?.div(TransactionEntity.COST_DIVISOR) }

    override fun itemsFor(id: Long): Flow<PagingData<Item>> =
        Pager(
                config = PagingConfig(pageSize = 8, enablePlaceholders = true),
                pagingSourceFactory = { dao.itemsFor(id) },
            )
            .flow
            .cancellable()

    override fun totalSpentByDay(id: Long): Flow<ImmutableList<TransactionSpentChartData>> =
        dao.totalSpentByDay(id).cancellable().map { it.toImmutableList() }

    override fun totalSpentByWeek(id: Long): Flow<ImmutableList<TransactionSpentChartData>> =
        dao.totalSpentByWeek(id).cancellable().map { it.toImmutableList() }

    override fun totalSpentByMonth(id: Long): Flow<ImmutableList<TransactionSpentChartData>> =
        dao.totalSpentByMonth(id).cancellable().map { it.toImmutableList() }

    override fun totalSpentByYear(id: Long): Flow<ImmutableList<TransactionSpentChartData>> =
        dao.totalSpentByYear(id).cancellable().map { it.toImmutableList() }

    override fun totalSpentByShop(): Flow<ImmutableList<TotalSpentByShop>> =
        dao.totalSpentByShop().cancellable().map { it.toImmutableList() }

    override fun totalSpentByShopByMonth(
        year: Int,
        month: Int,
    ): Flow<ImmutableList<TotalSpentByShop>> {
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

        return dao.totalSpentByShopByMonth(date).cancellable().map { it.toImmutableList() }
    }
}
