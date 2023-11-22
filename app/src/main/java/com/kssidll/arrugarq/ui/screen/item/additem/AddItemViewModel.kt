package com.kssidll.arrugarq.ui.screen.item.additem

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.item.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddItemViewModel @Inject constructor(
    override val itemRepository: IItemRepository,
    override val productRepository: IProductRepository,
    override val variantsRepository: IVariantRepository,
    override val shopRepository: IShopRepository,
): ModifyItemViewModel() {

    init {
        initialize()
    }

    /**
     * Tries to add item to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addItem(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        val item = screenState.extractItemOrNull() ?: return@async null

        return@async itemRepository.insert(item)
    }
        .await()
}