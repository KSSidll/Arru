package com.kssidll.arrugarq.ui.screen.modify.shop.editshop


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.modify.shop.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditShopViewModel @Inject constructor(
    override val shopRepository: IShopRepository,
    private val itemRepository: IItemRepository,
): ModifyShopViewModel() {

    /**
     * Tries to update shop with provided [shopId] with current screen state data
     */
    fun updateShop(shopId: Long) = viewModelScope.launch {
        screenState.attemptedToSubmit.value = true
        val shop = screenState.extractShopOrNull(shopId) ?: return@launch

        shopRepository.update(shop)
    }

    /**
     * Tries to delete shop with provided [shopId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return True if operation started, false otherwise
     */
    suspend fun deleteShop(shopId: Long) = viewModelScope.async {
        // return true if no such shop exists
        val shop = shopRepository.get(shopId) ?: return@async true

        val items = itemRepository.getByShopId(shopId)

        if (items.isNotEmpty() && !screenState.deleteWarningConfirmed.value) {
            screenState.showDeleteWarning.value = true
            return@async false
        } else {
            itemRepository.delete(items)
            shopRepository.delete(shop)
            return@async true
        }
    }
        .await()
}
