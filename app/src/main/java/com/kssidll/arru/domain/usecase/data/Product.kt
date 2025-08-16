package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */

class GetProductEntityUseCase(
    private val productRepository: ProductRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productRepository.get(id).flowOn(dispatcher)
}


/** DOMAIN */

// class GetProductUseCase(
//     private val getProductEntityUseCase: GetProductEntityUseCase,
// ) {
//     operator fun invoke(
//         id: Long,
//         dispatcher: CoroutineDispatcher = Dispatchers.IO,
//     ) = getProductEntityUseCase(id, dispatcher).map {
//         it?.let { Product.fromEntity(it) }
//     }
// }

class GetItemsForProductUseCase(
    private val productRepository: ProductRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productRepository.itemsFor(id).flowOn(dispatcher)
}
