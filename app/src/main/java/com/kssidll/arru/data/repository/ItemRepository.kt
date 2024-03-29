package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.*
import kotlinx.coroutines.flow.*

class ItemRepository(private val dao: ItemDao): ItemRepositorySource {
    // Create

    override suspend fun insert(
        transactionId: Long,
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long
    ): InsertResult {
        val item = Item(
            productId = productId,
            variantId = variantId,
            quantity = quantity,
            price = price
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

        if (item.validQuantity()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidQuantity)
        }

        if (item.validPrice()
                .not()
        ) {
            return InsertResult.Error(InsertResult.InvalidPrice)
        }

        val itemId = dao.insert(item)

        val transactionItem = TransactionBasketItem(
            transactionBasketId = transactionId,
            itemId = itemId
        )

        dao.insertTransactionItem(transactionItem)

        return InsertResult.Success(itemId)
    }

    // Update

    override suspend fun update(
        itemId: Long,
        productId: Long,
        variantId: Long?,
        quantity: Long,
        price: Long
    ): UpdateResult {
        val item = dao.get(itemId) ?: return UpdateResult.Error(UpdateResult.InvalidId)

        item.productId = productId
        item.variantId = variantId
        item.quantity = quantity
        item.price = price

        if (dao.getProduct(productId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidProductId)
        }

        if (variantId != null && dao.getVariant(variantId) == null) {
            return UpdateResult.Error(UpdateResult.InvalidVariantId)
        }

        if (item.validQuantity()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidQuantity)
        }

        if (item.validPrice()
                .not()
        ) {
            return UpdateResult.Error(UpdateResult.InvalidPrice)
        }

        dao.update(item)

        return UpdateResult.Success
    }

    // Delete

    override suspend fun delete(itemId: Long): DeleteResult {
        val item = dao.get(itemId) ?: return DeleteResult.Error(DeleteResult.InvalidId)

        val transactionBasketItems = dao.getTransactionBasketItems(itemId)

        dao.deleteTransactionBasketItems(transactionBasketItems)
        dao.delete(item)

        return DeleteResult.Success
    }

    // Read

    override suspend fun get(itemId: Long): Item? {
        return dao.get(itemId)
    }

    override suspend fun newest(): Item? {
        return dao.newest()
    }

    override suspend fun newestFlow(): Flow<Data<Item?>> {
        return dao.newestFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<Item>() }
    }
}