package com.kssidll.arru.ui.screen.modify.transaction.edittransaction

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteTransactionEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.GetAllShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateTransactionEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionEvent
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionEventResult
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditTransactionViewModel
@Inject
constructor(
    private val getTransactionEntityUseCase: GetTransactionEntityUseCase,
    private val updateTransactionEntityUseCase: UpdateTransactionEntityUseCase,
    private val deleteTransactionEntityUseCase: DeleteTransactionEntityUseCase,
    override val getAllShopEntityUseCase: GetAllShopEntityUseCase,
    override val getShopEntityUseCase: GetShopEntityUseCase,
) : ModifyTransactionViewModel() {
    suspend fun checkExists(id: Long): Boolean {
        return getTransactionEntityUseCase(id).first() != null
    }

    fun updateState(transactionId: Long) =
        viewModelScope.launch {
            val state = uiState.value
            // skip state update for repeating transactionId
            if (transactionId == state.currentTransaction?.id) return@launch

            _uiState.update { currentState ->
                currentState.copy(
                    date = currentState.date.toLoading(),
                    totalCost = currentState.totalCost.toLoading(),
                    note = currentState.note.toLoading(),
                    selectedShop = currentState.selectedShop.toLoading(),
                )
            }

            val transaction = getTransactionEntityUseCase(transactionId).first()
            val shop = transaction?.shopEntityId?.let { getShopEntityUseCase(it).first() }

            _uiState.update { currentState ->
                currentState.copy(
                    date = Field.Loaded(transaction?.date),
                    totalCost = Field.Loaded(transaction?.actualTotalCost()?.toString().orEmpty()),
                    note = Field.Loaded(transaction?.note),
                    selectedShop = Field.Loaded(shop),
                    currentTransaction = transaction,
                )
            }
        }

    init {
        init()
        _uiState.update { currentState -> currentState.copy(isDeleteEnabled = true) }
    }

    override suspend fun handleEvent(event: ModifyTransactionEvent): ModifyTransactionEventResult {
        return when (event) {
            is ModifyTransactionEvent.Submit -> {
                val state = uiState.value
                val result =
                    state.currentTransaction?.let { transaction ->
                        updateTransactionEntityUseCase(
                            id = transaction.id,
                            date = state.date.data,
                            totalCost = state.totalCost.data,
                            note = state.note.data,
                            shopId = state.selectedShop.data?.id,
                        )
                    } ?: return ModifyTransactionEventResult.Failure

                when (result) {
                    is UpdateTransactionEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                UpdateTransactionEntityUseCaseResult.TransactionIdInvalid -> {
                                    Log.e(
                                        "ModifyTransaction",
                                        "Update invalid transaction `${state.currentTransaction.id}`",
                                    )
                                }
                                UpdateTransactionEntityUseCaseResult.DateNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            date =
                                                currentState.date.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                                UpdateTransactionEntityUseCaseResult.ShopIdInvalid -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedShop =
                                                currentState.selectedShop.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                UpdateTransactionEntityUseCaseResult.TotalCostInvalid -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            totalCost =
                                                currentState.totalCost.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                UpdateTransactionEntityUseCaseResult.TotalCostNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            totalCost =
                                                currentState.totalCost.toError(
                                                    FieldError.NoValueError
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        ModifyTransactionEventResult.Failure
                    }
                    is UpdateTransactionEntityUseCaseResult.Success -> {
                        ModifyTransactionEventResult.SuccessUpdate
                    }
                }
            }
            is ModifyTransactionEvent.DeleteTransaction -> {
                val state = uiState.value
                val result =
                    state.currentTransaction?.let { transaction ->
                        deleteTransactionEntityUseCase(
                            id = transaction.id,
                            ignoreDangerous = state.isDangerousDeleteDialogConfirmed,
                        )
                    } ?: return ModifyTransactionEventResult.Failure

                when (result) {
                    is DeleteTransactionEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                DeleteTransactionEntityUseCaseResult.TransactionIdInvalid -> {
                                    Log.e(
                                        "ModifyTransaction",
                                        "Delete invalid transaction `${state.currentTransaction.id}`",
                                    )
                                }
                                DeleteTransactionEntityUseCaseResult.DangerousDelete -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(isDangerousDeleteDialogVisible = true)
                                    }
                                }
                            }
                        }

                        ModifyTransactionEventResult.Failure
                    }
                    is DeleteTransactionEntityUseCaseResult.Success -> {
                        ModifyTransactionEventResult.SuccessDelete
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
