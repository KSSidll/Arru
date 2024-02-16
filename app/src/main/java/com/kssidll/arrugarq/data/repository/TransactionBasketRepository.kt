package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.paging.*
import com.kssidll.arrugarq.data.repository.TransactionBasketRepositorySource.Companion.DeleteResult
import com.kssidll.arrugarq.data.repository.TransactionBasketRepositorySource.Companion.InsertResult
import com.kssidll.arrugarq.data.repository.TransactionBasketRepositorySource.Companion.ItemInsertResult
import com.kssidll.arrugarq.data.repository.TransactionBasketRepositorySource.Companion.UpdateResult
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

    override suspend fun insertTransactionItem(
        transactionBasketId: Long,
        itemId: Long
    ): ItemInsertResult {
        val transactionItem = TransactionBasketItem(
            transactionBasketId = transactionBasketId,
            itemId = itemId,
        )

        if (dao.get(transactionBasketId) == null) {
            return ItemInsertResult.Error(ItemInsertResult.InvalidTransactionId)
        }

        if (dao.itemById(itemId) == null) {
            return ItemInsertResult.Error(ItemInsertResult.InvalidItemId)
        }

        return ItemInsertResult.Success(dao.insertTransactionBasketItem(transactionItem))
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

        val transactionBasketItems = dao.transactionBasketItems(transactionId)
        val items = dao.itemsByTransactionBasketId(transactionId)

        if (!force && (items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteTransactionBasketItems(transactionBasketItems)
            dao.deleteItems(items)
            dao.delete(transaction)
        }

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(transactionBasketId: Long): TransactionBasket? {
        return dao.get(transactionBasketId)
    }

    override fun totalSpentFlow(): Flow<Long> {
        return dao.totalSpentFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByDayFlow(): Flow<List<TransactionSpentByTime>> {
        return dao.totalSpentByDayFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByWeekFlow(): Flow<List<TransactionSpentByTime>> {
        return dao.totalSpentByWeekFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByMonthFlow(): Flow<List<TransactionSpentByTime>> {
        return dao.totalSpentByMonthFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByYearFlow(): Flow<List<TransactionSpentByTime>> {
        return dao.totalSpentByYearFlow()
            .cancellable()
            .distinctUntilChanged()
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
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { TransactionBasketWithItemsPagingSource(this) }
        )
            .flow
    }

}