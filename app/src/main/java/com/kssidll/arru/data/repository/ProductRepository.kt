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
import com.kssidll.arru.data.view.Item
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

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

        val other = dao.byName(entity.name).first()

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(entity))
    }

    // Update

    override suspend fun update(
        id: Long,
        name: String,
        categoryId: Long,
        producerId: Long?
    ): UpdateResult {
        if (dao.get(id).first() == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        if (dao.categoryById(categoryId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidCategoryId)
        }

        if (producerId != null && dao.producerById(producerId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidProducerId)
        }

        val entity = ProductEntity(
            id = id,
            name = name.trim(),
            productCategoryEntityId = categoryId,
            productProducerEntityId = producerId
        )

        if (entity.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byName(entity.name).first()

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
        if (dao.get(entity.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidProduct)
        }

        if (dao.get(mergingInto.id).first() == null) {
            return MergeResult.Error(MergeResult.InvalidMergingInto)
        }

        val items = dao.getItems(entity.id)
        val variants = dao.variants(entity.id)
        val mergingIntoVariantsNames = dao.variants(mergingInto.id)
            .map { it.name }

        val newVariants = variants.filterNot { it.name in mergingIntoVariantsNames }
        val duplicateVariants = variants.filter { it.name in mergingIntoVariantsNames }

        // update new variants
        newVariants.forEach { it.productEntityId = mergingInto.id }
        dao.updateVariants(newVariants)

        items.forEach {
            it.productEntityId = mergingInto.id

            // update id in case it's part of the duplicate variants
            if (it.productVariantEntityId != null && it.productVariantEntityId in duplicateVariants.map { variant -> variant.id }) {
                it.productVariantEntityId = dao.variantByName(
                    it.productEntityId,
                    dao.variantById(it.productVariantEntityId!!)!!.name
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
        id: Long,
        force: Boolean
    ): DeleteResult {
        val product = dao.get(id).first() ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val variants = dao.variants(id)
        val items = dao.getItems(id)

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

    override fun get(id: Long): Flow<ProductEntity?> = dao.get(id).cancellable()

    override fun itemsFor(id: Long): Flow<PagingData<Item>> =
        Pager(
            config = PagingConfig(
                pageSize = 8,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { dao.itemsFor(id) }
        ).flow.cancellable()











    override fun totalSpent(entity: ProductEntity): Flow<Float?> {
        return dao.totalSpent(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map {
                it?.toFloat()?.div(ItemEntity.PRICE_DIVISOR * ItemEntity.QUANTITY_DIVISOR)
            }
    }

    override fun totalSpentByDay(entity: ProductEntity): Flow<ImmutableList<ItemSpentByTime>> {
        return dao.totalSpentByDay(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByWeek(entity: ProductEntity): Flow<ImmutableList<ItemSpentByTime>> {
        return dao.totalSpentByWeek(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByMonth(entity: ProductEntity): Flow<ImmutableList<ItemSpentByTime>> {
        return dao.totalSpentByMonth(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun totalSpentByYear(entity: ProductEntity): Flow<ImmutableList<ItemSpentByTime>> {
        return dao.totalSpentByYear(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun fullItemsPaged(entity: ProductEntity): Flow<PagingData<FullItem>> {
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

    override fun averagePriceByVariantByShopByMonth(entity: ProductEntity): Flow<ImmutableList<ProductPriceByShopByTime>> {
        return dao.averagePriceByVariantByShopByMonth(entity.id)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }

    override fun all(): Flow<ImmutableList<ProductEntity>> {
        return dao.all()
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }
}