package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ShopDao
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.paging.FullItemPagingSource
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.coroutines.flow.*

class ShopRepository(private val dao: ShopDao): ShopRepositorySource {
    // Create

    override suspend fun insert(name: String): InsertResult {
        val shop = Shop(name)

        if (shop.validName()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byName(shop.name)

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(shop))
    }

    // Update

    override suspend fun update(
        shopId: Long,
        name: String
    ): UpdateResult {
        if (dao.get(shopId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        val shop = Shop(
            id = shopId,
            name = name.trim()
        )

        if (shop.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byName(shop.name)

        if (other != null && other.id != shop.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(shop)

        return UpdateResult.Success
    }

    override suspend fun merge(
        shop: Shop,
        mergingInto: Shop
    ): MergeResult {
        if (dao.get(shop.id) == null) {
            return MergeResult.Error(MergeResult.InvalidShop)
        }

        if (dao.get(mergingInto.id) == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val transactionBaskets = dao.getTransactionBaskets(shop.id)
        transactionBaskets.forEach { it.shopId = mergingInto.id }
        dao.updateTransactionBaskets(transactionBaskets)

        dao.delete(shop)

        return MergeResult.Success
    }

    // Delete

    override suspend fun delete(
        shopId: Long,
        force: Boolean
    ): DeleteResult {
        val shop = dao.get(shopId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val transactionBaskets = dao.getTransactionBaskets(shopId)
        val items = dao.getItems(shopId)

        if (!force && transactionBaskets.isNotEmpty()) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteItems(items)
            dao.deleteTransactionBaskets(transactionBaskets)
            dao.delete(shop)
        }

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(shopId: Long): Shop? {
        return dao.get(shopId)
    }

    override fun getFlow(shopId: Long): Flow<Data<Shop?>> {
        return dao.getFlow(shopId)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<Shop?>() }
    }

    override fun totalSpentFlow(shop: Shop): Flow<Data<Float?>> {
        return dao.totalSpentFlow(shop.id)
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

    override fun totalSpentByDayFlow(shop: Shop): Flow<Data<List<TransactionTotalSpentByTime>>> {
        return dao.totalSpentByDayFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<TransactionTotalSpentByTime>>() }
    }

    override fun totalSpentByWeekFlow(shop: Shop): Flow<Data<List<TransactionTotalSpentByTime>>> {
        return dao.totalSpentByWeekFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<TransactionTotalSpentByTime>>() }
    }

    override fun totalSpentByMonthFlow(shop: Shop): Flow<Data<List<TransactionTotalSpentByTime>>> {
        return dao.totalSpentByMonthFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<TransactionTotalSpentByTime>>() }
    }

    override fun totalSpentByYearFlow(shop: Shop): Flow<Data<List<TransactionTotalSpentByTime>>> {
        return dao.totalSpentByYearFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<TransactionTotalSpentByTime>>() }
    }

    override fun fullItemsPagedFlow(shop: Shop): Flow<PagingData<FullItem>> {
        return Pager(
            config = PagingConfig(pageSize = 3),
            initialKey = 0,
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            shop.id,
                            loadSize,
                            start
                        )
                    },
                    itemsBefore = {
                        dao.countItemsBefore(
                            it,
                            shop.id
                        )
                    },
                    itemsAfter = {
                        dao.countItemsAfter(
                            it,
                            shop.id
                        )
                    },
                )
            }
        )
            .flow
    }

    override fun totalSpentByShopFlow(): Flow<Data<List<TransactionTotalSpentByShop>>> {
        return dao.totalSpentByShopFlow()
            .map { Data.Loaded(it) }
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByShopByMonthFlow(
        year: Int,
        month: Int
    ): Flow<Data<List<TransactionTotalSpentByShop>>> {
        val date: String = buildString {
            append(year)
            append("-")

            val monthStr: String = if (month < 10) {
                "0$month"
            } else {
                month.toString()
            }
            append(monthStr)
        }

        return dao.totalSpentByShopByMonthFlow(date)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<TransactionTotalSpentByShop>>() }
    }

    override fun allFlow(): Flow<Data<List<Shop>>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<List<Shop>>() }
    }
}