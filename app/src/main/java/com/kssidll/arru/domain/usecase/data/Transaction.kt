package com.kssidll.arru.domain.usecase.data

import androidx.paging.map
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

sealed class InsertTransactionEntityUseCaseResult {
    class Success(val id: Long) : InsertTransactionEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : InsertTransactionEntityUseCaseResult()

    sealed class Errors

    data object DateNoValue : Errors()

    data object TotalCostNoValue : Errors()

    data object TotalCostInvalid : Errors()

    data object ShopIdInvalid : Errors()
}

sealed class UpdateTransactionEntityUseCaseResult {
    object Success : UpdateTransactionEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : UpdateTransactionEntityUseCaseResult()

    sealed class Errors

    data object TransactionIdInvalid : Errors()

    data object DateNoValue : Errors()

    data object TotalCostNoValue : Errors()

    data object TotalCostInvalid : Errors()

    data object ShopIdInvalid : Errors()
}

sealed class DeleteTransactionEntityUseCaseResult {
    object Success : DeleteTransactionEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : DeleteTransactionEntityUseCaseResult()

    sealed class Errors

    data object TransactionIdInvalid : Errors()

    data object DangerousDelete : Errors()
}

/** ENTITY */
class InsertTransactionEntityUseCase(
    private val shopRepository: ShopRepositorySource,
    private val transactionRepository: TransactionRepositorySource,
) {
    suspend operator fun invoke(
        date: Long?,
        totalCost: String?,
        note: String?,
        shopId: Long?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): InsertTransactionEntityUseCaseResult {
        val errors = mutableListOf<InsertTransactionEntityUseCaseResult.Errors>()

        if (date == null) {
            errors.add(InsertTransactionEntityUseCaseResult.DateNoValue)
        }

        var entityTotalCost: Long? = null
        if (totalCost.isNullOrBlank()) {
            errors.add(InsertTransactionEntityUseCaseResult.TotalCostNoValue)
        } else {
            entityTotalCost =
                TransactionEntity.totalCostFromString(totalCost)
                    ?: TransactionEntity.INVALID_TOTAL_COST

            if (entityTotalCost == TransactionEntity.INVALID_TOTAL_COST) {
                errors.add(InsertTransactionEntityUseCaseResult.TotalCostInvalid)
            }
        }

        val entityNote = if (note.isNullOrBlank()) null else note.trim()

        shopId?.let {
            if (shopRepository.get(it).first() == null) {
                errors.add(InsertTransactionEntityUseCaseResult.ShopIdInvalid)
            }
        }

        if (errors.isNotEmpty()) {
            return InsertTransactionEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity =
            TransactionEntity(
                date = date!!,
                shopEntityId = shopId,
                totalCost = entityTotalCost!!,
                note = entityNote,
            )

        val id = transactionRepository.insert(entity)

        return InsertTransactionEntityUseCaseResult.Success(id)
    }
}

class UpdateTransactionEntityUseCase(
    private val shopRepository: ShopRepositorySource,
    private val transactionRepository: TransactionRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        date: Long?,
        totalCost: String?,
        note: String?,
        shopId: Long?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): UpdateTransactionEntityUseCaseResult {
        val errors = mutableListOf<UpdateTransactionEntityUseCaseResult.Errors>()

        if (transactionRepository.get(id).first() == null) {
            errors.add(UpdateTransactionEntityUseCaseResult.TransactionIdInvalid)
        }

        if (date == null) {
            errors.add(UpdateTransactionEntityUseCaseResult.DateNoValue)
        }

        var entityTotalCost: Long? = null
        if (totalCost.isNullOrBlank()) {
            errors.add(UpdateTransactionEntityUseCaseResult.TotalCostNoValue)
        } else {
            entityTotalCost =
                TransactionEntity.totalCostFromString(totalCost)
                    ?: TransactionEntity.INVALID_TOTAL_COST

            if (entityTotalCost == TransactionEntity.INVALID_TOTAL_COST) {
                errors.add(UpdateTransactionEntityUseCaseResult.TotalCostInvalid)
            }
        }

        val entityNote = if (note.isNullOrBlank()) null else note.trim()

        shopId?.let {
            if (shopRepository.get(it).first() == null) {
                errors.add(UpdateTransactionEntityUseCaseResult.ShopIdInvalid)
            }
        }

        if (errors.isNotEmpty()) {
            return UpdateTransactionEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity =
            TransactionEntity(
                id = id,
                date = date!!,
                shopEntityId = shopId,
                totalCost = entityTotalCost!!,
                note = entityNote,
            )

        transactionRepository.update(entity)

        return UpdateTransactionEntityUseCaseResult.Success
    }
}

class DeleteTransactionEntityUseCase(
    private val itemRepository: ItemRepositorySource,
    private val transactionRepository: TransactionRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        ignoreDangerous: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): DeleteTransactionEntityUseCaseResult {
        val errors = mutableListOf<DeleteTransactionEntityUseCaseResult.Errors>()

        val entity = transactionRepository.get(id).first()
        if (entity == null) {
            errors.add(DeleteTransactionEntityUseCaseResult.TransactionIdInvalid)
        }

        val items = itemRepository.byTransaction(id).first()

        if (!ignoreDangerous && items.isNotEmpty()) {
            errors.add(DeleteTransactionEntityUseCaseResult.DangerousDelete)
        }

        if (errors.isNotEmpty()) {
            return DeleteTransactionEntityUseCaseResult.Error(errors.toImmutableList())
        }

        itemRepository.delete(items)
        transactionRepository.delete(entity!!)

        return DeleteTransactionEntityUseCaseResult.Success
    }
}

class GetTransactionEntityUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.get(id).flowOn(dispatcher)
}

class GetNewestTransactionEntityUseCase(
    private val transactionRepository: TransactionRepositorySource
) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.newest().flowOn(dispatcher)
}

/** DOMAIN */
class GetTransactionUseCase(private val transactionRepository: TransactionRepositorySource) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository
            .intermediateFor(id)
            .map { intermediate -> intermediate?.toTransaction() }
            .flowOn(dispatcher)
}

class GetAllTransactionsUseCase(private val transactionRepository: TransactionRepositorySource) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository
            .intermediates()
            .map { pagingData -> pagingData.map { intermediate -> intermediate.toTransaction() } }
            .flowOn(dispatcher)
}

class GetTotalSpentUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpent().flowOn(dispatcher)
}

/** DOMAIN CHART */
class GetTotalSpentByDayUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpentByDay().flowOn(dispatcher)
}

class GetTotalSpentByWeekUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpentByWeek().flowOn(dispatcher)
}

class GetTotalSpentByMonthUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpentByMonth().flowOn(dispatcher)
}

class GetTotalSpentByYearUseCase(private val transactionRepository: TransactionRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        transactionRepository.totalSpentByYear().flowOn(dispatcher)
}
