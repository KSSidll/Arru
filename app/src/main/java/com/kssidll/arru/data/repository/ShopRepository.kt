package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ShopEntityDao
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.data.paging.FullItemPagingSource
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ShopRepositorySource.Companion.UpdateResult
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.TransactionSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ShopRepository(private val dao: ShopEntityDao): ShopRepositorySource {
    // Create

    override suspend fun insert(name: String): InsertResult {
        val entity = ShopEntity(name)

        if (entity.validName()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byName(entity.name).first()

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(entity))
    }

    // Update

    override suspend fun update(
        id: Long,
        name: String
    ): UpdateResult {
        if (dao.get(id).first() == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        val shop = ShopEntity(
            id = id,
            name = name.trim()
        )

        if (shop.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byName(shop.name).first()

        if (other != null && other.id != shop.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(shop)

        return UpdateResult.Success
    }

    override suspend fun merge(
        entity: ShopEntity,
        mergingInto: ShopEntity
    ): MergeResult {
        if (dao.get(entity.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidShop)
        }

        if (dao.get(mergingInto.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val transactionBaskets = dao.getTransactionBaskets(entity.id)

        val newTransactionBaskets = transactionBaskets.map {
            it.copy(
                shopEntityId = mergingInto.id
            )
        }

        dao.updateTransactionBaskets(newTransactionBaskets)

        dao.delete(entity)

        return MergeResult.Success
    }

    // Delete

    override suspend fun delete(
        id: Long,
        force: Boolean
    ): DeleteResult {
        val shop = dao.get(id).first() ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val transactionBaskets = dao.getTransactionBaskets(id)
        val items = dao.getItems(id)

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

    override fun get(id: Long): Flow<ShopEntity?> = dao.get(id).cancellable()

    override fun totalSpent(id: Long): Flow<Float?> = dao.totalSpent(id).cancellable()
        .map { it?.toFloat()?.div(TransactionEntity.COST_DIVISOR) }

    override fun itemsFor(id: Long): Flow<PagingData<Item>> =
        Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { dao.itemsFor(id) }
        ).flow.cancellable()

    override fun totalSpentByDay(id: Long): Flow<ImmutableList<TransactionSpentChartData>> = dao.totalSpentByDay(id).cancellable()
        .map { it.toImmutableList() }

    override fun totalSpentByWeek(id: Long): Flow<ImmutableList<TransactionSpentChartData>> = dao.totalSpentByWeek(id).cancellable()
        .map { it.toImmutableList() }

    override fun totalSpentByMonth(id: Long): Flow<ImmutableList<TransactionSpentChartData>> = dao.totalSpentByMonth(id).cancellable()
        .map { it.toImmutableList() }

    override fun totalSpentByYear(id: Long): Flow<ImmutableList<TransactionSpentChartData>> = dao.totalSpentByYear(id).cancellable()
        .map { it.toImmutableList() }









    override fun fullItemsPaged(entity: ShopEntity): Flow<PagingData<FullItem>> {
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

    override fun totalSpentByShop(): Flow<ImmutableList<TransactionTotalSpentByShop>> {
        return dao.totalSpentByShop()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByShopByMonth(
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

        return dao.totalSpentByShopByMonth(date)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun all(): Flow<ImmutableList<ShopEntity>> {
        return dao.all()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }
}