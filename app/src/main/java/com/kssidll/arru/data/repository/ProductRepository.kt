package com.kssidll.arru.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kssidll.arru.data.dao.ProductEntityDao
import com.kssidll.arru.data.data.FullItem
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductPriceByShopByTime
import com.kssidll.arru.data.paging.FullItemPagingSource
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.MergeResult
import com.kssidll.arru.data.repository.ProductRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class ProductRepository(private val dao: ProductEntityDao): ProductRepositorySource {
    // Create

    override suspend fun insert(
        name: String,
        categoryId: Long,
        producerId: Long?
    ): InsertResult {
        val entity = ProductEntity(
            categoryId,
            producerId,
            name
        )

        if (categoryId == ProductEntity.INVALID_CATEGORY_ID || dao.categoryById(categoryId) == null) {
            return InsertResult.Error(InsertResult.InvalidCategoryId)
        }

        if (producerId != null && dao.producerById(producerId) == null) {
            return InsertResult.Error(InsertResult.InvalidProducerId)
        }

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
        productId: Long,
        name: String,
        categoryId: Long,
        producerId: Long?
    ): UpdateResult {
        if (dao.get(productId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        if (dao.categoryById(categoryId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidCategoryId)
        }

        if (producerId != null && dao.producerById(producerId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidProducerId)
        }

        val entity = ProductEntity(
            id = productId,
            name = name.trim(),
            categoryId = categoryId,
            producerId = producerId
        )

        if (entity.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byName(entity.name)

        if (other != null && other.id != entity.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(entity)

        return UpdateResult.Success
    }

    override suspend fun merge(
        entity: ProductEntity,
        mergingInto: ProductEntity
    ): MergeResult {
        if (dao.get(entity.id) == null) {
            return MergeResult.Error(MergeResult.InvalidProduct)
        }

        if (dao.get(mergingInto.id) == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val items = dao.getItems(entity.id)
        val variants = dao.variants(entity.id)
        val mergingIntoVariantsNames = dao.variants(mergingInto.id)
            .map { it.name }

        val newVariants = variants.filterNot { it.name in mergingIntoVariantsNames }
        val duplicateVariants = variants.filter { it.name in mergingIntoVariantsNames }

        // update new variants
        newVariants.forEach { it.productId = mergingInto.id }
        dao.updateVariants(newVariants)

        items.forEach {
            it.productId = mergingInto.id

            // update id in case it's part of the duplicate variants
            if (it.variantId != null && it.variantId in duplicateVariants.map { variant -> variant.id }) {
                it.variantId = dao.variantByName(
                    it.productId,
                    dao.variantById(it.variantId!!)!!.name
                )!!.id
            }
        }
        dao.updateItems(items)

        dao.deleteVariants(duplicateVariants)
        dao.delete(entity)

        return MergeResult.Success
    }

    // Delete

    override suspend fun delete(
        productId: Long,
        force: Boolean
    ): DeleteResult {
        val product = dao.get(productId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val variants = dao.variants(productId)
        val items = dao.getItems(productId)

        if (!force && (variants.isNotEmpty() || items.isNotEmpty())) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteItems(items)
            dao.deleteVariants(variants)
            dao.delete(product)
        }

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(productId: Long): ProductEntity? {
        return dao.get(productId)
    }

    override fun getFlow(productId: Long): Flow<Data<ProductEntity?>> {
        return dao.getFlow(productId)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<ProductEntity?>() }
    }

    override fun totalSpentFlow(entity: ProductEntity): Flow<Data<Float?>> {
        return dao.totalSpentFlow(entity.id)
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

    override fun totalSpentByDayFlow(entity: ProductEntity): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByDayFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun totalSpentByWeekFlow(entity: ProductEntity): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByWeekFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun totalSpentByMonthFlow(entity: ProductEntity): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByMonthFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun totalSpentByYearFlow(entity: ProductEntity): Flow<Data<ImmutableList<ItemSpentByTime>>> {
        return dao.totalSpentByYearFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ItemSpentByTime>>() }
    }

    override fun fullItemsPagedFlow(entity: ProductEntity): Flow<PagingData<FullItem>> {
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

    override suspend fun newestItem(entity: ProductEntity): ItemEntity? {
        return dao.newestItem(entity.id)
    }

    override fun averagePriceByVariantByShopByMonthFlow(entity: ProductEntity): Flow<Data<ImmutableList<ProductPriceByShopByTime>>> {
        return dao.averagePriceByVariantByShopByMonthFlow(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ProductPriceByShopByTime>>() }
    }

    override fun allFlow(): Flow<Data<ImmutableList<ProductEntity>>> {
        return dao.allFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ProductEntity>>() }
    }

    override suspend fun totalCount(): Int {
        return dao.totalCount()
    }

    override suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<ProductEntity> {
        return dao.getPagedList(
            limit,
            offset
        ).toImmutableList()
    }
}