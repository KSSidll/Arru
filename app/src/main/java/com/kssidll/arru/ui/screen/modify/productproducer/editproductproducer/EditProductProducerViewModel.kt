package com.kssidll.arru.ui.screen.modify.productproducer.editproductproducer

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteProductProducerEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.GetAllProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeProductProducerEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateProductProducerEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerEvent
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerEventResult
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditProductProducerViewModel
@Inject
constructor(
    private val updateProductProducerEntityUseCase: UpdateProductProducerEntityUseCase,
    private val mergeProductProducerEntityUseCase: MergeProductProducerEntityUseCase,
    private val deleteProductProducerEntityUseCase: DeleteProductProducerEntityUseCase,
    private val getProductProducerEntityUseCase: GetProductProducerEntityUseCase,
    override val getAllProductProducerEntityUseCase: GetAllProductProducerEntityUseCase,
) : ModifyProductProducerViewModel() {
    suspend fun checkExists(id: Long): Boolean {
        return getProductProducerEntityUseCase(id).first() != null
    }

    fun updateState(producerId: Long) =
        viewModelScope.launch {
            val state = uiState.value

            // skip state update for repeating producerId
            if (producerId == state.currentProductProducer?.id) return@launch

            _uiState.update { currentState ->
                currentState.copy(name = currentState.name.toLoading())
            }

            val productProducer = getProductProducerEntityUseCase(producerId).first()
            _uiState.update { currentState ->
                currentState.copy(
                    currentProductProducer = productProducer,
                    name = Field.Loaded(productProducer?.name),
                )
            }
        }

    init {
        init()

        _uiState.update { currentState ->
            currentState.copy(isDeleteVisible = true, isMergeVisible = true)
        }
    }

    override suspend fun handleEvent(
        event: ModifyProductProducerEvent
    ): ModifyProductProducerEventResult {
        return when (event) {
            is ModifyProductProducerEvent.SetDangerousDeleteDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogVisible = event.visible)
                }

                ModifyProductProducerEventResult.Success
            }
            is ModifyProductProducerEvent.SetDangerousDeleteDialogConfirmation -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogConfirmed = event.confirmed)
                }

                ModifyProductProducerEventResult.Success
            }
            is ModifyProductProducerEvent.SetMergeSearchDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeSearchDialogVisible = event.visible)
                }

                ModifyProductProducerEventResult.Success
            }
            is ModifyProductProducerEvent.SetMergeConfirmationDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeConfirmationDialogVisible = event.visible)
                }

                ModifyProductProducerEventResult.Success
            }
            is ModifyProductProducerEvent.SelectMergeCandidate -> {
                _uiState.update { currentState ->
                    currentState.copy(selectedMergeCandidate = event.mergeInto)
                }

                ModifyProductProducerEventResult.Success
            }
            is ModifyProductProducerEvent.DeleteProductProducer -> {
                val state = uiState.value
                val result =
                    state.currentProductProducer?.let { productProducer ->
                        deleteProductProducerEntityUseCase(
                            id = productProducer.id,
                            state.isDangerousDeleteDialogConfirmed,
                        )
                    } ?: return ModifyProductProducerEventResult.SuccessDelete

                when (result) {
                    is DeleteProductProducerEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                DeleteProductProducerEntityUseCaseResult
                                    .ProductProducerIdInvalid -> {
                                    Log.e(
                                        "ModifyProductProducer",
                                        "Delete invalid product producer id `${state.currentProductProducer.id}`",
                                    )
                                }
                                DeleteProductProducerEntityUseCaseResult.DangerousDelete -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(isDangerousDeleteDialogVisible = true)
                                    }
                                }
                            }
                        }

                        ModifyProductProducerEventResult.Failure
                    }
                    is DeleteProductProducerEntityUseCaseResult.Success -> {
                        ModifyProductProducerEventResult.SuccessDelete
                    }
                }
            }
            is ModifyProductProducerEvent.MergeProductProducer -> {
                val state = uiState.value

                if (state.currentProductProducer == null) {
                    Log.e(
                        "ModifyProductProducer",
                        "Tried to merge product producer without being set",
                    )
                    return ModifyProductProducerEventResult.Failure
                }

                if (state.selectedMergeCandidate == null) {
                    Log.e(
                        "ModifyProductProducer",
                        "Tried to merge product producer without merge being set",
                    )
                    return ModifyProductProducerEventResult.Failure
                }

                val result =
                    mergeProductProducerEntityUseCase(
                        state.currentProductProducer.id,
                        state.selectedMergeCandidate.id,
                    )

                when (result) {
                    is MergeProductProducerEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                MergeProductProducerEntityUseCaseResult.MergeIntoIdInvalid -> {
                                    Log.e(
                                        "ModifyProductProducer",
                                        "Tried to merge product producer but merge id was invalid",
                                    )
                                }
                                MergeProductProducerEntityUseCaseResult
                                    .ProductProducerIdInvalid -> {
                                    Log.e(
                                        "ModifyProductProducer",
                                        "Tried to merge product producer but id was invalid",
                                    )
                                }
                            }
                        }

                        ModifyProductProducerEventResult.Failure
                    }
                    is MergeProductProducerEntityUseCaseResult.Success -> {
                        ModifyProductProducerEventResult.SuccessMerge(result.mergedEntity.id)
                    }
                }
            }
            is ModifyProductProducerEvent.Submit -> {
                val state = uiState.value

                val result =
                    state.currentProductProducer?.id?.let {
                        updateProductProducerEntityUseCase(id = it, name = state.name.data)
                    } ?: return ModifyProductProducerEventResult.SuccessUpdate

                when (result) {
                    is UpdateProductProducerEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                UpdateProductProducerEntityUseCaseResult
                                    .ProductProducerIdInvalid -> {
                                    Log.e(
                                        "ModifyProductProducer",
                                        "Update invalid product producer `${state.currentProductProducer.id}`",
                                    )
                                }
                                UpdateProductProducerEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }
                                UpdateProductProducerEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                            }
                        }

                        ModifyProductProducerEventResult.Failure
                    }
                    is UpdateProductProducerEntityUseCaseResult.Success -> {
                        ModifyProductProducerEventResult.SuccessUpdate
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
