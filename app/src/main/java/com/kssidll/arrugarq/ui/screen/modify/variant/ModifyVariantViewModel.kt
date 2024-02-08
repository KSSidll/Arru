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
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
): ModifyScreenState<ProductVariant>()
