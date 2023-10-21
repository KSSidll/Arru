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
     */
    fun deleteShop(shopId: Long) = viewModelScope.launch {
        val shop = shopRepository.get(shopId) ?: return@launch

        shopRepository.delete(shop)
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
