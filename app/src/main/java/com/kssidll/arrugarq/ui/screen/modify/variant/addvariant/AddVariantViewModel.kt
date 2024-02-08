package com.kssidll.arrugarq.ui.screen.modify.variant.addvariant

import android.util.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.data.repository.VariantRepositorySource.Companion.InsertResult
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
     * @return resulting [InsertResult]
     */
    suspend fun addVariant(productId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

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