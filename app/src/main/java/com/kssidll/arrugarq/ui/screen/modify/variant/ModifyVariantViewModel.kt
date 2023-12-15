package com.kssidll.arrugarq.ui.screen.modify.variant

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
import kotlinx.coroutines.*

/**
 * Base [ViewModel] class for Variant modification view models
 * @property screenState A [ModifyVariantScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Variant matching provided id, only changes representation data and loading state
 */
abstract class ModifyVariantViewModel: ViewModel() {
    protected abstract val variantRepository: VariantRepositorySource

    internal val screenState: ModifyVariantScreenState = ModifyVariantScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [variantId] was valid, false otherwise
     */
    suspend fun updateState(variantId: Long) = viewModelScope.async {
        screenState.name.apply { value = value.toLoading() }

        val variant = variantRepository.get(variantId)

        screenState.name.apply {
            value = variant?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        return@async variant != null
    }
        .await()
}

/**
 * Data representing [ModifyVariantScreenImpl] screen state
 */
data class ModifyVariantScreenState(
    var productId: Long = -1, // not sure how to handle this any other way
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
): ModifyScreenState<ProductVariant>() {
    /**
     * Validates name field and updates its error flag
     * @return true if field is of correct value, false otherwise
     */
    fun validateName(): Boolean {
        name.apply {
            if (value.data.isNullOrBlank()) {
                value = value.toError(FieldError.NoValueError)
            }

            return value.isNotError()
        }
    }

    override fun validate(): Boolean {
        return validateName()
    }

    override fun extractDataOrNull(id: Long): ProductVariant? {
        if (!validate()) return null
        if (productId < 1) error("ProductVariant Extraction from ModifyVariatScreenState Failed, productId was not set before trying to extract data")

        return ProductVariant(
            id = id,
            productId = productId,
            name = name.value.data?.trim() ?: return null,
        )
    }

}
