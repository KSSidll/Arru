package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn

sealed class InsertProductProducerEntityUseCaseResult {
    class Success(val id: Long) : InsertProductProducerEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : InsertProductProducerEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()
}

sealed class UpdateProductProducerEntityUseCaseResult {
    object Success : UpdateProductProducerEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : UpdateProductProducerEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ProductProducerIdInvalid : Errors()

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()
}

sealed class MergeProductProducerEntityUseCaseResult {
    class Success(val mergedEntity: ProductProducerEntity) :
        MergeProductProducerEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : MergeProductProducerEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ProductProducerIdInvalid : Errors()

    data object MergeIntoIdInvalid : Errors()
}

sealed class DeleteProductProducerEntityUseCaseResult {
    object Success : DeleteProductProducerEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : DeleteProductProducerEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ProductProducerIdInvalid : Errors()

    data object DangerousDelete : Errors()
}

/** ENTITY */
class InsertProductProducerEntityUseCase(
    private val productProducerRepository: ProductProducerRepositorySource
) {
    suspend operator fun invoke(
        name: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): InsertProductProducerEntityUseCaseResult {
        val errors = mutableListOf<InsertProductProducerEntityUseCaseResult.Errors>()

        if (name.isNullOrBlank()) {
            errors.add(InsertProductProducerEntityUseCaseResult.NameNoValue)
        }

        name?.let {
            if (productProducerRepository.byName(it).first() != null) {
                errors.add(InsertProductProducerEntityUseCaseResult.NameDuplicateValue)
            }
        }

        if (errors.isNotEmpty()) {
            return InsertProductProducerEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity = ProductProducerEntity(name = name!!.trim())

        val id = productProducerRepository.insert(entity)

        return InsertProductProducerEntityUseCaseResult.Success(id)
    }
}

class UpdateProductProducerEntityUseCase(
    private val productProducerRepository: ProductProducerRepositorySource
) {
    suspend operator fun invoke(
        id: Long,
        name: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): UpdateProductProducerEntityUseCaseResult {
        val errors = mutableListOf<UpdateProductProducerEntityUseCaseResult.Errors>()

        if (productProducerRepository.get(id).first() == null) {
            errors.add(UpdateProductProducerEntityUseCaseResult.ProductProducerIdInvalid)
        }

        if (name.isNullOrBlank()) {
            errors.add(UpdateProductProducerEntityUseCaseResult.NameNoValue)
        }

        name?.let {
            val other = productProducerRepository.byName(it).first()
            if (other != null && other.id != id) {
                errors.add(UpdateProductProducerEntityUseCaseResult.NameDuplicateValue)
            }
        }

        if (errors.isNotEmpty()) {
            return UpdateProductProducerEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity = ProductProducerEntity(id = id, name = name!!.trim())

        productProducerRepository.update(entity)

        return UpdateProductProducerEntityUseCaseResult.Success
    }
}

class MergeProductProducerEntityUseCase(
    private val productRepository: ProductRepositorySource,
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        mergeIntoId: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): MergeProductProducerEntityUseCaseResult {
        val errors = mutableListOf<MergeProductProducerEntityUseCaseResult.Errors>()

        val entity = productProducerRepository.get(id).first()
        if (entity == null) {
            errors.add(MergeProductProducerEntityUseCaseResult.ProductProducerIdInvalid)
        }

        val mergeInto = productProducerRepository.get(mergeIntoId).first()
        if (mergeInto == null) {
            errors.add(MergeProductProducerEntityUseCaseResult.MergeIntoIdInvalid)
        }

        if (errors.isNotEmpty()) {
            return MergeProductProducerEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val products = productRepository.byProductProducer(id).first()

        val productsToUpdate = products.map { it.copy(productProducerEntityId = mergeIntoId) }

        productRepository.update(productsToUpdate)
        productProducerRepository.delete(entity!!)

        return MergeProductProducerEntityUseCaseResult.Success(mergeInto!!)
    }
}

class DeleteProductProducerEntityUseCase(
    private val productRepository: ProductRepositorySource,
    private val productVariantRepository: ProductVariantRepositorySource,
    private val itemRepository: ItemRepositorySource,
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        ignoreDangerous: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): DeleteProductProducerEntityUseCaseResult {
        val errors = mutableListOf<DeleteProductProducerEntityUseCaseResult.Errors>()

        val entity = productProducerRepository.get(id).first()
        if (entity == null) {
            errors.add(DeleteProductProducerEntityUseCaseResult.ProductProducerIdInvalid)
        }

        val products = productRepository.byProductProducer(id).first()
        val productVariants = productVariantRepository.byProductProducer(id).first()
        val items = itemRepository.byProductProducer(id).first()

        if (
            !ignoreDangerous &&
                (products.isNotEmpty() || productVariants.isNotEmpty() || items.isNotEmpty())
        ) {
            errors.add(DeleteProductProducerEntityUseCaseResult.DangerousDelete)
        }

        if (errors.isNotEmpty()) {
            return DeleteProductProducerEntityUseCaseResult.Error(errors.toImmutableList())
        }

        itemRepository.delete(items)
        productVariantRepository.delete(productVariants)
        productRepository.delete(products)
        productProducerRepository.delete(entity!!)

        return DeleteProductProducerEntityUseCaseResult.Success
    }
}

class GetProductProducerEntityUseCase(
    private val productProducerRepository: ProductProducerRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productProducerRepository.get(id).flowOn(dispatcher)
}

/** DOMAIN */
class GetTotalSpentForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productProducerRepository.totalSpent(id).flowOn(dispatcher)
}

class GetItemsForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productProducerRepository.itemsFor(id).flowOn(dispatcher)
}

/** DOMAIN CHART */
class GetTotalSpentByDayForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productProducerRepository.totalSpentByDay(id).flowOn(dispatcher)
}

class GetTotalSpentByWeekForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productProducerRepository.totalSpentByWeek(id).flowOn(dispatcher)
}

class GetTotalSpentByMonthForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productProducerRepository.totalSpentByMonth(id).flowOn(dispatcher)
}

class GetTotalSpentByYearForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productProducerRepository.totalSpentByYear(id).flowOn(dispatcher)
}
