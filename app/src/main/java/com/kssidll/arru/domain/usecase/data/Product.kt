package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn

sealed class InsertProductEntityUseCaseResult {
    class Success(val id: Long) : InsertProductEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : InsertProductEntityUseCaseResult()

    sealed class Errors

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()

    data object ProductProducerIdInvalid : Errors()

    data object ProductCategoryNoValue : Errors()

    data object ProductCategoryIdInvalid : Errors()
}

sealed class UpdateProductEntityUseCaseResult {
    object Success : UpdateProductEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : UpdateProductEntityUseCaseResult()

    sealed class Errors

    data object ProductIdInvalid : Errors()

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()

    data object ProductProducerIdInvalid : Errors()

    data object ProductCategoryNoValue : Errors()

    data object ProductCategoryIdInvalid : Errors()
}

sealed class MergeProductEntityUseCaseResult {
    class Success(val mergedEntity: ProductEntity) : MergeProductEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : MergeProductEntityUseCaseResult()

    sealed class Errors

    data object ProductIdInvalid : Errors()

    data object MergeIntoIdInvalid : Errors()
}

sealed class DeleteProductEntityUseCaseResult {
    object Success : DeleteProductEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : DeleteProductEntityUseCaseResult()

    sealed class Errors

    data object ProductIdInvalid : Errors()

    data object DangerousDelete : Errors()
}

/** ENTITY */
class InsertProductEntityUseCase(
    private val productRepository: ProductRepositorySource,
    private val productProducerRepository: ProductProducerRepositorySource,
    private val productCategoryRepository: ProductCategoryRepositorySource,
) {
    suspend operator fun invoke(
        name: String?,
        productProducerEntityId: Long?,
        productCategoryEntityId: Long?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): InsertProductEntityUseCaseResult {
        val errors = mutableListOf<InsertProductEntityUseCaseResult.Errors>()

        if (name.isNullOrBlank()) {
            errors.add(InsertProductEntityUseCaseResult.NameNoValue)
        }

        name?.let {
            if (productRepository.byName(it).first() != null) {
                errors.add(InsertProductEntityUseCaseResult.NameDuplicateValue)
            }
        }

        if (
            productProducerEntityId != null &&
                productProducerRepository.get(productProducerEntityId).first() == null
        ) {
            errors.add(InsertProductEntityUseCaseResult.ProductProducerIdInvalid)
        }

        if (productCategoryEntityId == null) {
            errors.add(InsertProductEntityUseCaseResult.ProductCategoryNoValue)
        } else if (productCategoryRepository.get(productCategoryEntityId).first() == null) {
            errors.add(InsertProductEntityUseCaseResult.ProductCategoryIdInvalid)
        }

        if (errors.isNotEmpty()) {
            return InsertProductEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity =
            ProductEntity(
                name = name!!.trim(),
                productProducerEntityId = productProducerEntityId,
                productCategoryEntityId = productCategoryEntityId!!,
            )

        val id = productRepository.insert(entity)

        return InsertProductEntityUseCaseResult.Success(id)
    }
}

class UpdateProductEntityUseCase(
    private val productRepository: ProductRepositorySource,
    private val productProducerRepository: ProductProducerRepositorySource,
    private val productCategoryRepository: ProductCategoryRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        name: String?,
        productProducerEntityId: Long?,
        productCategoryEntityId: Long?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): UpdateProductEntityUseCaseResult {
        val errors = mutableListOf<UpdateProductEntityUseCaseResult.Errors>()

        if (productRepository.get(id).first() == null) {
            errors.add(UpdateProductEntityUseCaseResult.ProductIdInvalid)
        }

        if (name.isNullOrBlank()) {
            errors.add(UpdateProductEntityUseCaseResult.NameNoValue)
        }

        name?.let {
            val other = productRepository.byName(it).first()
            if (other != null && other.id != id) {
                errors.add(UpdateProductEntityUseCaseResult.NameDuplicateValue)
            }
        }

        if (
            productProducerEntityId != null &&
                productProducerRepository.get(productProducerEntityId).first() == null
        ) {
            errors.add(UpdateProductEntityUseCaseResult.ProductProducerIdInvalid)
        }

        if (productCategoryEntityId == null) {
            errors.add(UpdateProductEntityUseCaseResult.ProductCategoryNoValue)
        } else if (productCategoryRepository.get(productCategoryEntityId).first() == null) {
            errors.add(UpdateProductEntityUseCaseResult.ProductCategoryIdInvalid)
        }

        if (errors.isNotEmpty()) {
            return UpdateProductEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity =
            ProductEntity(
                id = id,
                name = name!!.trim(),
                productProducerEntityId = productProducerEntityId,
                productCategoryEntityId = productCategoryEntityId!!,
            )

        productRepository.update(entity)

        return UpdateProductEntityUseCaseResult.Success
    }
}

class MergeProductEntityUseCase(
    private val productRepository: ProductRepositorySource,
    private val productVariantRepository: ProductVariantRepositorySource,
    private val itemRepository: ItemRepositorySource,
    private val performAutomaticBackupIfEnabledUseCase: PerformAutomaticBackupIfEnabledUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        mergeIntoId: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): MergeProductEntityUseCaseResult {
        val errors = mutableListOf<MergeProductEntityUseCaseResult.Errors>()

        val entity = productRepository.get(id).first()
        if (entity == null) {
            errors.add(MergeProductEntityUseCaseResult.ProductIdInvalid)
        }

        val mergeInto = productRepository.get(mergeIntoId).first()
        if (entity == null) {
            errors.add(MergeProductEntityUseCaseResult.MergeIntoIdInvalid)
        }

        if (errors.isNotEmpty()) {
            return MergeProductEntityUseCaseResult.Error(errors.toImmutableList())
        }

        performAutomaticBackupIfEnabledUseCase()

        val productVariants = productVariantRepository.byProduct(id, false).first()
        val items = itemRepository.byProduct(id).first()

        val mergingIntoVariants = productVariantRepository.byProduct(mergeIntoId, false).first()
        val duplicateVariants =
            productVariants.filter { variant ->
                variant.name in mergingIntoVariants.map { it.name }
            }

        val productVariantsToUpdate =
            productVariants
                .filterNot { variant -> variant.name in mergingIntoVariants.map { it.name } }
                .map { it.copy(productEntityId = mergeIntoId) }

        val itemsToUpdate =
            items.map {
                if (it.productVariantEntityId in duplicateVariants.map { variant -> variant.id }) {
                    // Update variant if part of duplicates
                    it.copy(
                        productEntityId = mergeIntoId,
                        productVariantEntityId =
                            (productVariantsToUpdate + mergingIntoVariants)
                                .first { variant ->
                                    variant.name ==
                                        productVariants
                                            .first { original ->
                                                original.id == it.productVariantEntityId
                                            }
                                            .name
                                }
                                .id,
                    )
                } else {
                    it.copy(productEntityId = mergeIntoId)
                }
            }

        productVariantRepository.update(productVariantsToUpdate)
        itemRepository.update(itemsToUpdate)
        productVariantRepository.delete(duplicateVariants)
        productRepository.delete(entity!!)

        return MergeProductEntityUseCaseResult.Success(mergeInto!!)
    }
}

class DeleteProductEntityUseCase(
    private val productRepository: ProductRepositorySource,
    private val productVariantRepository: ProductVariantRepositorySource,
    private val itemRepository: ItemRepositorySource,
    private val performAutomaticBackupIfEnabledUseCase: PerformAutomaticBackupIfEnabledUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        ignoreDangerous: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): DeleteProductEntityUseCaseResult {
        val errors = mutableListOf<DeleteProductEntityUseCaseResult.Errors>()

        val entity = productRepository.get(id).first()
        if (entity == null) {
            errors.add(DeleteProductEntityUseCaseResult.ProductIdInvalid)
        }

        val productVariants = productVariantRepository.byProduct(id, false).first()
        val items = itemRepository.byProduct(id).first()
        if (!ignoreDangerous && (productVariants.isNotEmpty() || items.isNotEmpty())) {
            errors.add(DeleteProductEntityUseCaseResult.DangerousDelete)
        }

        if (errors.isNotEmpty()) {
            return DeleteProductEntityUseCaseResult.Error(errors.toImmutableList())
        }

        performAutomaticBackupIfEnabledUseCase()

        itemRepository.delete(items)
        productVariantRepository.delete(productVariants)
        productRepository.delete(entity!!)

        return DeleteProductEntityUseCaseResult.Success
    }
}

class GetProductEntityUseCase(private val productRepository: ProductRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.get(id).flowOn(dispatcher)
}

class GetAllProductEntityUseCase(private val productRepository: ProductRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.all().flowOn(dispatcher)
}

/** DOMAIN */
class GetTotalSpentForProductUseCase(private val productRepository: ProductRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.totalSpent(id).flowOn(dispatcher)
}

class GetItemsForProductUseCase(private val productRepository: ProductRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.itemsFor(id).flowOn(dispatcher)
}

/** DOMAIN CHART */
class GetTotalSpentByDayForProductUseCase(private val productRepository: ProductRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.totalSpentByDay(id).flowOn(dispatcher)
}

class GetTotalSpentByWeekForProductUseCase(private val productRepository: ProductRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.totalSpentByWeek(id).flowOn(dispatcher)
}

class GetTotalSpentByMonthForProductUseCase(
    private val productRepository: ProductRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.totalSpentByMonth(id).flowOn(dispatcher)
}

class GetTotalSpentByYearForProductUseCase(private val productRepository: ProductRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.totalSpentByYear(id).flowOn(dispatcher)
}

class GetAveragePriceByShopByVariantByProducerByDayForProductUseCase(
    private val productRepository: ProductRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.averagePriceByShopByVariantByProducerByDay(id).flowOn(dispatcher)
}
