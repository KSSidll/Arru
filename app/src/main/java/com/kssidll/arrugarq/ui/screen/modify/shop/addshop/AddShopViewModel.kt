package com.kssidll.arrugarq.ui.screen.modify.shop.addshop

import android.database.sqlite.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
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
        screenState.validate()

        val shop = screenState.extractDataOrNull() ?: return@async null

        try {
            return@async shopRepository.insert(shop)
        } catch (_: SQLiteConstraintException) {
            screenState.name.apply { value = value.toError(FieldError.DuplicateValueError) }
            return@async null
        }
    }
        .await()
}