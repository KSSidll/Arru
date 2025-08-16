package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ProductVariantEntityDao
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.repository.ProductVariantRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ProductVariantRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ProductVariantRepositorySource.Companion.UpdateResult
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ProductVariantRepository(private val dao: ProductVariantEntityDao): ProductVariantRepositorySource {
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
        ).first()

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
        id: Long,
        name: String
    ): UpdateResult {
        val variant = dao.get(id).first() ?: return UpdateResult.Error(UpdateResult.InvalidId)

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
        ).first()

        if (other != null && other.id != variant.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(variant)

        return UpdateResult.Success
    }

    override suspend fun update(
        id: Long,
        productId: Long?,
        name: String
    ): UpdateResult {
        if (dao.get(id).first() == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        if (productId != null && dao.getProduct(productId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidProductId)
        }

        val variant = ProductVariantEntity(
            id = id,
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
        ).first()

        if (other != null && other.id != variant.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(variant)

        return UpdateResult.Success
    }

    // Delete

    override suspend fun delete(
        id: Long,
        force: Boolean
    ): DeleteResult {
        val variant = dao.get(id).first() ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val items = dao.getItems(id)

        if (!force && items.isNotEmpty()) {
            return DeleteResult.Error(DeleteResult.DangerousDelete)
        } else {
            dao.deleteItems(items)
            dao.delete(variant)
        }

        return DeleteResult.Success
    }

    // Read

    override fun get(id: Long): Flow<ProductVariantEntity?> = dao.get(id).cancellable()









    override fun byProduct(productEntity: ProductEntity, showGlobal: Boolean): Flow<ImmutableList<ProductVariantEntity>> {
        return dao.byProduct(productEntity.id, showGlobal)
            .cancellable()
            .distinctUntilChanged()
            .map { it.toImmutableList() }
    }
}