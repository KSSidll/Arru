package com.kssidll.arrugarq.ui.screen.shop

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.*

/**
 * Base [ViewModel] class for Shop modification view models
 * @property screenState A [ModifyShopScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Shop matching provided id, only changes representation data and loading state
 */
abstract class ModifyShopViewModel: ViewModel() {
    protected abstract val shopRepository: IShopRepository

    internal val screenState: ModifyShopScreenState = ModifyShopScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [shopId] was valid, false otherwise
     */
    suspend fun updateState(shopId: Long) = viewModelScope.async {
        screenState.loadingName.value = true

        val shop = shopRepository.get(shopId)

        if (shop == null) {
            screenState.loadingName.value = false
            return@async false
        }

        screenState.name.value = shop.name

        screenState.loadingName.value = false
        return@async true
    }
        .await()
}
