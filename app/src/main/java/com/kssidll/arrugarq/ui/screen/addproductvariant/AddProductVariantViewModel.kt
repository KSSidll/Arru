package com.kssidll.arrugarq.ui.screen.addproductvariant

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

internal data class AddProductVariantScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val name: MutableState<String> = mutableStateOf(String()),
    val nameError: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Validates name field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddProductVariantScreenState.validateName(): Boolean {
    return !(name.value.isBlank()).also { nameError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
internal fun AddProductVariantScreenState.validate(): Boolean {
    return validateName()
}

/**
 * performs data validation and tries to extract embedded data
 * @param productId: Id of the product that the variant is being created for
 * @return Null if validation sets error flags, extracted data otherwise
 */
internal fun AddProductVariantScreenState.extractProducerOrNull(productId: Long): ProductVariant? {
    if (!validate()) return null

    return ProductVariant(
        productId = productId,
        name = name.value.trim(),
    )
}

@HiltViewModel
class AddProductVariantViewModel @Inject constructor(
    private val productVariantRepository: IProductVariantRepository,
): ViewModel() {
    internal val addProductVariantScreenState: AddProductVariantScreenState =
        AddProductVariantScreenState()

    /**
     * Tries to add a product variant to the repository
     * @param productId: Id of the product that the variant is being created for
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addVariant(productId: Long): Long? = viewModelScope.async {
        addProductVariantScreenState.attemptedToSubmit.value = true
        val variant =
            addProductVariantScreenState.extractProducerOrNull(productId) ?: return@async null

        return@async productVariantRepository.insert(variant)
    }
        .await()
}