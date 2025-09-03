package com.kssidll.arru.ui.screen.modify.transaction

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetShopEntityUseCase
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.StringHelper
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ModifyTransactionUiState(
    val currentTransaction: TransactionEntity? = null,
    val allShops: ImmutableList<ShopEntity> = emptyImmutableList(),
    val selectedShop: Field<ShopEntity?> = Field.Loaded(null),
    val date: Field<Long?> = Field.Loaded(null),
    val totalCost: Field<String> = Field.Loaded(String()),
    val note: Field<String?> = Field.Loaded(null),
    val isDatePickerDialogVisible: Boolean = false,
    val isShopSearchDialogVisible: Boolean = false,
    val isDeleteEnabled: Boolean = false,
    val isDangerousDeleteDialogVisible: Boolean = false,
    val isDangerousDeleteDialogConfirmed: Boolean = false,
)

@Immutable
sealed class ModifyTransactionEvent {
    data object NavigateBack : ModifyTransactionEvent()

    data object Submit : ModifyTransactionEvent()

    data object DeleteTransaction : ModifyTransactionEvent()

    data class SetDangerousDeleteDialogVisibility(val visible: Boolean) : ModifyTransactionEvent()

    data class SetDangerousDeleteDialogConfirmation(val confirmed: Boolean) :
        ModifyTransactionEvent()

    data class SelectShop(val shopId: Long?) : ModifyTransactionEvent()

    data class SetShopSearchDialogVisibility(val visible: Boolean) : ModifyTransactionEvent()

    data class SetDatePickerDialogVisibility(val visible: Boolean) : ModifyTransactionEvent()

    data class SetDate(val date: Long?) : ModifyTransactionEvent()

    data class SetTotalCost(val totalCost: String) : ModifyTransactionEvent()

    data object IncrementTotalCost : ModifyTransactionEvent()

    data object DecrementTotalCost : ModifyTransactionEvent()

    data class SetNote(val note: String) : ModifyTransactionEvent()

    data class NavigateEditShop(val shopId: Long) : ModifyTransactionEvent()

    data class NavigateAddShop(val name: String?) : ModifyTransactionEvent()
}

sealed class ModifyTransactionEventResult {
    data object Success : ModifyTransactionEventResult()

    data object Failure : ModifyTransactionEventResult()

    data class SuccessInsert(val id: Long) : ModifyTransactionEventResult()

    data object SuccessUpdate : ModifyTransactionEventResult()

    data object SuccessDelete : ModifyTransactionEventResult()
}

abstract class ModifyTransactionViewModel : ViewModel() {
    @Suppress("PropertyName") protected val _uiState = MutableStateFlow(ModifyTransactionUiState())
    val uiState = _uiState.asStateFlow()

    protected abstract val getAllShopEntityUseCase: GetAllShopEntityUseCase
    protected abstract val getShopEntityUseCase: GetShopEntityUseCase

    private var _shopListener: Job? = null

    fun init() {
        viewModelScope.launch {
            getAllShopEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allShops = it) }
            }
        }
    }

    open suspend fun handleEvent(event: ModifyTransactionEvent): ModifyTransactionEventResult {
        when (event) {
            is ModifyTransactionEvent.NavigateBack -> {}
            is ModifyTransactionEvent.NavigateAddShop -> {}
            is ModifyTransactionEvent.NavigateEditShop -> {}
            is ModifyTransactionEvent.SetDangerousDeleteDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogVisible = event.visible)
                }
            }
            is ModifyTransactionEvent.SetDangerousDeleteDialogConfirmation -> {
                _uiState.update { currentState ->
                    currentState.copy(isDangerousDeleteDialogConfirmed = event.confirmed)
                }
            }
            is ModifyTransactionEvent.SetShopSearchDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isShopSearchDialogVisible = event.visible)
                }
            }
            is ModifyTransactionEvent.SetDatePickerDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isDatePickerDialogVisible = event.visible)
                }
            }
            is ModifyTransactionEvent.SetDate -> {
                _uiState.update { currentState ->
                    currentState.copy(date = Field.Loaded(event.date))
                }
            }
            is ModifyTransactionEvent.SetNote -> {
                _uiState.update { currentState ->
                    currentState.copy(note = Field.Loaded(event.note.trimStart()))
                }
            }
            is ModifyTransactionEvent.SetTotalCost -> {
                if (event.totalCost.isBlank()) {
                    _uiState.update { currentState ->
                        currentState.copy(totalCost = Field.Loaded(String()))
                    }
                } else if (RegexHelper.isFloat(event.totalCost, 2)) {
                    _uiState.update { currentState ->
                        currentState.copy(totalCost = Field.Loaded(event.totalCost))
                    }
                }
            }
            is ModifyTransactionEvent.IncrementTotalCost -> {
                _uiState.update { currentState ->
                    if (currentState.totalCost.data.isBlank()) {
                        currentState.copy(totalCost = Field.Loaded("%.2f".format(0f)))
                    } else {
                        val value =
                            currentState.totalCost.data.let { StringHelper.toDoubleOrNull(it) }

                        if (value != null) {
                            currentState.copy(
                                totalCost = Field.Loaded("%.2f".format(value.plus(0.5f)))
                            )
                        } else currentState
                    }
                }
            }
            is ModifyTransactionEvent.DecrementTotalCost -> {
                _uiState.update { currentState ->
                    if (currentState.totalCost.data.isBlank()) {
                        currentState.copy(totalCost = Field.Loaded("%.2f".format(0f)))
                    } else {
                        val value =
                            currentState.totalCost.data.let { StringHelper.toDoubleOrNull(it) }

                        if (value != null) {
                            currentState.copy(
                                totalCost =
                                    Field.Loaded(
                                        "%.2f"
                                            .format(
                                                if (value > 0.5f) value.minus(0.5f)
                                                else {
                                                    0f
                                                }
                                            )
                                    )
                            )
                        } else currentState
                    }
                }
            }
            is ModifyTransactionEvent.SelectShop -> handleSelectShop(event.shopId)
            is ModifyTransactionEvent.Submit -> {}
            is ModifyTransactionEvent.DeleteTransaction -> {}
        }

        return ModifyTransactionEventResult.Success
    }

    protected fun handleSelectShop(shopId: Long?) {
        setShopListener(shopId)
    }

    protected fun cancelShopListener() {
        _shopListener?.cancel()
    }

    protected fun setShopListener(shopId: Long?) {
        cancelShopListener()
        if (shopId == null) {
            _uiState.update { currentState -> currentState.copy(selectedShop = Field.Loaded(null)) }
            return
        }

        _shopListener =
            viewModelScope.launch {
                getShopEntityUseCase(shopId).collectLatest {
                    _uiState.update { currentState ->
                        currentState.copy(selectedShop = Field.Loaded(it))
                    }
                }
            }
    }
}
