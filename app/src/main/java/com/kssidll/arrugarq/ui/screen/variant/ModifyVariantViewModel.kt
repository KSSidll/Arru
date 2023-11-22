package com.kssidll.arrugarq.ui.screen.variant

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.*

/**
 * Base [ViewModel] class for Variant modification view models
 * @property screenState A [ModifyVariantScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Variant matching provided id, only changes representation data and loading state
 */
abstract class ModifyVariantViewModel: ViewModel() {
    protected abstract val variantRepository: IVariantRepository

    internal val screenState: ModifyVariantScreenState = ModifyVariantScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [variantId] was valid, false otherwise
     */
    suspend fun updateState(variantId: Long) = viewModelScope.async {
        screenState.loadingName.value = true

        val variant = variantRepository.get(variantId)

        if (variant == null) {
            screenState.loadingName.value = false
            return@async false
        }

        screenState.name.value = variant.name

        screenState.loadingName.value = false
        return@async true
    }
        .await()
}