package com.kssidll.arru.ui.screen.modify.variant.editvariant


import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.repository.VariantRepositorySource
import com.kssidll.arru.data.repository.VariantRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.VariantRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.variant.ModifyVariantViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@HiltViewModel
class EditVariantViewModel @Inject constructor(
    override val variantRepository: VariantRepositorySource,
): ModifyVariantViewModel() {
    private var mVariant: ProductVariantEntity? = null

    /**
     * Updates data in the screen state
     * @return true if provided [variantId] was valid, false otherwise
     */
    suspend fun updateState(variantId: Long) = viewModelScope.async {
        // skip state update for repeating variantId
        if (variantId == mVariant?.id) return@async true

        screenState.name.apply { value = value.toLoading() }

        val variant = variantRepository.get(variantId).first()

        screenState.name.apply {
            value = variant?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        screenState.isVariantGlobal.apply {
            value = Field.Loading(variant?.productEntityId == null)
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

        val result = variantRepository.delete(
            variantId,
            screenState.deleteWarningConfirmed.value
        )

        if (result.isError()) {
            when (result.error!!) {
                DeleteResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to delete variant with invalid variant id in EditVariantViewModel"
                    )
                    return@async DeleteResult.Success
                }

                DeleteResult.DangerousDelete -> {
                    screenState.showDeleteWarning.value = true
                }
            }
        }

        return@async result
    }
        .await()
}
