package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */

class GetProductCategoryEntityUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productCategoryRepository.get(id).flowOn(dispatcher)
}


/** DOMAIN */

// class GetProductCategoryUseCase(
//     private val getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase,
// ) {
//     operator fun invoke(
//         id: Long,
//         dispatcher: CoroutineDispatcher = Dispatchers.IO,
//     ) = getProductCategoryEntityUseCase(id, dispatcher).map {
//         it?.let { ProductCategory.fromEntity(it) }
//     }
// }

class GetTotalSpentForProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productCategoryRepository.totalSpent(id).flowOn(dispatcher)
}

class GetItemsForProductCategoryUseCase(
    private val productCategoryRepository: ProductCategoryRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = productCategoryRepository.itemsFor(id).flowOn(dispatcher)
}
