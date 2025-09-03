package com.kssidll.arru.ui.screen.modify.productvariant.editproductvariant

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteProductVariantEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.GetAllGlobalProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductVariantEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductVariantEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantEvent
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantEventResult
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditProductVariantViewModel
@Inject
constructor(
    private val updateProductVariantEntityUseCase: UpdateProductVariantEntityUseCase,
    private val deleteProductVariantEntityUseCase: DeleteProductVariantEntityUseCase,
    private val mergeProductVariantEntityUseCase: MergeProductVariantEntityUseCase,
    private val getProductVariantEntityUseCase: GetProductVariantEntityUseCase,
    override val getProductEntityUseCase: GetProductEntityUseCase,
    override val getProductVariantEntityByProductUseCase: GetProductVariantEntityByProductUseCase,
    override val getAllGlobalProductVariantEntityUseCase: GetAllGlobalProductVariantEntityUseCase,
) : ModifyProductVariantViewModel() {
    suspend fun checkExists(id: Long): Boolean {
        return getProductVariantEntityUseCase(id).first() != null
    }

    fun updateState(variantId: Long) =
        viewModelScope.launch {
            val state = _uiState.value
            // skip state update for repeating variantId
            if (variantId == state.currentProductVariant?.id) return@launch

            _uiState.update { currentState ->
                currentState.copy(
                    name = currentState.name.toLoading(),
                    isVariantGlobal = currentState.isVariantGlobal.toLoading(),
                )
            }

            val variant = getProductVariantEntityUseCase(variantId).first()

            _uiState.update { currentState ->
                currentState.copy(
                    currentProductVariant = variant,
                    name = variant?.name?.let { Field.Loaded(it) } ?: currentState.name.toLoaded(),
                    isVariantGlobal = Field.Loaded(variant?.productEntityId == null),
                )
            }

            setAndCheckProduct(variant?.productEntityId)
        }

    init {
        _uiState.update { currentState ->
            currentState.copy(isDeleteEnabled = true, isMergeEnabled = true)
        }
    }

    override suspend fun handleEvent(
        event: ModifyProductVariantEvent
    ): ModifyProductVariantEventResult {
        return when (event) {
            is ModifyProductVariantEvent.SetDangerousDeleteDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogVisible = event.visible)
                }

                ModifyProductVariantEventResult.Success
            }
            is ModifyProductVariantEvent.SetDangerousDeleteDialogConfirmation -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogConfirmed = event.confirmed)
                }

                ModifyProductVariantEventResult.Success
            }
            is ModifyProductVariantEvent.SetMergeSearchDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeSearchDialogVisible = event.visible)
                }

                ModifyProductVariantEventResult.Success
            }
            is ModifyProductVariantEvent.SetMergeConfirmationDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeConfirmationDialogVisible = event.visible)
                }

                ModifyProductVariantEventResult.Success
            }
            is ModifyProductVariantEvent.SelectMergeCandidate -> {
                _uiState.update { currentState ->
                    currentState.copy(selectedMergeCandidate = event.mergeInto)
                }

                ModifyProductVariantEventResult.Success
            }
            is ModifyProductVariantEvent.DeleteProductVariant -> {
                val state = uiState.value
                val result =
                    state.currentProductVariant?.let { productVariant ->
                        deleteProductVariantEntityUseCase(
                            id = productVariant.id,
                            state.isDangerousDeleteDialogConfirmed,
                        )
                    } ?: return ModifyProductVariantEventResult.SuccessDelete

                when (result) {
                    is DeleteProductVariantEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                DeleteProductVariantEntityUseCaseResult.ProductVariantIdInvalid -> {
                                    Log.e(
                                        "ModifyProductVariant",
                                        "Delete invalid product variant id `${state.currentProductVariant.id}`",
                                    )
                                }
                                DeleteProductVariantEntityUseCaseResult.DangerousDelete -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(isDangerousDeleteDialogVisible = true)
                                    }
                                }
                            }
                        }

                        ModifyProductVariantEventResult.Failure
                    }
                    is DeleteProductVariantEntityUseCaseResult.Success -> {
                        ModifyProductVariantEventResult.SuccessDelete
                    }
                }
            }
            is ModifyProductVariantEvent.MergeProductVariant -> {
                val state = uiState.value

                if (state.currentProductVariant == null) {
                    Log.e(
                        "ModifyProductVariant",
                        "Tried to merge product variant without being set",
                    )
                    return ModifyProductVariantEventResult.Failure
                }

                if (state.selectedMergeCandidate == null) {
                    Log.e(
                        "ModifyProductVariant",
                        "Tried to merge product variant without merge being set",
                    )
                    return ModifyProductVariantEventResult.Failure
                }

                val result =
                    mergeProductVariantEntityUseCase(
                        state.currentProductVariant.id,
                        state.selectedMergeCandidate.id,
                    )

                when (result) {
                    is MergeProductVariantEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                MergeProductVariantEntityUseCaseResult.MergeIntoIdInvalid -> {
                                    Log.e(
                                        "ModifyProductVariant",
                                        "Tried to merge product variant but merge id was invalid",
                                    )
                                }
                                MergeProductVariantEntityUseCaseResult.ProductVariantIdInvalid -> {
                                    Log.e(
                                        "ModifyProductVariant",
                                        "Tried to merge product variant but id was invalid",
                                    )
                                }
                                MergeProductVariantEntityUseCaseResult.MergeGlobalIntoLocal -> {
                                    Log.e(
                                        "ModifyProductVariant",
                                        "Tried to merge global product variant into local",
                                    )
                                }
                                MergeProductVariantEntityUseCaseResult
                                    .MergeLocalIntoLocalDifferentProductId -> {
                                    Log.e(
                                        "ModifyProductVariant",
                                        "Tried to merge local product variant into local but product id differed",
                                    )
                                }
                            }
                        }

                        ModifyProductVariantEventResult.Failure
                    }
                    is MergeProductVariantEntityUseCaseResult.Success -> {
                        ModifyProductVariantEventResult.SuccessMerge(result.mergedEntity.id)
                    }
                }
            }
            is ModifyProductVariantEvent.Submit -> {
                val state = uiState.value

                val result =
                    state.currentProductVariant?.id?.let {
                        updateProductVariantEntityUseCase(id = it, name = state.name.data)
                    } ?: return ModifyProductVariantEventResult.SuccessUpdate

                when (result) {
                    is UpdateProductVariantEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                UpdateProductVariantEntityUseCaseResult.ProductVariantIdInvalid -> {
                                    Log.e(
                                        "ModifyProductVariant",
                                        "Update invalid product variant `${state.currentProductVariant.id}`",
                                    )
                                }
                                UpdateProductVariantEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }
                                UpdateProductVariantEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                            }
                        }

                        ModifyProductVariantEventResult.Failure
                    }
                    is UpdateProductVariantEntityUseCaseResult.Success -> {
                        ModifyProductVariantEventResult.SuccessUpdate
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
