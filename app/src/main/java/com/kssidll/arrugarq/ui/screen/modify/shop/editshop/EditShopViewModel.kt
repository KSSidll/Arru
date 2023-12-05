package com.kssidll.arrugarq.ui.screen.modify.shop.editshop


import android.database.sqlite.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.shop.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditShopViewModel @Inject constructor(
    override val shopRepository: ShopRepositorySource,
    private val itemRepository: ItemRepositorySource,
): ModifyShopViewModel() {

    /**
     * Tries to update shop with provided [shopId] with current screen state data
     * @return Whether the update was successful
     */
    suspend fun updateShop(shopId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        val shop = screenState.extractDataOrNull(shopId) ?: return@async false

        try {
            shopRepository.update(shop)
        } catch (_: SQLiteConstraintException) {
            screenState.name.apply { value = value.toError(FieldError.DuplicateValueError) }
            return@async false
        }

        return@async true
    }
        .await()

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
