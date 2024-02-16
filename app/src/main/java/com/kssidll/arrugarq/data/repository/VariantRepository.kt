package com.kssidll.arrugarq.data.repository

import com.kssidll.arrugarq.data.dao.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.VariantRepositorySource.Companion.DeleteResult
import com.kssidll.arrugarq.data.repository.VariantRepositorySource.Companion.InsertResult
import com.kssidll.arrugarq.data.repository.VariantRepositorySource.Companion.UpdateResult
import kotlinx.coroutines.flow.*

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

        if (other != null) {
            if (other.id == variant.id) {
                return UpdateResult.Success
            }

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

        if (other != null) {
            if (other.id == variant.id) {
                return UpdateResult.Success
            }

            return UpdateResult.Error(UpdateResult.DuplicateName)
        }

        dao.update(variant)

        return UpdateResult.Success
    }

    // Delete

    override suspend fun delete(variantId: Long): DeleteResult {
        val variant = dao.get(variantId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        dao.delete(variant)

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(variantId: Long): ProductVariant? {
        return dao.get(variantId)
    }

    override fun byProductFlow(product: Product): Flow<List<ProductVariant>> {
        return dao.byProductFlow(product.id)
            .cancellable()
            .distinctUntilChanged()
    }
}