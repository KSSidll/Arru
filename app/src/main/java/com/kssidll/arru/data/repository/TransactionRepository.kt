package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.TransactionEntityDao
import com.kssidll.arru.data.data.IntermediateTransaction
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.map

class TransactionRepository(private val dao: TransactionEntityDao) : TransactionRepositorySource {
    // Create

    override suspend fun insert(entity: TransactionEntity): Long = dao.insert(entity)

    // Update

    override suspend fun update(entity: TransactionEntity) = dao.update(entity)

    // Delete

    override suspend fun delete(entity: TransactionEntity) = dao.delete(entity)

    override suspend fun delete(entity: List<TransactionEntity>) = dao.delete(entity)

    // Read

    override fun get(id: Long): Flow<TransactionEntity?> = dao.get(id).cancellable()

    override fun totalSpent(): Flow<Float?> =
        dao.totalSpent().cancellable().map { it?.toFloat()?.div(TransactionEntity.COST_DIVISOR) }

    override fun intermediateFor(id: Long): Flow<IntermediateTransaction?> =
        dao.intermediateFor(id).cancellable()

    override fun intermediates(): Flow<PagingData<IntermediateTransaction>> =
        Pager(
                config = PagingConfig(pageSize = 8, enablePlaceholders = true),
                pagingSourceFactory = { dao.intermediates() },
            )
            .flow
            .cancellable()

    override fun totalSpentByDay(): Flow<ImmutableList<TransactionSpentChartData>> =
        dao.totalSpentByDay().cancellable().map { it.toImmutableList() }

    override fun totalSpentByWeek(): Flow<ImmutableList<TransactionSpentChartData>> =
        dao.totalSpentByWeek().cancellable().map { it.toImmutableList() }

    override fun totalSpentByMonth(): Flow<ImmutableList<TransactionSpentChartData>> =
        dao.totalSpentByMonth().cancellable().map { it.toImmutableList() }

    override fun totalSpentByYear(): Flow<ImmutableList<TransactionSpentChartData>> =
        dao.totalSpentByYear().cancellable().map { it.toImmutableList() }

    override fun newest(): Flow<TransactionEntity?> = dao.newest().cancellable()
}
