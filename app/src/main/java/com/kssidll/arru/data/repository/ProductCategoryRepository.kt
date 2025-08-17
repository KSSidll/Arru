package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ProductCategoryEntityDao
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource.Companion.UpdateResult
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ProductCategoryRepository(private val dao: ProductCategoryEntityDao): ProductCategoryRepositorySource {
    // Create

    override suspend fun insert(name: String): InsertResult {
        val category = ProductCategoryEntity(name)

        if (category.validName()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byName(category.name).first()

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(category))
    }

    // Update

    override suspend fun update(
        id: Long,
        name: String
    ): UpdateResult {
        if (dao.get(id).first() == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        val category = ProductCategoryEntity(
            id = id,
            name = name.trim(),
        )

        if (category.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byName(category.name).first()

        if (other != null && other.id != category.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(category)

        return UpdateResult.Success
    }

    override suspend fun merge(
        entity: ProductCategoryEntity,
        mergingInto: ProductCategoryEntity
    ): MergeResult {
        if (dao.get(entity.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidCategory)
        }

        if (dao.get(mergingInto.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val products = dao.getProducts(entity.id)
        products.forEach { it.productCategoryEntityId = mergingInto.id }
        dao.updateProducts(products)

        dao.delete(entity)

        return MergeResult.Success
    }

    // Delete

    override suspend fun delete(
        id: Long,
        force: Boolean
    ): DeleteResult {
        val category =
            dao.get(id).first() ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val products = dao.getProducts(id)
        val productVariants = dao.getProductsVariants(id)
        val items = dao.getItems(id)

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

    override fun get(id: Long): Flow<ProductCategoryEntity?> = dao.get(id).cancellable()

    override fun totalSpent(id: Long): Flow<Float?> = dao.totalSpent(id).cancellable()
        .map { it?.toFloat()?.div(ItemEntity.PRICE_DIVISOR * ItemEntity.QUANTITY_DIVISOR) }

    override fun itemsFor(id: Long): Flow<PagingData<Item>> =
        Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { dao.itemsFor(id) }
        ).flow.cancellable()

    override fun totalSpentByDay(id: Long): Flow<ImmutableList<ItemSpentChartData>> = dao.totalSpentByDay(id).cancellable()
        .map { it.toImmutableList() }

    override fun totalSpentByWeek(id: Long): Flow<ImmutableList<ItemSpentChartData>> = dao.totalSpentByWeek(id).cancellable()
        .map { it.toImmutableList() }

    override fun totalSpentByMonth(id: Long): Flow<ImmutableList<ItemSpentChartData>> = dao.totalSpentByMonth(id).cancellable()
        .map { it.toImmutableList() }

    override fun totalSpentByYear(id: Long): Flow<ImmutableList<ItemSpentChartData>> = dao.totalSpentByYear(id).cancellable()
        .map { it.toImmutableList() }






    override fun all(): Flow<ImmutableList<ProductCategoryEntity>> {
        return dao.all()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByCategory(): Flow<ImmutableList<ItemSpentByCategory>> {
        return dao.totalSpentByCategory()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByCategoryByMonth(
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

        return dao.totalSpentByCategoryByMonth(date)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }
}