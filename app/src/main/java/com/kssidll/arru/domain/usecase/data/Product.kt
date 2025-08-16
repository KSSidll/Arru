package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.domain.data.data.Product
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** ENTITY */

class GetProductEntityUseCase(
    private val productRepository: ProductRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = withContext(dispatcher) {
        productRepository.get(id)
    }
}


/** DOMAIN */

class GetProductUseCase(
    private val getProductEntityUseCase: GetProductEntityUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = getProductEntityUseCase(id, dispatcher).map {
        it?.let { Product.fromEntity(it) }
    }
}
