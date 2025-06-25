package com.kssidll.arru.data.repository

import com.kssidll.arru.data.dao.ItemDao
import com.kssidll.arru.data.data.Item
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.InsertResult
import com.kssidll.arru.data.repository.ItemRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Data
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

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
            transactionBasketId = transactionId,
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

    override fun newestFlow(): Flow<Data<Item?>> {
        return dao.newestFlow()
            .cancellable()
            .distinctUntilChanged()
            .map { Data.Loaded(it) }
            .onStart { Data.Loading<Item>() }
    }

    override suspend fun totalCount(): Int {
        return dao.totalCount()
    }

    override suspend fun getPagedList(
        limit: Int,
        offset: Int
    ): ImmutableList<Item> {
        return dao.getPagedList(
            limit,
            offset
        ).toImmutableList()
    }

    override suspend fun getByTransaction(transactionId: Long): ImmutableList<Item> {
        return dao.getByTransaction(transactionId).toImmutableList()
    }
}