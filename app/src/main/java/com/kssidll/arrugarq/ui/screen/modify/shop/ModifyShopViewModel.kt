package com.kssidll.arrugarq.ui.screen.modify.shop

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
import kotlinx.coroutines.*

/**
 * Base [ViewModel] class for Shop modification view models
 * @property screenState A [ModifyShopScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Shop matching provided id, only changes representation data and loading state
 */
abstract class ModifyShopViewModel: ViewModel() {
    protected abstract val shopRepository: ShopRepositorySource

    internal val screenState: ModifyShopScreenState = ModifyShopScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [shopId] was valid, false otherwise
     */
    suspend fun updateState(shopId: Long) = viewModelScope.async {
        screenState.name.apply { value = value.toLoading() }

        val shop: Shop? = shopRepository.get(shopId)

        screenState.name.apply {
            value = shop?.name.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        return@async shop != null
    }
        .await()
}

/**
 * Data representing [ModifyShopScreenImpl] screen state
 */
data class ModifyShopScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
): ModifyScreenState<Shop>() {
    /**
     * Validates name field and updates its error flag
     * @return true if field is of correct value, false otherwise
     */
    fun validateName(): Boolean {
        name.apply {
            if (value.data.isNullOrBlank()) {
                value = value.toError(FieldError.NoValueError)
            }

            return name.value.isNotError()
        }
    }

    override fun validate(): Boolean {
        return validateName()
    }

    override fun extractDataOrNull(id: Long): Shop? {
        if (!validate()) return null

        return Shop(
            id = id,
            name = name.value.data?.trim() ?: return null,
        )
    }

}