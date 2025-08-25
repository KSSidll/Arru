package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn

sealed class InsertItemEntityUseCaseResult {
    class Success(val id: Long) : InsertItemEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : InsertItemEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object TransactionIdInvalid : Errors()

    data object ProductIdNoValue : Errors()

    data object ProductIdInvalid : Errors()

    data object ProductVariantIdInvalid : Errors()

    data object QuantityNoValue : Errors()

    data object QuantityInvalid : Errors()

    data object PriceNoValue : Errors()

    data object PriceInvalid : Errors()
}

sealed class UpdateItemEntityUseCaseResult {
    object Success : UpdateItemEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : UpdateItemEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ItemIdInvalid : Errors()

    data object TransactionIdInvalid : Errors()

    data object ProductIdNoValue : Errors()

    data object ProductIdInvalid : Errors()

    data object ProductVariantIdInvalid : Errors()

    data object QuantityNoValue : Errors()

    data object QuantityInvalid : Errors()

    data object PriceNoValue : Errors()

    data object PriceInvalid : Errors()
}

sealed class DeleteItemEntityUseCaseResult {
    object Success : DeleteItemEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : DeleteItemEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ItemIdInvalid : Errors()
}

/** ENTITY */
class InsertItemEntityUseCase(
    private val transactionRepository: TransactionRepositorySource,
    private val productRepository: ProductRepositorySource,
    private val productVariantRepository: ProductVariantRepositorySource,
    private val itemRepository: ItemRepositorySource,
) {
    suspend operator fun invoke(
        transactionEntityId: Long,
        productEntityId: Long?,
        productVariantEntityId: Long?,
        quantity: String?,
        price: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): InsertItemEntityUseCaseResult {
        val errors = mutableListOf<InsertItemEntityUseCaseResult.Errors>()

        if (transactionRepository.get(transactionEntityId).first() == null) {
            errors.add(InsertItemEntityUseCaseResult.TransactionIdInvalid)
        }

        if (productEntityId == null) {
            errors.add(InsertItemEntityUseCaseResult.ProductIdNoValue)
        } else if (productRepository.get(productEntityId).first() == null) {
            errors.add(InsertItemEntityUseCaseResult.ProductIdInvalid)
        }

        if (
            productVariantEntityId != null &&
                productVariantRepository.get(productVariantEntityId).first() == null
        ) {
            errors.add(InsertItemEntityUseCaseResult.ProductVariantIdInvalid)
        }

        var entityQuantity: Long? = null
        if (quantity.isNullOrBlank()) {
            errors.add(InsertItemEntityUseCaseResult.QuantityNoValue)
        } else {
            entityQuantity = ItemEntity.quantityFromString(quantity) ?: ItemEntity.INVALID_QUANTITY

            if (entityQuantity == ItemEntity.INVALID_QUANTITY) {
                errors.add(InsertItemEntityUseCaseResult.QuantityInvalid)
            }
        }

        var entityPrice: Long? = null
        if (price.isNullOrBlank()) {
            errors.add(InsertItemEntityUseCaseResult.PriceNoValue)
        } else {
            entityPrice = ItemEntity.priceFromString(price) ?: ItemEntity.INVALID_PRICE

            if (entityPrice == ItemEntity.INVALID_PRICE) {
                errors.add(InsertItemEntityUseCaseResult.PriceInvalid)
            }
        }

        if (errors.isNotEmpty()) {
            return InsertItemEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity =
            ItemEntity(
                transactionEntityId = transactionEntityId,
                productEntityId = productEntityId!!,
                productVariantEntityId = productVariantEntityId,
                quantity = entityQuantity!!,
                price = entityPrice!!,
            )

        val id = itemRepository.insert(entity)

        return InsertItemEntityUseCaseResult.Success(id)
    }
}

class UpdateItemEntityUseCase(
    private val transactionRepository: TransactionRepositorySource,
    private val productRepository: ProductRepositorySource,
    private val productVariantRepository: ProductVariantRepositorySource,
    private val itemRepository: ItemRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        transactionEntityId: Long,
        productEntityId: Long?,
        productVariantEntityId: Long?,
        quantity: String?,
        price: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): UpdateItemEntityUseCaseResult {
        val errors = mutableListOf<UpdateItemEntityUseCaseResult.Errors>()

        if (itemRepository.get(id).first() == null) {
            errors.add(UpdateItemEntityUseCaseResult.ItemIdInvalid)
        }

        if (transactionRepository.get(transactionEntityId).first() == null) {
            errors.add(UpdateItemEntityUseCaseResult.TransactionIdInvalid)
        }

        if (productEntityId == null) {
            errors.add(UpdateItemEntityUseCaseResult.ProductIdNoValue)
        } else if (productRepository.get(productEntityId).first() == null) {
            errors.add(UpdateItemEntityUseCaseResult.ProductIdInvalid)
        }

        if (
            productVariantEntityId != null &&
                productVariantRepository.get(productVariantEntityId).first() == null
        ) {
            errors.add(UpdateItemEntityUseCaseResult.ProductVariantIdInvalid)
        }

        var entityQuantity: Long? = null
        if (quantity.isNullOrBlank()) {
            errors.add(UpdateItemEntityUseCaseResult.QuantityNoValue)
        } else {
            entityQuantity = ItemEntity.quantityFromString(quantity) ?: ItemEntity.INVALID_QUANTITY

            if (entityQuantity == ItemEntity.INVALID_QUANTITY) {
                errors.add(UpdateItemEntityUseCaseResult.QuantityInvalid)
            }
        }

        var entityPrice: Long? = null
        if (price.isNullOrBlank()) {
            errors.add(UpdateItemEntityUseCaseResult.PriceNoValue)
        } else {
            entityPrice = ItemEntity.priceFromString(price) ?: ItemEntity.INVALID_PRICE

            if (entityPrice == ItemEntity.INVALID_PRICE) {
                errors.add(UpdateItemEntityUseCaseResult.PriceInvalid)
            }
        }

        if (errors.isNotEmpty()) {
            return UpdateItemEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity =
            ItemEntity(
                id = id,
                transactionEntityId = transactionEntityId,
                productEntityId = productEntityId!!,
                productVariantEntityId = productVariantEntityId,
                quantity = entityQuantity!!,
                price = entityPrice!!,
            )

        itemRepository.update(entity)

        return UpdateItemEntityUseCaseResult.Success
    }
}

class DeleteItemEntityUseCase(private val itemRepository: ItemRepositorySource) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): DeleteItemEntityUseCaseResult {
        val errors = mutableListOf<DeleteItemEntityUseCaseResult.Errors>()

        val entity = itemRepository.get(id).first()

        if (entity == null) {
            errors.add(DeleteItemEntityUseCaseResult.ItemIdInvalid)
        }

        if (errors.isNotEmpty()) {
            return DeleteItemEntityUseCaseResult.Error(errors.toImmutableList())
        }

        itemRepository.delete(entity!!)

        return DeleteItemEntityUseCaseResult.Success
    }
}

class GetItemEntityUseCase(private val itemRepository: ItemRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        itemRepository.get(id).flowOn(dispatcher)
}

class GetNewestItemEntityUseCase(private val itemRepository: ItemRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        itemRepository.newest().flowOn(dispatcher)
}

class GetNewestItemEntityByProductUseCase(private val itemRepository: ItemRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        itemRepository.newestByProduct(id).flowOn(dispatcher)
}

/** DOMAIN */
