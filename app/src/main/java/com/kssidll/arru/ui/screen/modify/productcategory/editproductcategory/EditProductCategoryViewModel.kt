package com.kssidll.arru.ui.screen.modify.productcategory.editproductcategory

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteProductCategoryEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.GetAllProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductCategoryEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductCategoryEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryEvent
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryEventResult
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditProductCategoryViewModel
@Inject
constructor(
    private val getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase,
    private val updateProductCategoryEntityUseCase: UpdateProductCategoryEntityUseCase,
    private val mergeProductCategoryEntityUseCase: MergeProductCategoryEntityUseCase,
    private val deleteProductCategoryEntityUseCase: DeleteProductCategoryEntityUseCase,
    override val getAllProductCategoryEntityUseCase: GetAllProductCategoryEntityUseCase,
) : ModifyProductCategoryViewModel() {
    suspend fun checkExists(id: Long): Boolean {
        return getProductCategoryEntityUseCase(id).first() != null
    }

    fun updateState(categoryId: Long) =
        viewModelScope.launch {
            val state = uiState.value

            // skip state update for repeating categoryId
            if (categoryId == state.currentProductCategory?.id) return@launch

            _uiState.update { currentState ->
                currentState.copy(name = currentState.name.toLoading())
            }

            val productCategory = getProductCategoryEntityUseCase(categoryId).first()
            _uiState.update { currentState ->
                currentState.copy(
                    currentProductCategory = productCategory,
                    name = Field.Loaded(productCategory?.name),
                )
            }
        }

    init {
        init()

        _uiState.update { currentState ->
            currentState.copy(isDeleteEnabled = true, isMergeEnabled = true)
        }
    }

    override suspend fun handleEvent(
        event: ModifyProductCategoryEvent
    ): ModifyProductCategoryEventResult {
        return when (event) {
            is ModifyProductCategoryEvent.SetDangerousDeleteDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogVisible = event.visible)
                }

                ModifyProductCategoryEventResult.Success
            }
            is ModifyProductCategoryEvent.SetDangerousDeleteDialogConfirmation -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogConfirmed = event.confirmed)
                }

                ModifyProductCategoryEventResult.Success
            }
            is ModifyProductCategoryEvent.SetMergeSearchDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeSearchDialogVisible = event.visible)
                }

                ModifyProductCategoryEventResult.Success
            }
            is ModifyProductCategoryEvent.SetMergeConfirmationDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeConfirmationDialogVisible = event.visible)
                }

                ModifyProductCategoryEventResult.Success
            }
            is ModifyProductCategoryEvent.SelectMergeCandidate -> {
                _uiState.update { currentState ->
                    currentState.copy(selectedMergeCandidate = event.mergeInto)
                }

                ModifyProductCategoryEventResult.Success
            }
            is ModifyProductCategoryEvent.DeleteProductCategory -> {
                val state = uiState.value
                val result =
                    state.currentProductCategory?.let { productCategory ->
                        deleteProductCategoryEntityUseCase(
                            id = productCategory.id,
                            state.isDangerousDeleteDialogConfirmed,
                        )
                    } ?: return ModifyProductCategoryEventResult.SuccessDelete

                when (result) {
                    is DeleteProductCategoryEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                DeleteProductCategoryEntityUseCaseResult
                                    .ProductCategoryIdInvalid -> {
                                    Log.e(
                                        "ModifyProductCategory",
                                        "Delete invalid product category id `${state.currentProductCategory.id}`",
                                    )
                                }
                                DeleteProductCategoryEntityUseCaseResult.DangerousDelete -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(isDangerousDeleteDialogVisible = true)
                                    }
                                }
                            }
                        }

                        ModifyProductCategoryEventResult.Failure
                    }
                    is DeleteProductCategoryEntityUseCaseResult.Success -> {
                        ModifyProductCategoryEventResult.SuccessDelete
                    }
                }
            }
            is ModifyProductCategoryEvent.MergeProductCategory -> {
                val state = uiState.value

                if (state.currentProductCategory == null) {
                    Log.e(
                        "ModifyProductCategory",
                        "Tried to merge product category without being set",
                    )
                    return ModifyProductCategoryEventResult.Failure
                }

                if (state.selectedMergeCandidate == null) {
                    Log.e(
                        "ModifyProductCategory",
                        "Tried to merge product category without merge being set",
                    )
                    return ModifyProductCategoryEventResult.Failure
                }

                val result =
                    mergeProductCategoryEntityUseCase(
                        state.currentProductCategory.id,
                        state.selectedMergeCandidate.id,
                    )

                when (result) {
                    is MergeProductCategoryEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                MergeProductCategoryEntityUseCaseResult.MergeIntoIdInvalid -> {
                                    Log.e(
                                        "ModifyProductCategory",
                                        "Tried to merge product category but merge id was invalid",
                                    )
                                }
                                MergeProductCategoryEntityUseCaseResult
                                    .ProductCategoryIdInvalid -> {
                                    Log.e(
                                        "ModifyProductCategory",
                                        "Tried to merge product category but id was invalid",
                                    )
                                }
                            }
                        }

                        ModifyProductCategoryEventResult.Failure
                    }
                    is MergeProductCategoryEntityUseCaseResult.Success -> {
                        ModifyProductCategoryEventResult.SuccessMerge(result.mergedEntity.id)
                    }
                }
            }
            is ModifyProductCategoryEvent.Submit -> {
                val state = uiState.value

                val result =
                    state.currentProductCategory?.id?.let {
                        updateProductCategoryEntityUseCase(id = it, name = state.name.data)
                    } ?: return ModifyProductCategoryEventResult.SuccessUpdate

                when (result) {
                    is UpdateProductCategoryEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                UpdateProductCategoryEntityUseCaseResult
                                    .ProductCategoryIdInvalid -> {
                                    Log.e(
                                        "ModifyProductCategory",
                                        "Update invalid product category `${state.currentProductCategory.id}`",
                                    )
                                }
                                UpdateProductCategoryEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }
                                UpdateProductCategoryEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                            }
                        }

                        ModifyProductCategoryEventResult.Failure
                    }
                    is UpdateProductCategoryEntityUseCaseResult.Success -> {
                        ModifyProductCategoryEventResult.SuccessUpdate
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
