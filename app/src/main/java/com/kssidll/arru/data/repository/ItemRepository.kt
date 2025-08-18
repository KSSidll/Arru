package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ItemEntityDao
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.UpdateResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.first

class ItemRepository(private val dao: ItemEntityDao) : ItemRepositorySource {
    // Create

    override suspend fun insert(
        transactionId: Long,
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long,
    ): InsertResult {
        val entity =
            ItemEntity(
                transactionEntityId = transactionId,
                productEntityId = productId,
                productVariantEntityId = variantId,
                quantity = quantity,
                price = price,
            )

        if (dao.getTransactionBasket(transactionId) == null) {
            return InsertResult.Error(InsertResult.InvalidTransactionId)
        }

        if (dao.getProduct(productId) == null) {
            return InsertResult.Error(InsertResult.InvalidProductId)
        }

        if (variantId != null && dao.getVariant(variantId) == null) {
            return InsertResult.Error(InsertResult.InvalidVariantId)
        }

        if (entity.validQuantity().not()) {
            return InsertResult.Error(InsertResult.InvalidQuantity)
        }

        if (entity.validPrice().not()) {
            return InsertResult.Error(InsertResult.InvalidPrice)
        }

        val itemId = dao.insert(entity)

        return InsertResult.Success(itemId)
    }

    // Update

    override suspend fun update(
        id: Long,
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long,
    ): UpdateResult {
        val item = dao.get(id).first() ?: return UpdateResult.Error(UpdateResult.InvalidId)

        item.productEntityId = productId
        item.productVariantEntityId = variantId
        item.quantity = quantity
        item.price = price

        if (dao.getProduct(productId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidProductId)
        }

        if (variantId != null && dao.getVariant(variantId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidVariantId)
        }

        if (item.validQuantity().not()) {
            return UpdateResult.Error(UpdateResult.InvalidQuantity)
        }

        if (item.validPrice().not()) {
            return UpdateResult.Error(UpdateResult.InvalidPrice)
        }

        dao.update(item)

        return UpdateResult.Success
    }

    // Delete

    override suspend fun delete(id: Long): DeleteResult {
        val item = dao.get(id).first() ?: return DeleteResult.Error(DeleteResult.InvalidId)

        dao.delete(item)

        return DeleteResult.Success
    }

    // Read

    override fun get(id: Long): Flow<ItemEntity?> = dao.get(id).cancellable()

    override fun newest(): Flow<ItemEntity?> = dao.newest().cancellable()
}
