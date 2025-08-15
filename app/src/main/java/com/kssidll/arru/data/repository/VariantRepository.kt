package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ProductVariantEntityDao
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.repository.VariantRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.VariantRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.VariantRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class VariantRepository(private val dao: ProductVariantEntityDao): VariantRepositorySource {
    // Create

    override suspend fun insert(
        productId: Long?,
        name: String
    ): InsertResult {
        val variant = ProductVariantEntity(
            productId,
            name
        )

        if (productId != null && dao.getProduct(productId) == null) {
            return InsertResult.Error(InsertResult.InvalidProductId)
        }

        if (!variant.validName()) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byProductAndName(
            productId,
            name,
            true
        )

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        val newVariantId = dao.insert(variant)

        // handle change to global variant for local ones on global creation by name
        if (productId == null) {
            val others = dao.byName(name).first().filter { it.id != newVariantId }

            others.forEach { variant ->
                val othersItems = dao.getItems(variant.id)

                othersItems.forEach { it.productVariantEntityId = newVariantId }

                dao.updateItems(othersItems)
                dao.delete(variant)
            }
        }

        return InsertResult.Success(newVariantId)

    }

    // Update

    override suspend fun update(
        variantId: Long,
        name: String
    ): UpdateResult {
        val variant = dao.get(variantId) ?: return UpdateResult.Error(UpdateResult.InvalidId)

        variant.name = name.trim()

        if (variant.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byProductAndName(
            variant.productEntityId,
            name,
            true
        )

        if (other != null && other.id != variant.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(variant)

        return UpdateResult.Success
    }

    override suspend fun update(
        variantId: Long,
        productId: Long?,
        name: String
    ): UpdateResult {
        if (dao.get(variantId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        if (productId != null && dao.getProduct(productId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidProductId)
        }

        val variant = ProductVariantEntity(
            id = variantId,
            productEntityId = productId,
            name = name.trim(),
        )

        if (variant.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byProductAndName(
            productId,
            name,
            true
        )

        if (other != null && other.id != variant.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(variant)

        return UpdateResult.Success
    }

    // Delete

    override suspend fun delete(
        variantId: Long,
        force: Boolean
    ): DeleteResult {
        val variant = dao.get(variantId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val items = dao.getItems(variantId)

        if (!force && items.isNotEmpty()) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteItems(items)
            dao.delete(variant)
        }

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(variantId: Long): ProductVariantEntity? {
        return dao.get(variantId)
    }

    override fun getFlow(variantId: Long): Flow<Data<ProductVariantEntity?>> {
        return dao.getFlow(variantId)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<ProductVariantEntity?>() }
    }

    override fun byProductFlow(productEntity: ProductEntity, showGlobal: Boolean): Flow<Data<ImmutableList<ProductVariantEntity>>> {
        return dao.byProductFlow(productEntity.id, showGlobal)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ProductVariantEntity>>() }
    }

    override suspend fun totalCount(): Int {
        return dao.totalCount()
    }

    override suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<ProductVariantEntity> {
        return dao.getPagedList(
            limit,
            offset
        ).toImmutableList()
    }
}