package com.kssidll.arrugarq.ui.screen.variant.editvariant


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.variant.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditVariantViewModel @Inject constructor(
    private val variantRepository: IVariantRepository,
    private val itemRepository: IItemRepository,
): ViewModel() {
    internal val screenState: EditVariantScreenState = EditVariantScreenState()

    /**
     * Tries to update variant with provided [variantId] with current screen state data
     */
    fun updateVariant(variantId: Long) = viewModelScope.launch {
        screenState.attemptedToSubmit.value = true
        val oldVariant = variantRepository.get(variantId) ?: return@launch
        val variant = screenState.extractVariantOrNull(
            productId = oldVariant.productId,
            variantId = variantId,
        ) ?: return@launch

        variantRepository.update(variant)
    }

    /**
     * Tries to delete variant with provided [variantId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return True if operation started, false otherwise
     */
    suspend fun deleteVariant(variantId: Long) = viewModelScope.async {
        // return true if no such variant exists
        val variant = variantRepository.get(variantId) ?: return@async true

        val items = itemRepository.getByVariantId(variantId)

        if (items.isNotEmpty() && !screenState.deleteWarningConfirmed.value) {
            screenState.showDeleteWarning.value = true
            return@async false
        } else {
            itemRepository.delete(items)
            variantRepository.delete(variant)
            return@async true
        }
    }
        .await()

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
