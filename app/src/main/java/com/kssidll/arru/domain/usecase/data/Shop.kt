package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.domain.data.data.Shop
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** ENTITY */

class GetShopEntityUseCase(
    private val shopRepository: ShopRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = withContext(dispatcher) {
        shopRepository.get(id)
    }
}


/** DOMAIN */

class GetShopUseCase(
    private val getShopEntityUseCase: GetShopEntityUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = getShopEntityUseCase(id, dispatcher).map {
        it?.let { Shop.fromEntity(it) }
    }
}
