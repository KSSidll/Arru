package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ProductCategoryEntityDao
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.paging.FullItemPagingSource
import com.kssidll.arru.data.repository.CategoryRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.CategoryRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.CategoryRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.CategoryRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class CategoryRepository(private val dao: ProductCategoryEntityDao): CategoryRepositorySource {
    // Create

    override suspend fun insert(name: String): InsertResult {
        val category = ProductCategoryEntity(name)

        if (category.validName()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byName(category.name)

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(category))
    }

    // Update

    override suspend fun update(
        categoryId: Long,
        name: String
    ): UpdateResult {
        if (dao.get(categoryId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        val category = ProductCategoryEntity(
            id = categoryId,
            name = name.trim(),
        )

        if (category.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byName(category.name)

        if (other != null && other.id != category.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(category)

        return UpdateResult.Success
    }

    override suspend fun merge(
        category: ProductCategoryEntity,
        mergingInto: ProductCategoryEntity
    ): MergeResult {
        if (dao.get(category.id) == null) {
            return MergeResult.Error(MergeResult.InvalidCategory)
        }

        if (dao.get(mergingInto.id) == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val products = dao.getProducts(category.id)
        products.forEach { it.categoryId = mergingInto.id }
        dao.updateProducts(products)

        dao.delete(category)

        return MergeResult.Success
    }

    // Delete

    override suspend fun delete(
        productCategoryId: Long,
        force: Boolean
    ): DeleteResult {
        val category =
            dao.get(productCategoryId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val products = dao.getProducts(productCategoryId)
        val productVariants = dao.getProductsVariants(productCategoryId)
        val items = dao.getItems(productCategoryId)

        if (!force && (products.isNotEmpty() || items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteItems(items)
            dao.deleteProductVariants(productVariants)
            dao.deleteProducts(products)
            dao.delete(category)
        }

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(categoryId: Long): ProductCategoryEntity? {
        return dao.get(categoryId)
    }

    override fun getFlow(categoryId: Long): Flow<Data<ProductCategoryEntity?>> {
        return dao.getFlow(categoryId)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<ProductCategoryEntity?>() }
    }

    override fun totalSpentFlow(category: ProductCategoryEntity): Flow<Data<Float?>> {
        return dao.totalSpentFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
            .map {
                Data.Loaded(
                    it?.toFloat()
                        ?.div(ItemEntity.PRICE_DIVISOR * ItemEntity.QUANTITY_DIVISOR)
                )
            }
            .onStart { Data.Loading<Long>() }
    }

    override fun totalSpentByDayFlow(category: ProductCategoryEntity): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByDayFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun totalSpentByWeekFlow(category: ProductCategoryEntity): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByWeekFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun totalSpentByMonthFlow(category: ProductCategoryEntity): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByMonthFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun totalSpentByYearFlow(category: ProductCategoryEntity): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByYearFlow(category.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun fullItemsPagedFlow(category: ProductCategoryEntity): Flow<PagingData<FullItem>> {
        return Pager(
            config = PagingConfig(pageSize = 3),
            initialKey = 0,
            pagingSourceFactory = {
                FullItemPagingSource(
                    query = { start, loadSize ->
                        dao.fullItems(
                            category.id,
                            loadSize,
                            start
                        )
                    },
                    itemsBefore = {
                        dao.countItemsBefore(
                            it,
                            category.id
                        )
                    },
                    itemsAfter = {
                        dao.countItemsAfter(
                            it,
                            category.id
                        )
                    },
                )
            }
        )
            .flow
    }

    override fun totalSpentByCategoryFlow(): Flow<ImmutableList<ItemSpentByCategory>> {
        return dao.totalSpentByCategoryFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByCategoryByMonthFlow(
        year: Int,
        month: Int
    ): Flow<ImmutableList<ItemSpentByCategory>> {
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

        return dao.totalSpentByCategoryByMonthFlow(date)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun allFlow(): Flow<Data<ImmutableList<ProductCategoryEntity>>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ProductCategoryEntity>>() }
    }

    override suspend fun totalCount(): Int {
        return dao.totalCount()
    }

    override suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<ProductCategoryEntity> {
        return dao.getPagedList(
            limit,
            offset
        ).toImmutableList()
    }
}