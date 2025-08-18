package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */
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
