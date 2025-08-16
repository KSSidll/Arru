package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.data.data.ProductVariant
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** ENTITY */

class GetProductVariantEntityUseCase(
    private val productVariantRepository: ProductVariantRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = withContext(dispatcher) {
        productVariantRepository.get(id)
    }
}


/** DOMAIN */

class GetProductVariantUseCase(
    private val getProductVariantEntityUseCase: GetProductVariantEntityUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = getProductVariantEntityUseCase(id, dispatcher).map {
        it?.let { ProductVariant.fromEntity(it) }
    }
}
