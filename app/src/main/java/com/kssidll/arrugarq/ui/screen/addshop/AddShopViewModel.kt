package com.kssidll.arrugarq.ui.screen.addshop

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

internal data class AddShopScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val name: MutableState<String> = mutableStateOf(String()),
    val nameError: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Validates name field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddShopScreenState.validateName(): Boolean {
    return !(name.value.isBlank()).also { nameError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
internal fun AddShopScreenState.validate(): Boolean {
    return validateName()
}

/**
 * performs data validation and tries to extract embedded data
 * @return Null if validation sets error flags, extracted data otherwise
 */
internal fun AddShopScreenState.extractShopOrNull(): Shop? {
    if (!validate()) return null

    return Shop(
        name = name.value.trim(),
    )
}

@HiltViewModel
class AddShopViewModel @Inject constructor(
    shopRepository: IShopRepository,
): ViewModel() {
    internal val addShopScreenState: AddShopScreenState = AddShopScreenState()

    private val shopRepository: IShopRepository

    init {
        this.shopRepository = shopRepository
    }

    /**
     * Tries to add a shop to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addShop(): Long? = viewModelScope.async {
        addShopScreenState.attemptedToSubmit.value = true
        val shop = addShopScreenState.extractShopOrNull() ?: return@async null

        return@async shopRepository.insert(shop)
    }
        .await()
}