package com.kssidll.arrugarq.ui.screen.modify.variant.editvariant


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.modify.variant.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditVariantViewModel @Inject constructor(
    override val variantRepository: IVariantRepository,
    private val itemRepository: IItemRepository,
): ModifyVariantViewModel() {

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
}
