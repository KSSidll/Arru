package com.kssidll.arru.ui.screen.modify.product.editproduct

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteProductEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.GetAllProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetAllProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetAllProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.product.ModifyProductEvent
import com.kssidll.arru.ui.screen.modify.product.ModifyProductEventResult
import com.kssidll.arru.ui.screen.modify.product.ModifyProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditProductViewModel
@Inject
constructor(
    private val getProductEntityUseCase: GetProductEntityUseCase,
    private val updateProductEntityUseCase: UpdateProductEntityUseCase,
    private val deleteProductEntityUseCase: DeleteProductEntityUseCase,
    private val mergeProductEntityUseCase: MergeProductEntityUseCase,
    override val getAllProductEntityUseCase: GetAllProductEntityUseCase,
    override val getAllProductCategoryEntityUseCase: GetAllProductCategoryEntityUseCase,
    override val getAllProductProducerEntityUseCase: GetAllProductProducerEntityUseCase,
    override val getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase,
    override val getProductProducerEntityUseCase: GetProductProducerEntityUseCase,
) : ModifyProductViewModel() {
    suspend fun checkExists(id: Long): Boolean {
        return getProductEntityUseCase(id).first() != null
    }

    fun updateState(productId: Long) =
        viewModelScope.launch {
            val state = uiState.value
            // skip state update for repeating productId
            if (productId == state.currentProduct?.id) return@launch

            _uiState.update { currentState ->
                currentState.copy(
                    name = currentState.name.toLoading(),
                    selectedProductProducer = currentState.selectedProductProducer.toLoading(),
                    selectedProductCategory = currentState.selectedProductCategory.toLoading(),
                )
            }

            val product = getProductEntityUseCase(productId).first()

            _uiState.update { currentState ->
                currentState.copy(
                    currentProduct = product,
                    name = product?.name?.let { Field.Loaded(it) } ?: currentState.name.toLoaded(),
                )
            }

            setProductProducerListener(product?.productProducerEntityId)
            setProductCategoryListener(product?.productCategoryEntityId)
        }

    init {
        init()

        _uiState.update { currentState ->
            currentState.copy(isDeleteEnabled = true, isMergeEnabled = true)
        }
    }

    override suspend fun handleEvent(event: ModifyProductEvent): ModifyProductEventResult {
        return when (event) {
            is ModifyProductEvent.SetDangerousDeleteDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogVisible = event.visible)
                }

                ModifyProductEventResult.Success
            }
            is ModifyProductEvent.SetDangerousDeleteDialogConfirmation -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogConfirmed = event.confirmed)
                }

                ModifyProductEventResult.Success
            }
            is ModifyProductEvent.SetMergeSearchDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeSearchDialogVisible = event.visible)
                }

                ModifyProductEventResult.Success
            }
            is ModifyProductEvent.SetMergeConfirmationDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeConfirmationDialogVisible = event.visible)
                }

                ModifyProductEventResult.Success
            }
            is ModifyProductEvent.SelectMergeCandidate -> {
                _uiState.update { currentState ->
                    currentState.copy(selectedMergeCandidate = event.mergeInto)
                }

                ModifyProductEventResult.Success
            }
            is ModifyProductEvent.DeleteProduct -> {
                val state = uiState.value
                val result =
                    state.currentProduct?.let { product ->
                        deleteProductEntityUseCase(
                            id = product.id,
                            state.isDangerousDeleteDialogConfirmed,
                        )
                    } ?: return ModifyProductEventResult.SuccessDelete

                when (result) {
                    is DeleteProductEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                DeleteProductEntityUseCaseResult.ProductIdInvalid -> {
                                    Log.e(
                                        "ModifyProduct",
                                        "Delete invalid product id `${state.currentProduct.id}`",
                                    )
                                }
                                DeleteProductEntityUseCaseResult.DangerousDelete -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(isDangerousDeleteDialogVisible = true)
                                    }
                                }
                            }
                        }

                        ModifyProductEventResult.Failure
                    }
                    is DeleteProductEntityUseCaseResult.Success -> {
                        ModifyProductEventResult.SuccessDelete
                    }
                }
            }
            is ModifyProductEvent.MergeProduct -> {
                val state = uiState.value

                if (state.currentProduct == null) {
                    Log.e("ModifyProduct", "Tried to merge product without being set")
                    return ModifyProductEventResult.Failure
                }

                if (state.selectedMergeCandidate == null) {
                    Log.e("ModifyProduct", "Tried to merge product without merge being set")
                    return ModifyProductEventResult.Failure
                }

                val result =
                    mergeProductEntityUseCase(
                        state.currentProduct.id,
                        state.selectedMergeCandidate.id,
                    )

                when (result) {
                    is MergeProductEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                MergeProductEntityUseCaseResult.MergeIntoIdInvalid -> {
                                    Log.e(
                                        "ModifyProduct",
                                        "Tried to merge product but merge id was invalid",
                                    )
                                }
                                MergeProductEntityUseCaseResult.ProductIdInvalid -> {
                                    Log.e(
                                        "ModifyProduct",
                                        "Tried to merge product but id was invalid",
                                    )
                                }
                            }
                        }

                        ModifyProductEventResult.Failure
                    }
                    is MergeProductEntityUseCaseResult.Success -> {
                        ModifyProductEventResult.SuccessMerge(result.mergedEntity.id)
                    }
                }
            }
            is ModifyProductEvent.Submit -> {
                val state = uiState.value

                val result =
                    state.currentProduct?.id?.let {
                        updateProductEntityUseCase(
                            id = it,
                            name = state.name.data,
                            productProducerEntityId = state.selectedProductProducer.data?.id,
                            productCategoryEntityId = state.selectedProductCategory.data?.id,
                        )
                    } ?: return ModifyProductEventResult.SuccessUpdate

                when (result) {
                    is UpdateProductEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                UpdateProductEntityUseCaseResult.ProductIdInvalid -> {
                                    Log.e(
                                        "ModifyProduct",
                                        "Update invalid product `${state.currentProduct.id}`",
                                    )
                                }
                                UpdateProductEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }
                                UpdateProductEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                                UpdateProductEntityUseCaseResult.ProductCategoryIdInvalid -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProductCategory =
                                                currentState.selectedProductCategory.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                UpdateProductEntityUseCaseResult.ProductCategoryNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProductCategory =
                                                currentState.selectedProductCategory.toError(
                                                    FieldError.NoValueError
                                                )
                                        )
                                    }
                                }
                                UpdateProductEntityUseCaseResult.ProductProducerIdInvalid -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProductProducer =
                                                currentState.selectedProductProducer.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        ModifyProductEventResult.Failure
                    }
                    is UpdateProductEntityUseCaseResult.Success -> {
                        ModifyProductEventResult.SuccessUpdate
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
