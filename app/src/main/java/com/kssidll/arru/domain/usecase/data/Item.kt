package com.kssidll.arru.domain.usecase.data

import com.kssidll.arru.data.repository.ItemRepositorySource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn

/** ENTITY */

class GetItemEntityUseCase(
    private val itemRepository: ItemRepositorySource,
) {
    operator fun invoke(
        id: Long,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ) = itemRepository.get(id).flowOn(dispatcher)
}


/** DOMAIN */
