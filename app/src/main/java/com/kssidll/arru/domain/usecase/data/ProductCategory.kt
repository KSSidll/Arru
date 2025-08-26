package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.data.ProductCategoryEntity
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn

sealed class InsertProductCategoryEntityUseCaseResult {
    class Success(val id: Long) : InsertProductCategoryEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : InsertProductCategoryEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()
}

sealed class UpdateProductCategoryEntityUseCaseResult {
    object Success : UpdateProductCategoryEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : UpdateProductCategoryEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ProductCategoryIdInvalid : Errors()

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()
}

sealed class MergeProductCategoryEntityUseCaseResult {
    class Success(val mergedEntity: ProductCategoryEntity) :
        MergeProductCategoryEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : MergeProductCategoryEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ProductCategoryIdInvalid : Errors()

    data object MergeIntoIdInvalid : Errors()
}

sealed class DeleteProductCategoryEntityUseCaseResult {
    object Success : DeleteProductCategoryEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : DeleteProductCategoryEntityUseCaseResult()

    fun isError(): Boolean = this is Error

    fun isNotError(): Boolean = !isError()

    sealed class Errors

    data object ProductCategoryIdInvalid : Errors()

    data object DangerousDelete : Errors()
}

/** ENTITY */
class InsertProductCategoryEntityUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    suspend operator fun invoke(
        name: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): InsertProductCategoryEntityUseCaseResult {
        val errors = mutableListOf<InsertProductCategoryEntityUseCaseResult.Errors>()

        if (name.isNullOrBlank()) {
            errors.add(InsertProductCategoryEntityUseCaseResult.NameNoValue)
        }

        name?.let {
            if (productCategoryRepository.byName(it).first() != null) {
                errors.add(InsertProductCategoryEntityUseCaseResult.NameDuplicateValue)
            }
        }

        if (errors.isNotEmpty()) {
            return InsertProductCategoryEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity = ProductCategoryEntity(name = name!!.trim())

        val id = productCategoryRepository.insert(entity)

        return InsertProductCategoryEntityUseCaseResult.Success(id)
    }
}

class UpdateProductCategoryEntityUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    suspend operator fun invoke(
        id: Long,
        name: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): UpdateProductCategoryEntityUseCaseResult {
        val errors = mutableListOf<UpdateProductCategoryEntityUseCaseResult.Errors>()

        if (productCategoryRepository.get(id).first() == null) {
            errors.add(UpdateProductCategoryEntityUseCaseResult.ProductCategoryIdInvalid)
        }

        if (name.isNullOrBlank()) {
            errors.add(UpdateProductCategoryEntityUseCaseResult.NameNoValue)
        }

        name?.let {
            if (productCategoryRepository.byName(it).first() != null) {
                errors.add(UpdateProductCategoryEntityUseCaseResult.NameDuplicateValue)
            }
        }

        if (errors.isNotEmpty()) {
            return UpdateProductCategoryEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity = ProductCategoryEntity(id = id, name = name!!.trim())

        productCategoryRepository.update(entity)

        return UpdateProductCategoryEntityUseCaseResult.Success
    }
}

class MergeProductCategoryEntityUseCase(
    private val productRepository: ProductRepositorySource,
    private val productCategoryRepository: ProductCategoryRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        mergeIntoId: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): MergeProductCategoryEntityUseCaseResult {
        val errors = mutableListOf<MergeProductCategoryEntityUseCaseResult.Errors>()

        val entity = productCategoryRepository.get(id).first()
        if (entity == null) {
            errors.add(MergeProductCategoryEntityUseCaseResult.ProductCategoryIdInvalid)
        }

        val mergeInto = productCategoryRepository.get(mergeIntoId).first()
        if (mergeInto == null) {
            errors.add(MergeProductCategoryEntityUseCaseResult.MergeIntoIdInvalid)
        }

        if (errors.isNotEmpty()) {
            return MergeProductCategoryEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val products = productRepository.byProductCategory(id).first()

        val productsToUpdate = products.map { it.copy(productCategoryEntityId = mergeIntoId) }

        productRepository.update(productsToUpdate)
        productCategoryRepository.delete(entity!!)

        return MergeProductCategoryEntityUseCaseResult.Success(mergeInto!!)
    }
}

class DeleteProductCategoryEntityUseCase(
    private val productRepository: ProductRepositorySource,
    private val productVariantRepository: ProductVariantRepositorySource,
    private val itemRepository: ItemRepositorySource,
    private val productCategoryRepository: ProductCategoryRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        ignoreDangerous: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): DeleteProductCategoryEntityUseCaseResult {
        val errors = mutableListOf<DeleteProductCategoryEntityUseCaseResult.Errors>()

        val entity = productCategoryRepository.get(id).first()
        if (entity == null) {
            errors.add(DeleteProductCategoryEntityUseCaseResult.ProductCategoryIdInvalid)
        }

        val products = productRepository.byProductCategory(id).first()
        val productVariants = productVariantRepository.byProductCategory(id).first()
        val items = itemRepository.byProductCategory(id).first()

        if (
            !ignoreDangerous &&
                (products.isNotEmpty() || productVariants.isNotEmpty() || items.isNotEmpty())
        ) {
            errors.add(DeleteProductCategoryEntityUseCaseResult.DangerousDelete)
        }

        if (errors.isNotEmpty()) {
            return DeleteProductCategoryEntityUseCaseResult.Error(errors.toImmutableList())
        }

        itemRepository.delete(items)
        productVariantRepository.delete(productVariants)
        productRepository.delete(products)
        productCategoryRepository.delete(entity!!)

        return DeleteProductCategoryEntityUseCaseResult.Success
    }
}

class GetProductCategoryEntityUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productCategoryRepository.get(id).flowOn(dispatcher)
}

/** DOMAIN */
class GetTotalSpentForProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productCategoryRepository.totalSpent(id).flowOn(dispatcher)
}

class GetItemsForProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productCategoryRepository.itemsFor(id).flowOn(dispatcher)
}

/** DOMAIN CHART */
class GetTotalSpentByDayForProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productCategoryRepository.totalSpentByDay(id).flowOn(dispatcher)
}

class GetTotalSpentByWeekForProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productCategoryRepository.totalSpentByWeek(id).flowOn(dispatcher)
}

class GetTotalSpentByMonthForProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productCategoryRepository.totalSpentByMonth(id).flowOn(dispatcher)
}

class GetTotalSpentByYearForProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productCategoryRepository.totalSpentByYear(id).flowOn(dispatcher)
}

class GetTotalSpentByProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productCategoryRepository.totalSpentByCategory().flowOn(dispatcher)
}

class GetTotalSpentByProductCategoryByMonthUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource
) {
    operator fun invoke(year: Int, month: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productCategoryRepository.totalSpentByCategoryByMonth(year, month).flowOn(dispatcher)
}
