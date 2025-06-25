package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.VariantDao
import com.kssidll.arru.data.data.Product
import com.kssidll.arru.data.data.ProductVariant
import com.kssidll.arru.data.repository.VariantRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.VariantRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.VariantRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

class VariantRepository(private val dao: VariantDao): VariantRepositorySource {
    // Create

    override suspend fun insert(
        productId: Long,
        name: String
    ): InsertResult {
        val variant = ProductVariant(
            productId,
            name
        )

        if (dao.getProduct(productId) == null) {
            return InsertResult.Error(InsertResult.InvalidProductId)
        }

        if (variant.validName()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidName)
        }

        val other = dao.byProductAndName(
            productId,
            name
        )

        if (other != null) {
            return InsertResult.Error(InsertResult.DuplicateName)
        }

        return InsertResult.Success(dao.insert(variant))
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
            variant.productId,
            name
        )

        if (other != null && other.id != variant.id) {
            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(variant)

        return UpdateResult.Success
    }

    override suspend fun update(
        variantId: Long,
        productId: Long,
        name: String
    ): UpdateResult {
        if (dao.get(variantId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidId)
        }

        if (dao.getProduct(productId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidProductId)
        }

        val variant = ProductVariant(
            id = variantId,
            productId = productId,
            name = name.trim(),
        )

        if (variant.validName()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidName)
        }

        val other = dao.byProductAndName(
            productId,
            name
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

    override suspend fun get(variantId: Long): ProductVariant? {
        return dao.get(variantId)
    }

    override fun getFlow(variantId: Long): Flow<Data<ProductVariant?>> {
        return dao.getFlow(variantId)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<ProductVariant?>() }
    }

    override fun byProductFlow(product: Product): Flow<Data<ImmutableList<ProductVariant>>> {
        return dao.byProductFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it.toImmutableList()) }
            .onStart { Data.Loading<ImmutableList<ProductVariant>>() }
    }

    override suspend fun totalCount(): Int {
        return dao.totalCount()
    }

    override suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<ProductVariant> {
        return dao.getPagedList(
            limit,
            offset
        ).toImmutableList()
    }
}