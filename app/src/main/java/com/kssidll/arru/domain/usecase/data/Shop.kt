package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn

sealed class InsertShopEntityUseCaseResult {
    class Success(val id: Long) : InsertShopEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : InsertShopEntityUseCaseResult()

    sealed class Errors

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()
}

sealed class UpdateShopEntityUseCaseResult {
    object Success : UpdateShopEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : UpdateShopEntityUseCaseResult()

    sealed class Errors

    data object ShopIdInvalid : Errors()

    data object NameNoValue : Errors()

    data object NameDuplicateValue : Errors()
}

sealed class MergeShopEntityUseCaseResult {
    class Success(val mergedEntity: ShopEntity) : MergeShopEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : MergeShopEntityUseCaseResult()

    sealed class Errors

    data object ShopIdInvalid : Errors()

    data object MergeIntoIdInvalid : Errors()
}

sealed class DeleteShopEntityUseCaseResult {
    object Success : DeleteShopEntityUseCaseResult()

    class Error(val errors: ImmutableList<Errors>) : DeleteShopEntityUseCaseResult()

    sealed class Errors

    data object ShopIdInvalid : Errors()

    data object DangerousDelete : Errors()
}

/** ENTITY */
class InsertShopEntityUseCase(private val shopRepository: ShopRepositorySource) {
    suspend operator fun invoke(
        name: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): InsertShopEntityUseCaseResult {
        val errors = mutableListOf<InsertShopEntityUseCaseResult.Errors>()

        if (name.isNullOrBlank()) {
            errors.add(InsertShopEntityUseCaseResult.NameNoValue)
        }

        name?.let {
            if (shopRepository.byName(it).first() != null) {
                errors.add(InsertShopEntityUseCaseResult.NameDuplicateValue)
            }
        }

        if (errors.isNotEmpty()) {
            return InsertShopEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity = ShopEntity(name = name!!.trim())

        val id = shopRepository.insert(entity)

        return InsertShopEntityUseCaseResult.Success(id)
    }
}

class UpdateShopEntityUseCase(private val shopRepository: ShopRepositorySource) {
    suspend operator fun invoke(
        id: Long,
        name: String?,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): UpdateShopEntityUseCaseResult {
        val errors = mutableListOf<UpdateShopEntityUseCaseResult.Errors>()

        if (shopRepository.get(id).first() == null) {
            errors.add(UpdateShopEntityUseCaseResult.ShopIdInvalid)
        }

        if (name.isNullOrBlank()) {
            errors.add(UpdateShopEntityUseCaseResult.NameNoValue)
        }

        name?.let {
            val other = shopRepository.byName(it).first()
            if (other != null && other.id != id) {
                errors.add(UpdateShopEntityUseCaseResult.NameDuplicateValue)
            }
        }

        if (errors.isNotEmpty()) {
            return UpdateShopEntityUseCaseResult.Error(errors.toImmutableList())
        }

        val entity = ShopEntity(id = id, name = name!!.trim())

        shopRepository.update(entity)

        return UpdateShopEntityUseCaseResult.Success
    }
}

class MergeShopEntityUseCase(
    private val transactionRepository: TransactionRepositorySource,
    private val shopRepository: ShopRepositorySource,
    private val performAutomaticBackupIfEnabledUseCase: PerformAutomaticBackupIfEnabledUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        mergeIntoId: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): MergeShopEntityUseCaseResult {
        val errors = mutableListOf<MergeShopEntityUseCaseResult.Errors>()

        val entity = shopRepository.get(id).first()
        if (entity == null) {
            errors.add(MergeShopEntityUseCaseResult.ShopIdInvalid)
        }

        val mergeInto = shopRepository.get(mergeIntoId).first()
        if (mergeInto == null) {
            errors.add(MergeShopEntityUseCaseResult.MergeIntoIdInvalid)
        }

        if (errors.isNotEmpty()) {
            return MergeShopEntityUseCaseResult.Error(errors.toImmutableList())
        }

        performAutomaticBackupIfEnabledUseCase()

        val transactions = transactionRepository.byShop(id).first()

        val transactionsToUpdate = transactions.map { it.copy(shopEntityId = mergeIntoId) }

        transactionRepository.update(transactionsToUpdate)
        shopRepository.delete(entity!!)

        return MergeShopEntityUseCaseResult.Success(mergeInto!!)
    }
}

class DeleteShopEntityUseCase(
    private val transactionRepository: TransactionRepositorySource,
    private val itemRepository: ItemRepositorySource,
    private val shopRepository: ShopRepositorySource,
    private val performAutomaticBackupIfEnabledUseCase: PerformAutomaticBackupIfEnabledUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        ignoreDangerous: Boolean = false,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): DeleteShopEntityUseCaseResult {
        val errors = mutableListOf<DeleteShopEntityUseCaseResult.Errors>()

        val entity = shopRepository.get(id).first()
        if (entity == null) {
            errors.add(DeleteShopEntityUseCaseResult.ShopIdInvalid)
        }

        val transactions = transactionRepository.byShop(id).first()

        if (!ignoreDangerous && transactions.isNotEmpty()) {
            errors.add(DeleteShopEntityUseCaseResult.DangerousDelete)
        }

        if (errors.isNotEmpty()) {
            return DeleteShopEntityUseCaseResult.Error(errors.toImmutableList())
        }

        performAutomaticBackupIfEnabledUseCase()

        transactions.forEach {
            val items = itemRepository.byTransaction(it.id).first()
            itemRepository.delete(items)
        }

        transactionRepository.delete(transactions)
        shopRepository.delete(entity!!)

        return DeleteShopEntityUseCaseResult.Success
    }
}

class GetShopEntityUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.get(id).flowOn(dispatcher)
}

class GetAllShopEntityUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.all().flowOn(dispatcher)
}

/** DOMAIN */
class GetTotalSpentForShopUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.totalSpent(id).flowOn(dispatcher)
}

class GetItemsForShopUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.itemsFor(id).flowOn(dispatcher)
}

/** DOMAIN CHART */
class GetTotalSpentByDayForShopUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.totalSpentByDay(id).flowOn(dispatcher)
}

class GetTotalSpentByWeekForShopUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.totalSpentByWeek(id).flowOn(dispatcher)
}

class GetTotalSpentByMonthForShopUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.totalSpentByMonth(id).flowOn(dispatcher)
}

class GetTotalSpentByYearForShopUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.totalSpentByYear(id).flowOn(dispatcher)
}

class GetTotalSpentByShopUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.totalSpentByShop().flowOn(dispatcher)
}

class GetTotalSpentByShopByMonthUseCase(private val shopRepository: ShopRepositorySource) {
    operator fun invoke(year: Int, month: Int, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        shopRepository.totalSpentByShopByMonth(year, month).flowOn(dispatcher)
}
