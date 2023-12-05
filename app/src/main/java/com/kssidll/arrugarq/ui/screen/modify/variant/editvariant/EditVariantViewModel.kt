package com.kssidll.arrugarq.ui.screen.modify.variant.editvariant


import android.database.sqlite.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.variant.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditVariantViewModel @Inject constructor(
    override val variantRepository: VariantRepositorySource,
    private val itemRepository: ItemRepositorySource,
): ModifyVariantViewModel() {

    /**
     * Tries to update variant with provided [variantId] with current screen state data
     * @return Whether the update was successful
     */
    suspend fun updateVariant(variantId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        screenState.validate()

        val oldVariant = variantRepository.get(variantId) ?: return@async false
        screenState.productId = oldVariant.productId

        val variant = screenState.extractDataOrNull(variantId) ?: return@async false

        try {
            variantRepository.update(variant)
        } catch (_: SQLiteConstraintException) {
            screenState.name.apply { value = value.toError(FieldError.DuplicateValueError) }
            return@async false
        }

        return@async true
    }
        .await()

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
