package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.domain.data.data.Item
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/** ENTITY */

class GetItemEntityUseCase(
    private val itemRepository: ItemRepositorySource,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = withContext(dispatcher) {
        itemRepository.get(id)
    }
}


/** DOMAIN */

class GetItemUseCase(
    private val getItemEntityUseCase: GetItemEntityUseCase,
) {
    suspend operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = getItemEntityUseCase(id, dispatcher).map {
        it?.let { Item.fromEntity(it) }
    }
}
