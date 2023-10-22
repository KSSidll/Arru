package com.kssidll.arrugarq.ui.screen.shop.editshop


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.shop.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditShopViewModel @Inject constructor(
    private val shopRepository: IShopRepository,
    private val itemRepository: IItemRepository,
): ViewModel() {
    internal val screenState: EditShopScreenState = EditShopScreenState()

    /**
     * Tries to update shop with provided id with current screen state data
     */
    fun updateShop(shopId: Long) = viewModelScope.launch {
        screenState.attemptedToSubmit.value = true
        val shop = screenState.extractShopOrNull(shopId) ?: return@launch

        shopRepository.update(shop)
    }

    /**
     * Tries to delete shop with provided id
     * @return True if operation started, false otherwise
     */
    suspend fun deleteShop(shopId: Long): Boolean {
        return viewModelScope.async {
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

    /**
     * Updates data in the screen state
     */
    fun updateState(shopId: Long) = viewModelScope.launch {
        screenState.loadingName.value = true

        run {
            val shop = shopRepository.get(shopId) ?: return@launch

            screenState.name.value = shop.name
        }

        screenState.loadingName.value = false
    }
}
