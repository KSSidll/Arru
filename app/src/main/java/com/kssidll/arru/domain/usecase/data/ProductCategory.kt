package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.domain.data.data.ProductCategory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** ENTITY */

class GetProductCategoryEntityUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = withContext(dispatcher) {
        productCategoryRepository.get(id)
    }
}


/** DOMAIN */

class GetProductCategoryUseCase(
    private val getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = getProductCategoryEntityUseCase(id, dispatcher).map {
        it?.let { ProductCategory.fromEntity(it) }
    }
}
