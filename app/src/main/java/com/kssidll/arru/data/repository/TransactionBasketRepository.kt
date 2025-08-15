package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.kssidll.arru.data.dao.TransactionEntityDao
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.data.TransactionSpentByTime
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class TransactionBasketRepository(
    private val dao: TransactionEntityDao
): TransactionBasketRepositorySource {
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
            shopId = shopId,
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
        transactionId: Long,
        date: Long,
        totalCost: Long,
        shopId: Long?,
        note: String?
    ): UpdateResult {
        val transaction =
            dao.get(transactionId) ?: return UpdateResult.Error(UpdateResult.InvalidId)

        val newTransaction = transaction.copy(
            date = date,
            totalCost = totalCost,
            shopId = shopId,
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
        transactionId: Long,
        force: Boolean
    ): DeleteResult {
        val transaction =
            dao.get(transactionId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val items = dao.itemsByTransactionBasketId(transactionId)

        if (!force && (items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteItems(items)
            dao.delete(transaction)
        }

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(transactionBasketId: Long): TransactionEntity? {
        return dao.get(transactionBasketId)
    }

    override suspend fun newest(): TransactionEntity? {
        return dao.newest()
    }

    override suspend fun count(): Int {
        return dao.count()
    }

    override suspend fun countBefore(transactionBasketId: Long): Int {
        return dao.countBefore(transactionBasketId)
    }

    override suspend fun countAfter(transactionBasketId: Long): Int {
        return dao.countAfter(transactionBasketId)
    }

    override suspend fun totalSpentLong(): Data<Long?> {
        return Data.Loaded(dao.totalSpent())
    }

    override fun totalSpentFlow(): Flow<Float?> {
        return dao.totalSpentFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { it?.toFloat()?.div(TransactionEntity.COST_DIVISOR) }
    }

    override fun totalSpentByDayFlow(): Flow<ImmutableList<TransactionSpentByTime>> {
        return dao.totalSpentByDayFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByWeekFlow(): Flow<ImmutableList<TransactionSpentByTime>> {
        return dao.totalSpentByWeekFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByMonthFlow(): Flow<ImmutableList<TransactionSpentByTime>> {
        return dao.totalSpentByMonthFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByYearFlow(): Flow<ImmutableList<TransactionSpentByTime>> {
        return dao.totalSpentByYearFlow()
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

    override fun transactionBasketsPagedFlow(): Flow<PagingData<TransactionBasketWithItems>> {
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
                        shop = transaction.shopId?.let { dao.shopById(it) },
                        totalCost = transaction.totalCost,
                        items = dao.fullItemsByTransactionBasketId(transaction.id),
                        note = transaction.note
                    )
                }
            }
    }

    override fun transactionBasketWithItemsFlow(transactionId: Long): Flow<Data<TransactionBasketWithItems?>> {
        return dao.transactionBasketWithItems(transactionId)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<TransactionBasketWithItems>() }
    }

    override suspend fun totalCount(): Int {
        return dao.totalCount()
    }

    override suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<TransactionEntity> {
        return dao.getPagedList(
            limit,
            offset
        ).toImmutableList()
    }
}