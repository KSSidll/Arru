package com.kssidll.arrugarq.ui.screen.modify.item.edititem


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.modify.item.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditItemViewModel @Inject constructor(
    override val itemRepository: IItemRepository,
    override val productRepository: IProductRepository,
    override val variantsRepository: IVariantRepository,
    override val shopRepository: IShopRepository,
): ModifyItemViewModel() {

    init {
        computeStartState()
    }

    /**
     * Tries to update item with provided [itemId] with current screen state data
     */
    fun updateItem(itemId: Long) = viewModelScope.launch {
        screenState.attemptedToSubmit.value = true
        val item = screenState.extractItemOrNull(itemId) ?: return@launch

        itemRepository.update(item)
    }

    /**
     * Tries to delete item with provided [itemId]
     * @return True if operation started, false otherwise
     */
    suspend fun deleteItem(itemId: Long) = viewModelScope.async {
        // return true if no such item exists
        val item = itemRepository.get(itemId) ?: return@async true

        itemRepository.delete(item)
        return@async true
    }
        .await()
}
