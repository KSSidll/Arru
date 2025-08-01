package com.kssidll.arru.ui.screen.modify.variant.addvariant

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.repository.VariantRepositorySource
import com.kssidll.arru.data.repository.VariantRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.variant.ModifyVariantViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class AddVariantViewModel @Inject constructor(
    override val variantRepository: VariantRepositorySource,
): ModifyVariantViewModel() {

    init {
        screenState.isVariantGlobal.value = Field.Loaded(false)
    }

    /**
     * Tries to add a product variant to the repository
     * @param productId: Id of the product that the variant is being created for, ignored if state defines product as global
     * @return resulting [InsertResult]
     */
    suspend fun addVariant(productId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val productId = if (screenState.isVariantGlobal.value.data ?: false) {
            null
        } else productId

        val result = variantRepository.insert(
            productId,
            screenState.name.value.data.orEmpty()
        )

        if (result.isError()) {
            when (result.error!!) {
                is InsertResult.InvalidName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                is InsertResult.DuplicateName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.DuplicateValueError)
                    }
                }

                is InsertResult.InvalidProductId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to insert variant with invalid productId in AddVariantViewModel"
                    )

                    return@async InsertResult.Success(0)
                }
            }
        }

        return@async result
    }
        .await()
}