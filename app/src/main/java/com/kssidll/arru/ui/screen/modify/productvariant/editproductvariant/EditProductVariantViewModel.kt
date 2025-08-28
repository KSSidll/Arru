package com.kssidll.arru.ui.screen.modify.productvariant.editproductvariant

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteProductVariantEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductVariantEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class EditProductVariantViewModel
@Inject
constructor(
    override val variantRepository: ProductVariantRepositorySource,
    private val updateProductVariantEntityUseCase: UpdateProductVariantEntityUseCase,
    private val deleteProductVariantEntityUseCase: DeleteProductVariantEntityUseCase,
) : ModifyProductVariantViewModel() {
    private var mVariant: ProductVariantEntity? = null

    suspend fun checkExists(id: Long): Boolean {
        return variantRepository.get(id).first() != null
    }

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

    suspend fun updateVariant(variantId: Long): Boolean {
        screenState.attemptedToSubmit.value = true

        val result =
            updateProductVariantEntityUseCase(id = variantId, name = screenState.name.value.data)

        return when (result) {
            is UpdateProductVariantEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        UpdateProductVariantEntityUseCaseResult.ProductVariantIdInvalid -> {
                            Log.e(
                                "ModifyProductProducer",
                                "Insert invalid product producer `${variantId}`",
                            )
                        }
                        UpdateProductVariantEntityUseCaseResult.NameDuplicateValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.DuplicateValueError)
                            }
                        }
                        UpdateProductVariantEntityUseCaseResult.NameNoValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                    }
                }

                false
            }
            is UpdateProductVariantEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun deleteVariant(variantId: Long): Boolean {
        val result =
            deleteProductVariantEntityUseCase(variantId, screenState.deleteWarningConfirmed.value)

        return when (result) {
            is DeleteProductVariantEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        DeleteProductVariantEntityUseCaseResult.DangerousDelete -> {
                            screenState.showDeleteWarning.value = true
                        }
                        DeleteProductVariantEntityUseCaseResult.ProductVariantIdInvalid -> {
                            Log.e(
                                "ModifyProductVariant",
                                "Tried to delete product variant with invalid id",
                            )
                        }
                    }
                }

                false
            }
            is DeleteProductVariantEntityUseCaseResult.Success -> {
                true
            }
        }
    }
}
