package com.kssidll.arrugarq.data.repository

import androidx.paging.*
import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.paging.*
import com.kssidll.arrugarq.data.repository.ShopRepositorySource.Companion.DeleteResult
import com.kssidll.arrugarq.data.repository.ShopRepositorySource.Companion.InsertResult
import com.kssidll.arrugarq.data.repository.ShopRepositorySource.Companion.MergeResult
import com.kssidll.arrugarq.data.repository.ShopRepositorySource.Companion.UpdateResult
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

        if (other != null) {
            if (other.id == shop.id) {
                return UpdateResult.Success
            }

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
        val transactionBasketItems = dao.getTransactionBasketItems(shopId)

        if (!force && transactionBaskets.isNotEmpty()) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteTransactionBasketItems(transactionBasketItems)
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

    override fun getFlow(shopId: Long): Flow<Shop?> {
        return dao.getFlow(shopId)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentFlow(shop: Shop): Flow<Long> {
        return dao.totalSpentFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByDayFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByDayFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByWeekFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByWeekFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByMonthFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByMonthFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByYearFlow(shop: Shop): Flow<List<ItemSpentByTime>> {
        return dao.totalSpentByYearFlow(shop.id)
            .cancellable()
            .distinctUntilChanged()
    }

    override fun fullItemsPagedFlow(shop: Shop): Flow<PagingData<FullItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            shop.id,
                            loadSize,
                            start
                        )
                    }
                )
            }
        )
            .flow
    }

    override fun totalSpentByShopFlow(): Flow<List<ItemSpentByShop>> {
        return dao.totalSpentByShopFlow()
            .cancellable()
            .distinctUntilChanged()
    }

    override fun totalSpentByShopByMonthFlow(
        year: Int,
        month: Int
    ): Flow<List<ItemSpentByShop>> {
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
    }

    override fun allFlow(): Flow<List<Shop>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
    }
}