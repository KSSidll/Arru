package com.kssidll.arrugarq.ui.screen.modify.item.edititem


import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.ui.screen.modify.item.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditItemViewModel @Inject constructor(
    override val itemRepository: ItemRepositorySource,
    override val productRepository: ProductRepositorySource,
    override val variantsRepository: VariantRepositorySource,
    override val shopRepository: ShopRepositorySource,
): ModifyItemViewModel() {

    /**
     * Tries to update item with provided [itemId] with current screen state data
     * @return Whether the update was successful
     */
    suspend fun updateItem(itemId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        screenState.validate()

        val item = screenState.extractDataOrNull(itemId) ?: return@async false

        itemRepository.update(item)

        return@async true
    }
        .await()

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
