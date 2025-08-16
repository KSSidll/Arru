package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.domain.data.data.ProductProducer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** ENTITY */

class GetProductProducerEntityUseCase(
    private val productProducerRepository: ProductProducerRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = withContext(dispatcher) {
        productProducerRepository.get(id)
    }
}


/** DOMAIN */

class GetProductProducerUseCase(
    private val getProductProducerEntityUseCase: GetProductProducerEntityUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = getProductProducerEntityUseCase(id, dispatcher).map {
        it?.let { ProductProducer.fromEntity(it) }
    }
}
