package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */


class GetProductProducerEntityUseCase(
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productProducerRepository.get(id).flowOn(dispatcher)
}


/** DOMAIN */


// class GetProductProducerUseCase(
//     private val getProductProducerEntityUseCase: GetProductProducerEntityUseCase,
// ) {
//     operator fun invoke(
//         id: Long,
//         dispatcher: CoroutineDispatcher = Dispatchers.IO,
//     ) = getProductProducerEntityUseCase(id, dispatcher).map {
//         it?.let { ProductProducer.fromEntity(it) }
//     }
// }

class GetTotalSpentForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productProducerRepository.totalSpent(id).flowOn(dispatcher)
}

class GetItemsForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productProducerRepository.itemsFor(id).flowOn(dispatcher)
}


/** DOMAIN CHART */


class GetTotalSpentByDayForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productProducerRepository.totalSpentByDay(id).flowOn(dispatcher)
}

class GetTotalSpentByWeekForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productProducerRepository.totalSpentByWeek(id).flowOn(dispatcher)
}

class GetTotalSpentByMonthForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productProducerRepository.totalSpentByMonth(id).flowOn(dispatcher)
}

class GetTotalSpentByYearForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productProducerRepository.totalSpentByYear(id).flowOn(dispatcher)
}
