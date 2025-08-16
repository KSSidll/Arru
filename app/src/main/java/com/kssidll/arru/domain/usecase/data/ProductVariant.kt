package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */

class GetProductVariantEntityUseCase(
    private val productVariantRepository: ProductVariantRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productVariantRepository.get(id).flowOn(dispatcher)
}


/** DOMAIN */

// class GetProductVariantUseCase(
//     private val getProductVariantEntityUseCase: GetProductVariantEntityUseCase,
// ) {
//     operator fun invoke(
//         id: Long,
//         dispatcher: CoroutineDispatcher = Dispatchers.IO,
//     ) = getProductVariantEntityUseCase(id, dispatcher).map {
//         it?.let { ProductVariant.fromEntity(it) }
//     }
// }
