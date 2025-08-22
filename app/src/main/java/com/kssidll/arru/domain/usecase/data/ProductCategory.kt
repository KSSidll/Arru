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
