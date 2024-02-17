package com.kssidll.arrugarq.ui.screen.modify.variant.editvariant


import android.util.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.data.repository.VariantRepositorySource.Companion.DeleteResult
import com.kssidll.arrugarq.data.repository.VariantRepositorySource.Companion.UpdateResult
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.variant.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditVariantViewModel @Inject constructor(
    override val variantRepository: VariantRepositorySource,
): ModifyVariantViewModel() {
    private var mVariant: ProductVariant? = null

    /**
     * Updates data in the screen state
     * @return true if provided [variantId] was valid, false otherwise
     */
    suspend fun updateState(variantId: Long) = viewModelScope.async {
        // skip state update for repeating variantId
        if (variantId == mVariant?.id) return@async true

        screenState.name.apply { value = value.toLoading() }

        val variant = variantRepository.get(variantId)

        screenState.name.apply {
            value = variant?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        return@async variant != null
    }
        .await()

    /**
     * Tries to update variant with provided [variantId] with current screen state data
     * @return resulting [UpdateResult]
     */
    suspend fun updateVariant(variantId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = variantRepository.update(
            variantId,
            screenState.name.value.data.orEmpty()
        )

        if (result.isError()) {
            when (result.error!!) {
                is UpdateResult.InvalidName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                is UpdateResult.DuplicateName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.DuplicateValueError)
                    }
                }

                is UpdateResult.InvalidProductId -> {
                    Log.e(
                        "InvalidId",
                        "This is impossible, tried to update variant with invalid productId in AddVariantViewModel, productId is taken from the database"
                    )

                    return@async UpdateResult.Success
                }

                is UpdateResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to update variant with invalid id in AddVariantViewModel"
                    )

                    return@async UpdateResult.Success
                }
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete variant with provided [variantId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return resulting [DeleteResult]
     */
    suspend fun deleteVariant(variantId: Long) = viewModelScope.async {

        val result = variantRepository.delete(variantId)

        if (result.isError()) {
            when (result.error!!) {
                DeleteResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to delete variant with invalid variant id in EditVariantViewModel"
                    )
                    return@async DeleteResult.Success
                }
            }
        }

        return@async result
    }
        .await()
}
