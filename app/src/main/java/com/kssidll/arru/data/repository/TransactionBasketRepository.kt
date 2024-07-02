package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.TransactionBasketDao
import com.kssidll.arru.data.data.TransactionBasket
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.data.data.TransactionSpentByTime
import com.kssidll.arru.data.paging.TransactionBasketWithItemsPagingSource
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.coroutines.flow.*

class TransactionBasketRepository(private val dao: TransactionBasketDao): TransactionBasketRepositorySource {
    // Create

    override suspend fun insert(
        date: Long,
        totalCost: Long,
        shopId: Long?
    ): InsertResult {
        val transaction = TransactionBasket(
            date = date,
            totalCost = totalCost,
            shopId = shopId
        )

        if (transaction.validDate()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidDate)
        }

        if (transaction.validTotalCost()
                .not()
        ) {
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
        shopId: Long?
    ): UpdateResult {
        val transaction =
            dao.get(transactionId) ?: return UpdateResult.Error(UpdateResult.InvalidId)

        transaction.date = date
        transaction.totalCost = totalCost
        transaction.shopId = shopId

        if (transaction.validDate()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidDate)
        }

        if (transaction.validTotalCost()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidTotalCost)
        }

        if (shopId != null && dao.shopById(shopId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidShopId)
        }

        dao.update(transaction)

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

    override suspend fun get(transactionBasketId: Long): TransactionBasket? {
        return dao.get(transactionBasketId)
    }

    override suspend fun newest(): TransactionBasket? {
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

    override fun totalSpentFlow(): Flow<Data<Float?>> {
        return dao.totalSpentFlow()
            .cancellable()
            .distinctUntilChanged()
            .map {
                Data.Loaded(
                    it?.toFloat()
                        ?.div(TransactionBasket.COST_DIVISOR)
                )
            }
            .onStart { Data.Loading<Long>() }
    }

    override fun totalSpentByDayFlow(): Flow<Data<List<TransactionSpentByTime>>> {
        return dao.totalSpentByDayFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
    }

    override fun totalSpentByWeekFlow(): Flow<Data<List<TransactionSpentByTime>>> {
        return dao.totalSpentByWeekFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
    }

    override fun totalSpentByMonthFlow(): Flow<Data<List<TransactionSpentByTime>>> {
        return dao.totalSpentByMonthFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
    }

    override fun totalSpentByYearFlow(): Flow<Data<List<TransactionSpentByTime>>> {
        return dao.totalSpentByYearFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
    }

    override suspend fun transactionBasketsWithItems(
        startPosition: Int,
        count: Int
    ): List<TransactionBasketWithItems> {
        return dao.transactionBasketsWithItems(
            startPosition,
            count
        )
    }

    override fun transactionBasketsPagedFlow(): Flow<PagingData<TransactionBasketWithItems>> {
        return Pager(
            config = PagingConfig(pageSize = 3),
            initialKey = 0,
            pagingSourceFactory = { TransactionBasketWithItemsPagingSource(this) }
        )
            .flow
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
    ): List<TransactionBasket> {
        return dao.getPagedList(
            limit,
            offset
        )
    }
}