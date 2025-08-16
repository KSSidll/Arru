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

class GetItemsForProductProducerUseCase(
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productProducerRepository.itemsFor(id).flowOn(dispatcher)
}
