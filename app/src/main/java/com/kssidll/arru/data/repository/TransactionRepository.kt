package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.TransactionEntityDao
import com.kssidll.arru.data.data.IntermediateTransaction
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TransactionRepository(private val dao: TransactionEntityDao) : TransactionRepositorySource {
    // Create

    override suspend fun insert(
        date: Long,
        totalCost: Long,
        shopId: Long?,
        note: String?,
    ): InsertResult {
        val transaction =
            TransactionEntity(
                date = date,
                totalCost = totalCost,
                shopEntityId = shopId,
                note = note,
            )

        if (!transaction.validDate()) {
            return InsertResult.Error(InsertResult.InvalidDate)
        }

        if (!transaction.validTotalCost()) {
            return InsertResult.Error(InsertResult.InvalidTotalCost)
        }

        if (shopId != null && dao.shopById(shopId) == null) {
            return InsertResult.Error(InsertResult.InvalidShopId)
        }

        return InsertResult.Success(dao.insert(transaction))
    }

    // Update

    override suspend fun update(
        id: Long,
        date: Long,
        totalCost: Long,
        shopId: Long?,
        note: String?,
    ): UpdateResult {
        val transaction = dao.get(id).first() ?: return UpdateResult.Error(UpdateResult.InvalidId)

        val newTransaction =
            transaction.copy(date = date, totalCost = totalCost, shopEntityId = shopId, note = note)

        if (!newTransaction.validDate()) {
            return UpdateResult.Error(UpdateResult.InvalidDate)
        }

        if (!newTransaction.validTotalCost()) {
            return UpdateResult.Error(UpdateResult.InvalidTotalCost)
        }

        if (shopId != null && dao.shopById(shopId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidShopId)
        }

        dao.update(newTransaction)

        return UpdateResult.Success
    }

    // Delete

    override suspend fun delete(id: Long, force: Boolean): DeleteResult {
        val transaction = dao.get(id).first() ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val items = dao.itemsByTransactionBasketId(id)

        if (!force && (items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteItems(items)
            dao.delete(transaction)
        }

        return DeleteResult.Success
    }

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
