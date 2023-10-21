package com.kssidll.arrugarq.ui.screen.shop.addshop

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.shop.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddShopViewModel @Inject constructor(
    private val shopRepository: IShopRepository,
): ViewModel() {
    internal val screenState: EditShopScreenState = EditShopScreenState()

    /**
     * Tries to add a shop to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addShop(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        val shop = screenState.extractShopOrNull() ?: return@async null

        return@async shopRepository.insert(shop)
    }
        .await()
}