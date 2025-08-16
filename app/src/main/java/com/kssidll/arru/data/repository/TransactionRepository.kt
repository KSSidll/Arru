package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kssidll.arru.data.dao.TransactionEntityDao
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.data.TransactionSpentByTime
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.UpdateResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TransactionRepository(
    private val dao: TransactionEntityDao
): TransactionRepositorySource {
    // Create

    override suspend fun insert(
        date: Long,
        totalCost: Long,
        shopId: Long?,
        note: String?
    ): InsertResult {
        val transaction = TransactionEntity(
            date = date,
            totalCost = totalCost,
            shopEntityId = shopId,
            note = note
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
        note: String?
    ): UpdateResult {
        val transaction =
            dao.get(id).first() ?: return UpdateResult.Error(UpdateResult.InvalidId)

        val newTransaction = transaction.copy(
            date = date,
            totalCost = totalCost,
            shopEntityId = shopId,
            note = note
        )

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

    override suspend fun delete(
        id: Long,
        force: Boolean
    ): DeleteResult {
        val transaction =
            dao.get(id).first() ?: return DeleteResult.Error(DeleteResult.InvalidId)

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













    override suspend fun count(): Int {
        return dao.count()
    }

    override suspend fun countBefore(id: Long): Int {
        return dao.countBefore(id)
    }

    override suspend fun countAfter(id: Long): Int {
        return dao.countAfter(id)
    }

    override fun newest(): Flow<TransactionEntity?> {
        return dao.newest()
    }

    override fun totalSpentLong(): Flow<Long?> {
        return dao.totalSpent()
    }

    override fun totalSpent(): Flow<Float?> {
        return dao.totalSpent()
            .cancellable()
            .distinctUntilChanged()
            .map { it?.toFloat()?.div(TransactionEntity.COST_DIVISOR) }
    }

    override fun totalSpentByDay(): Flow<ImmutableList<TransactionSpentByTime>> {
        return dao.totalSpentByDay()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByWeek(): Flow<ImmutableList<TransactionSpentByTime>> {
        return dao.totalSpentByWeek()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByMonth(): Flow<ImmutableList<TransactionSpentByTime>> {
        return dao.totalSpentByMonth()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByYear(): Flow<ImmutableList<TransactionSpentByTime>> {
        return dao.totalSpentByYear()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override suspend fun transactionBasketsWithItems(
        startPosition: Int,
        count: Int
    ): ImmutableList<TransactionBasketWithItems> {
        return dao.transactionBasketsWithItems(
            startPosition,
            count
        ).toImmutableList()
    }

    override fun transactionBasketsPaged(): Flow<PagingData<TransactionBasketWithItems>> {
        return Pager(
            config = PagingConfig(
                pageSize = 12,
                enablePlaceholders = true,
                jumpThreshold = 24
            ),
            pagingSourceFactory = { dao.allPaged() }
        )
            .flow
            .map { pagingData ->
                pagingData.map { transaction ->
                    TransactionBasketWithItems(
                        id = transaction.id,
                        date = transaction.date,
                        shop = transaction.shopEntityId?.let { dao.shopById(it) },
                        totalCost = transaction.totalCost,
                        items = dao.fullItemsByTransactionBasketId(transaction.id),
                        note = transaction.note
                    )
                }
            }
    }

    override fun transactionBasketWithItems(transactionId: Long): Flow<TransactionBasketWithItems?> {
        return dao.transactionBasketWithItems(transactionId)
            .cancellable()
            .distinctUntilChanged()
    }
}