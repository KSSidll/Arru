package com.kssidll.arru.ui.screen.modify.productvariant.editproductvariant

import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class EditProductVariantViewModel
@Inject
constructor(override val variantRepository: ProductVariantRepositorySource) :
    ModifyProductVariantViewModel() {
    private var mVariant: ProductVariantEntity? = null

    suspend fun checkExists(id: Long) =
        viewModelScope
            .async {
                return@async variantRepository.get(id).first() != null
            }
            .await()

    fun updateState(variantId: Long) =
        viewModelScope.launch {
            // skip state update for repeating variantId
            if (variantId == mVariant?.id) return@launch

            screenState.name.apply { value = value.toLoading() }

            val variant = variantRepository.get(variantId).first()

            screenState.name.apply {
                value = variant?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
            }

            screenState.isVariantGlobal.apply {
                value = Field.Loading(variant?.productEntityId == null)
            }
        }

    /**
     * Tries to update variant with provided [variantId] with current screen state data
     *
     * @return resulting [UpdateResult]
     */
    // suspend fun updateVariant(variantId: Long) =
    //     viewModelScope
    //         .async {
    //             screenState.attemptedToSubmit.value = true
    //
    //             val result =
    //                 variantRepository.update(variantId, screenState.name.value.data.orEmpty())
    //
    //             if (result.isError()) {
    //                 when (result.error!!) {
    //                     is UpdateResult.InvalidName -> {
    //                         screenState.name.apply {
    //                             value = value.toError(FieldError.InvalidValueError)
    //                         }
    //                     }
    //
    //                     is UpdateResult.DuplicateName -> {
    //                         screenState.name.apply {
    //                             value = value.toError(FieldError.DuplicateValueError)
    //                         }
    //                     }
    //
    //                     is UpdateResult.InvalidProductId -> {
    //                         Log.e(
    //                             "InvalidId",
    //                             "This is impossible, tried to update variant with invalid
    // productId in AddVariantViewModel, productId is taken from the database",
    //                         )
    //
    //                         return@async UpdateResult.Success
    //                     }
    //
    //                     is UpdateResult.InvalidId -> {
    //                         Log.e(
    //                             "InvalidId",
    //                             "Tried to update variant with invalid id in AddVariantViewModel",
    //                         )
    //
    //                         return@async UpdateResult.Success
    //                     }
    //                 }
    //             }
    //
    //             return@async result
    //         }
    //         .await()
    //
    // /**
    //  * Tries to delete variant with provided [variantId], sets showDeleteWarning flag in state if
    //  * operation would require deleting foreign constrained data, state deleteWarningConfirmed
    // flag
    //  * needs to be set to start foreign constrained data deletion
    //  *
    //  * @return resulting [DeleteResult]
    //  */
    // suspend fun deleteVariant(variantId: Long) =
    //     viewModelScope
    //         .async {
    //             val result =
    //                 variantRepository.delete(variantId, screenState.deleteWarningConfirmed.value)
    //
    //             if (result.isError()) {
    //                 when (result.error!!) {
    //                     DeleteResult.InvalidId -> {
    //                         Log.e(
    //                             "InvalidId",
    //                             "Tried to delete variant with invalid variant id in
    // EditVariantViewModel",
    //                         )
    //                         return@async DeleteResult.Success
    //                     }
    //
    //                     DeleteResult.DangerousDelete -> {
    //                         screenState.showDeleteWarning.value = true
    //                     }
    //                 }
    //             }
    //
    //             return@async result
    //         }
    //         .await()
}
