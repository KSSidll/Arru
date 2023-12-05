package com.kssidll.arrugarq.ui.screen.modify.variant.addvariant

import android.database.sqlite.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.variant.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddVariantViewModel @Inject constructor(
    override val variantRepository: VariantRepositorySource,
): ModifyVariantViewModel() {

    /**
     * Tries to add a product variant to the repository
     * @param productId: Id of the product that the variant is being created for
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addVariant(productId: Long): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        screenState.validate()

        screenState.productId = productId
        val variant = screenState.extractDataOrNull(productId) ?: return@async null

        try {
            return@async variantRepository.insert(variant)
        } catch (_: SQLiteConstraintException) {
            screenState.name.apply { value = value.toError(FieldError.DuplicateValueError) }
            return@async null
        }
    }
        .await()
}