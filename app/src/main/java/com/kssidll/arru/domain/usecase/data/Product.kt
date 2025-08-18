package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */
class GetProductEntityUseCase(private val productRepository: ProductRepositorySource) {
    operator fun invoke(id: Long, dispatcher: CoroutineDispatcher = Dispatchers.IO) =
        productRepository.get(id).flowOn(dispatcher)
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
