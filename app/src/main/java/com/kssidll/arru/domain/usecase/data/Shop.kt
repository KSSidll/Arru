package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ShopRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */

class GetShopEntityUseCase(
    private val shopRepository: ShopRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = shopRepository.get(id).flowOn(dispatcher)
}


/** DOMAIN */

// class GetShopUseCase(
//     private val getShopEntityUseCase: GetShopEntityUseCase,
// ) {
//     operator fun invoke(
//         id: Long,
//         dispatcher: CoroutineDispatcher = Dispatchers.IO,
//     ) = getShopEntityUseCase(id, dispatcher).map {
//         it?.let { Shop.fromEntity(it) }
//     }
// }

class GetItemsForShopUseCase(
    private val shopRepository: ShopRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = shopRepository.itemsFor(id).flowOn(dispatcher)
}
