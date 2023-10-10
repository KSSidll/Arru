package com.kssidll.arrugarq.ui.screen.addproductproducer

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

internal data class AddProductProducerScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val name: MutableState<String> = mutableStateOf(String()),
    val nameError: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Validates name field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddProductProducerScreenState.validateName(): Boolean {
    return !(name.value.isBlank()).also { nameError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
internal fun AddProductProducerScreenState.validate(): Boolean {
    return validateName()
}

/**
 * performs data validation and tries to extract embedded data
 * @return Null if validation sets error flags, extracted data otherwise
 */
internal fun AddProductProducerScreenState.extractProducerOrNull(): ProductProducer? {
    if (!validate()) return null

    return ProductProducer(
        name = name.value.trim(),
    )
}

@HiltViewModel
class AddProductProducerViewModel @Inject constructor(
    productProducerRepository: IProductProducerRepository,
): ViewModel() {
    internal val addProductProducerScreenState: AddProductProducerScreenState =
        AddProductProducerScreenState()

    private val productProducerRepository: IProductProducerRepository

    init {
        this.productProducerRepository = productProducerRepository
    }

    /**
     * Tries to add a product variant to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addProducer(): Long? = viewModelScope.async {
        addProductProducerScreenState.attemptedToSubmit.value = true
        val producer = addProductProducerScreenState.extractProducerOrNull() ?: return@async null

        return@async productProducerRepository.insert(producer)
    }
        .await()
}