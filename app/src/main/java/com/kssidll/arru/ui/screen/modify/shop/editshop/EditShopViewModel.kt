package com.kssidll.arru.ui.screen.modify.shop.editshop

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteShopEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.GetAllShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.MergeShopEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateShopEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopEvent
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopEventResult
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditShopViewModel
@Inject
constructor(
    private val updateShopEntityUseCase: UpdateShopEntityUseCase,
    private val mergeShopEntityUseCase: MergeShopEntityUseCase,
    private val deleteShopEntityUseCase: DeleteShopEntityUseCase,
    private val getShopEntityUseCase: GetShopEntityUseCase,
    override val getAllShopEntityUseCase: GetAllShopEntityUseCase,
) : ModifyShopViewModel() {

    suspend fun checkExists(id: Long): Boolean {
        return getShopEntityUseCase(id).first() != null
    }

    fun updateState(shopId: Long) =
        viewModelScope.launch {
            val state = _uiState.value

            // skip state update for repeating shopId
            if (shopId == state.currentShop?.id) return@launch

            _uiState.update { currentState ->
                currentState.copy(name = currentState.name.toLoading())
            }

            val shop = getShopEntityUseCase(shopId).first()

            _uiState.update { currentState ->
                currentState.copy(
                    currentShop = shop,
                    name = shop?.name?.let { Field.Loaded(it) } ?: currentState.name.toLoaded(),
                )
            }
        }

    init {
        init()

        _uiState.update { currentState ->
            currentState.copy(isDeleteEnabled = true, isMergeEnabled = true)
        }
    }

    override suspend fun handleEvent(event: ModifyShopEvent): ModifyShopEventResult {
        return when (event) {
            is ModifyShopEvent.SetDangerousDeleteDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogVisible = event.visible)
                }

                ModifyShopEventResult.Success
            }
            is ModifyShopEvent.SetDangerousDeleteDialogConfirmation -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogConfirmed = event.confirmed)
                }

                ModifyShopEventResult.Success
            }
            is ModifyShopEvent.SetMergeSearchDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeSearchDialogVisible = event.visible)
                }

                ModifyShopEventResult.Success
            }
            is ModifyShopEvent.SetMergeConfirmationDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isMergeConfirmationDialogVisible = event.visible)
                }

                ModifyShopEventResult.Success
            }
            is ModifyShopEvent.SelectMergeCandidate -> {
                _uiState.update { currentState ->
                    currentState.copy(selectedMergeCandidate = event.mergeInto)
                }

                ModifyShopEventResult.Success
            }
            is ModifyShopEvent.DeleteShop -> {
                val state = uiState.value
                val result =
                    state.currentShop?.let { productProducer ->
                        deleteShopEntityUseCase(
                            id = productProducer.id,
                            state.isDangerousDeleteDialogConfirmed,
                        )
                    } ?: return ModifyShopEventResult.SuccessDelete

                when (result) {
                    is DeleteShopEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                DeleteShopEntityUseCaseResult.ShopIdInvalid -> {
                                    Log.e(
                                        "ModifyShop",
                                        "Delete invalid shop id `${state.currentShop.id}`",
                                    )
                                }
                                DeleteShopEntityUseCaseResult.DangerousDelete -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(isDangerousDeleteDialogVisible = true)
                                    }
                                }
                            }
                        }

                        ModifyShopEventResult.Failure
                    }
                    is DeleteShopEntityUseCaseResult.Success -> {
                        ModifyShopEventResult.SuccessDelete
                    }
                }
            }
            is ModifyShopEvent.MergeShop -> {
                val state = uiState.value

                if (state.currentShop == null) {
                    Log.e("ModifyShop", "Tried to merge shop without being set")
                    return ModifyShopEventResult.Failure
                }

                if (state.selectedMergeCandidate == null) {
                    Log.e("ModifyShop", "Tried to merge shop without merge being set")
                    return ModifyShopEventResult.Failure
                }

                val result =
                    mergeShopEntityUseCase(state.currentShop.id, state.selectedMergeCandidate.id)

                when (result) {
                    is MergeShopEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                MergeShopEntityUseCaseResult.MergeIntoIdInvalid -> {
                                    Log.e(
                                        "ModifyShop",
                                        "Tried to merge shop but merge id was invalid",
                                    )
                                }
                                MergeShopEntityUseCaseResult.ShopIdInvalid -> {
                                    Log.e("ModifyShop", "Tried to merge shop but id was invalid")
                                }
                            }
                        }

                        ModifyShopEventResult.Failure
                    }
                    is MergeShopEntityUseCaseResult.Success -> {
                        ModifyShopEventResult.SuccessMerge(result.mergedEntity.id)
                    }
                }
            }
            is ModifyShopEvent.Submit -> {
                val state = uiState.value

                val result =
                    state.currentShop?.id?.let {
                        updateShopEntityUseCase(id = it, name = state.name.data)
                    } ?: return ModifyShopEventResult.SuccessUpdate

                when (result) {
                    is UpdateShopEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                UpdateShopEntityUseCaseResult.ShopIdInvalid -> {
                                    Log.e(
                                        "ModifyShop",
                                        "Update invalid shop `${state.currentShop.id}`",
                                    )
                                }
                                UpdateShopEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }
                                UpdateShopEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                            }
                        }

                        ModifyShopEventResult.Failure
                    }
                    is UpdateShopEntityUseCaseResult.Success -> {
                        ModifyShopEventResult.SuccessUpdate
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
