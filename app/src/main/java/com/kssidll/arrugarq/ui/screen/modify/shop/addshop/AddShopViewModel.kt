package com.kssidll.arrugarq.ui.screen.modify.shop.addshop

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.ui.screen.modify.shop.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddShopViewModel @Inject constructor(
    override val shopRepository: ShopRepositorySource,
): ModifyShopViewModel() {

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