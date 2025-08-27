package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn

sealed class InsertProductVariantEntityUseCaseResult {
    class Success(val id: Long) : InsertProductVariantEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : InsertProductVariantEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ProductIdInvalid : Errors()

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()
}

sealed class UpdateProductVariantEntityUseCaseResult {
    object Success : UpdateProductVariantEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : UpdateProductVariantEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ProductVariantIdInvalid : Errors()

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()
}

sealed class DeleteProductVariantEntityUseCaseResult {
    object Success : DeleteProductVariantEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : DeleteProductVariantEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ProductVariantIdInvalid : Errors()

    data object DangerousDelete : Errors()
}

/** ENTITY */
class InsertProductVariantEntityUseCase(
    private val itemRepository: ItemRepositorySource,
    private val productRepository: ProductRepositorySource,
    private val productVariantRepository: ProductVariantRepositorySource,
) {
    suspend operator fun invoke(
        name: String?,
        productId: Long?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): InsertProductVariantEntityUseCaseResult {
        val errors = mutableListOf<InsertProductVariantEntityUseCaseResult.Errors>()

        productId?.let {
            if (productRepository.get(it).first() == null) {
                errors.add(InsertProductVariantEntityUseCaseResult.ProductIdInvalid)
            }
        }

        if (name.isNullOrBlank()) {
            errors.add(InsertProductVariantEntityUseCaseResult.NameNoValue)
        }

        name?.let {
            val other =
                if (productId == null) {
                    // handle if global
                    productVariantRepository.allGlobal().first()
                } else {
                    // handle if local
                    productVariantRepository.byProduct(productId, true).first()
                }

            if (it in other.map { o -> o.name }) {
                errors.add(InsertProductVariantEntityUseCaseResult.NameDuplicateValue)
            }
        }

        if (errors.isNotEmpty()) {
            return InsertProductVariantEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity = ProductVariantEntity(name = name!!.trim(), productEntityId = productId)

        val id = productVariantRepository.insert(entity)

        // Migrate conflicting product variants to new one if a global variant
        if (productId == null) {
            val conflictingVariants =
                productVariantRepository.byName(name).first().filter { it.id != id }

            conflictingVariants.forEach { variant ->
                val items = itemRepository.byProductVariant(variant.id).first()

                val itemsToUpdate = items.map { it.copy(productVariantEntityId = id) }

                itemRepository.update(itemsToUpdate)
            }

            productVariantRepository.delete(conflictingVariants)
        }

        return InsertProductVariantEntityUseCaseResult.Success(id)
    }
}

class UpdateProductVariantEntityUseCase(
    private val itemRepository: ItemRepositorySource,
    private val productVariantRepository: ProductVariantRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        name: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): UpdateProductVariantEntityUseCaseResult {
        val errors = mutableListOf<UpdateProductVariantEntityUseCaseResult.Errors>()

        val oldEntity = productVariantRepository.get(id).first()
        val productId = oldEntity?.productEntityId
        if (oldEntity == null) {
            errors.add(UpdateProductVariantEntityUseCaseResult.ProductVariantIdInvalid)
        } else {
            name?.let {
                val other =
                    if (productId == null) {
                            // handle if global
                            productVariantRepository.allGlobal().first()
                        } else {
                            // handle if local
                            productVariantRepository.byProduct(productId, true).first()
                        }
                        .filter { o -> o.id != id }

                if (it in other.map { o -> o.name }) {
                    errors.add(UpdateProductVariantEntityUseCaseResult.NameDuplicateValue)
                }
            }
        }

        if (name.isNullOrBlank()) {
            errors.add(UpdateProductVariantEntityUseCaseResult.NameNoValue)
        }

        if (errors.isNotEmpty()) {
            return UpdateProductVariantEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity = oldEntity!!.copy(name = name!!.trim())

        productVariantRepository.update(entity)

        // Migrate conflicting product variants to updated one if a global variant
        if (productId == null) {
            val conflictingVariants =
                productVariantRepository.byName(name).first().filter { it.id != entity.id }

            conflictingVariants.forEach { variant ->
                val items = itemRepository.byProductVariant(variant.id).first()

                val itemsToUpdate = items.map { it.copy(productVariantEntityId = id) }

                itemRepository.update(itemsToUpdate)
            }

            productVariantRepository.delete(conflictingVariants)
        }

        return UpdateProductVariantEntityUseCaseResult.Success
    }
}

class DeleteProductVariantEntityUseCase(
    private val itemRepository: ItemRepositorySource,
    private val productVariantRepository: ProductVariantRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        ignoreDangerous: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): DeleteProductVariantEntityUseCaseResult {
        val errors = mutableListOf<DeleteProductVariantEntityUseCaseResult.Errors>()

        val entity = productVariantRepository.get(id).first()
        if (entity == null) {
            errors.add(DeleteProductVariantEntityUseCaseResult.ProductVariantIdInvalid)
        }

        val items = itemRepository.byProductVariant(id).first()

        if (!ignoreDangerous && items.isNotEmpty()) {
            errors.add(DeleteProductVariantEntityUseCaseResult.DangerousDelete)
        }

        if (errors.isNotEmpty()) {
            return DeleteProductVariantEntityUseCaseResult.Error(errors.toImmutableList())
        }

        itemRepository.delete(items)
        productVariantRepository.delete(entity!!)

        return DeleteProductVariantEntityUseCaseResult.Success
    }
}

class GetProductVariantEntityUseCase(
    private val productVariantRepository: ProductVariantRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productVariantRepository.get(id).flowOn(dispatcher)
}

class GetProductVariantEntityByProductUseCase(
    private val productVariantRepository: ProductVariantRepositorySource
) {
    operator fun invoke(
        id: Long,
        showGlobal: Boolean = true,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productVariantRepository.byProduct(id, showGlobal).flowOn(dispatcher)
}

/** DOMAIN */
