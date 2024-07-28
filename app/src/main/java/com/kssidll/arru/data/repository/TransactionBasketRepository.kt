package com.kssidll.arru.data.repository

import androidx.paging.PagingData
import com.kssidll.arru.data.dao.TransactionDao
import com.kssidll.arru.data.data.Transaction
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.data.TransactionSpentByTime
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class TransactionBasketRepository(private val dao: TransactionDao): TransactionBasketRepositorySource {
    // Create

    override suspend fun insert(
        date: Long,
        totalCost: Long,
        shopId: Long?
    ): InsertResult {
/*        val transaction = TransactionEntity(
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

        return InsertResult.Success(dao.insert(transaction))*/

        return InsertResult.Success(0)
    }

    // Update

    override suspend fun update(
        transactionId: Long,
        date: Long,
        totalCost: Long,
        shopId: Long?
    ): UpdateResult {
/*        val transaction =
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

        return UpdateResult.Success*/

        return UpdateResult.Success
    }

    // Delete

    override suspend fun delete(
        transactionId: Long,
        force: Boolean
    ): DeleteResult {
/*        val transaction =
            dao.get(transactionId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val items = dao.itemsByTransactionEntityId(transactionId)

        if (!force && (items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteItems(items)
            dao.delete(transaction)
        }

        return DeleteResult.Success*/

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(transactionBasketId: Long): TransactionEntity? {
/*        return dao.get(transactionBasketId)*/
return null
    }

    override suspend fun newest(): TransactionEntity? {
/*        return dao.newest()*/
return null
    }

    override suspend fun count(): Int {
/*        return dao.count()*/
return 0
    }

    override suspend fun countBefore(transactionBasketId: Long): Int {
/*        return dao.countBefore(transactionBasketId)*/
return 0
    }

    override suspend fun countAfter(transactionBasketId: Long): Int {
/*        return dao.countAfter(transactionBasketId)*/
return 0
    }

    override suspend fun totalSpentLong(): Data<Long?> {
//        return Data.Loaded(dao.totalSpent())
        return Data.Loading()
    }

    override fun totalSpentFlow(): Flow<Data<Float?>> {
//        return dao.totalSpentFlow()
//            .cancellable()
//            .distinctUntilChanged()
//            .map {
//                Data.Loaded(
//                    it?.toFloat()
//                        ?.div(TransactionEntity.COST_DIVISOR)
//                )
//            }
//            .onStart { Data.Loading<Long>() }
        return flowOf()
    }

    override fun totalSpentByDayFlow(): Flow<Data<List<TransactionSpentByTime>>> {
//        return dao.totalSpentByDayFlow()
//            .cancellable()
//            .distinctUntilChanged()
//            .map { Data.Loaded(it) }
//            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
        return flowOf()
    }

    override fun totalSpentByWeekFlow(): Flow<Data<List<TransactionSpentByTime>>> {
//        return dao.totalSpentByWeekFlow()
//            .cancellable()
//            .distinctUntilChanged()
//            .map { Data.Loaded(it) }
//            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
        return flowOf()
    }

    override fun totalSpentByMonthFlow(): Flow<Data<List<TransactionSpentByTime>>> {
//        return dao.totalSpentByMonthFlow()
//            .cancellable()
//            .distinctUntilChanged()
//            .map { Data.Loaded(it) }
//            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
        return flowOf()
    }

    override fun totalSpentByYearFlow(): Flow<Data<List<TransactionSpentByTime>>> {
//        return dao.totalSpentByYearFlow()
//            .cancellable()
//            .distinctUntilChanged()
//            .map { Data.Loaded(it) }
//            .onStart { Data.Loading<List<TransactionSpentByTime>>() }
        return flowOf()
    }

    override suspend fun transactionBasketsWithItems(
        startPosition: Int,
        count: Int
    ): List<Transaction> {
//        return dao.transactionEntitiesWithItems(
//            startPosition,
//            count
//        )
        return listOf()
    }

    override fun transactionBasketsPagedFlow(): Flow<PagingData<Transaction>> {
//        return Pager(
//            config = PagingConfig(pageSize = 3),
//            initialKey = 0,
//            pagingSourceFactory = { TransactionBasketWithItemsPagingSource(this) }
//        )
//            .flow
        return flowOf()
    }

    override fun transactionBasketWithItemsFlow(transactionId: Long): Flow<Data<Transaction?>> {
//        return dao.transactionEntityWithItems(transactionId)
//            .cancellable()
//            .distinctUntilChanged()
//            .map { Data.Loaded(it) }
//            .onStart { Data.Loading<Transaction>() }
        return flowOf()
    }
}