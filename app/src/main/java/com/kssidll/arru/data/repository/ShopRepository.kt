package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ShopEntityDao
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.data.TransactionTotalSpentByTime
import com.kssidll.arru.data.paging.FullItemPagingSource
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ShopRepository(private val dao: ShopEntityDao): ShopRepositorySource {
    // Create

    override suspend fun insert(name: String): InsertResult {
        val entity = ShopEntity(name)

        if (entity.validName()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byName(entity.name)

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(entity))
    }

    // Update

    override suspend fun update(
        shopId: Long,
        name: String
    ): UpdateResult {
        if (dao.get(shopId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        val shop = ShopEntity(
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
        shop: ShopEntity,
        mergingInto: ShopEntity
    ): MergeResult {
        if (dao.get(shop.id) == null) {
            return MergeResult.Error(MergeResult.InvalidShop)
        }

        if (dao.get(mergingInto.id) == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val transactionBaskets = dao.getTransactionBaskets(shop.id)

        val newTransactionBaskets = transactionBaskets.map {
            it.copy(
                shopEntityId = mergingInto.id
            )
        }

        dao.updateTransactionBaskets(newTransactionBaskets)

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

    override suspend fun get(shopId: Long): ShopEntity? {
        return dao.get(shopId)
    }

    override fun getFlow(shopId: Long): Flow<Data<ShopEntity?>> {
        return dao.getFlow(shopId)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<ShopEntity?>() }
    }

    override fun totalSpentFlow(entity: ShopEntity): Flow<Data<Float?>> {
        return dao.totalSpentFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map {
                Data.Loaded(
                    it?.toFloat()
                        ?.div(TransactionEntity.COST_DIVISOR)
                )
            }
            .onStart { Data.Loading<Long>() }
    }

    override fun totalSpentByDayFlow(entity: ShopEntity): Flow<Data<ImmutableList<TransactionTotalSpentByTime>>> {
        return dao.totalSpentByDayFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<TransactionTotalSpentByTime>>() }
    }

    override fun totalSpentByWeekFlow(entity: ShopEntity): Flow<Data<ImmutableList<TransactionTotalSpentByTime>>> {
        return dao.totalSpentByWeekFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<TransactionTotalSpentByTime>>() }
    }

    override fun totalSpentByMonthFlow(entity: ShopEntity): Flow<Data<ImmutableList<TransactionTotalSpentByTime>>> {
        return dao.totalSpentByMonthFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<TransactionTotalSpentByTime>>() }
    }

    override fun totalSpentByYearFlow(entity: ShopEntity): Flow<Data<ImmutableList<TransactionTotalSpentByTime>>> {
        return dao.totalSpentByYearFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<TransactionTotalSpentByTime>>() }
    }

    override fun fullItemsPagedFlow(entity: ShopEntity): Flow<PagingData<FullItem>> {
        return Pager(
            config = PagingConfig(pageSize = 3),
            initialKey = 0,
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            entity.id,
                            loadSize,
                            start
                        )
                    },
                    itemsBefore = {
                        dao.countItemsBefore(
                            it,
                            entity.id
                        )
                    },
                    itemsAfter = {
                        dao.countItemsAfter(
                            it,
                            entity.id
                        )
                    },
                )
            }
        )
            .flow
    }

    override fun totalSpentByShopFlow(): Flow<ImmutableList<TransactionTotalSpentByShop>> {
        return dao.totalSpentByShopFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByShopByMonthFlow(
        year: Int,
        month: Int
    ): Flow<ImmutableList<TransactionTotalSpentByShop>> {
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
            .map { it.toImmutableList() }
    }

    override fun allFlow(): Flow<Data<ImmutableList<ShopEntity>>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ShopEntity>>() }
    }

    override suspend fun totalCount(): Int {
        return dao.totalCount()
    }

    override suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<ShopEntity> {
        return dao.getPagedList(
            limit,
            offset
        ).toImmutableList()
    }
}