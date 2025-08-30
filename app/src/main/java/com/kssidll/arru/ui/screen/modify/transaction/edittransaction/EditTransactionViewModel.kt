package com.kssidll.arru.ui.screen.modify.transaction.edittransaction

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ShopEntity
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteTransactionEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.UpdateTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateTransactionEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class EditTransactionViewModel
@Inject
constructor(
    private val transactionRepository: TransactionRepositorySource,
    override val shopRepository: ShopRepositorySource,
    private val updateTransactionEntityUseCase: UpdateTransactionEntityUseCase,
    private val deleteTransactionEntityUseCase: DeleteTransactionEntityUseCase,
) : ModifyTransactionViewModel() {
    private var mTransaction: TransactionEntity? = null

    suspend fun checkExists(id: Long): Boolean {
        return transactionRepository.get(id).first() != null
    }

    fun updateState(transactionId: Long) =
        viewModelScope.launch {
            // skip state update for repeating transactionId
            if (transactionId == mTransaction?.id) return@launch

            screenState.allToLoading()

            mTransaction = transactionRepository.get(transactionId).first()

            updateStateForTransaction(mTransaction)
        }

    private suspend fun updateStateForTransaction(transaction: TransactionEntity?) {
        val shop: ShopEntity? = transaction?.shopEntityId?.let { shopRepository.get(it).first() }

        screenState.date.apply { value = Field.Loaded(transaction?.date) }

        screenState.totalCost.apply {
            value = Field.Loaded(transaction?.actualTotalCost()?.toString().orEmpty())
        }

        screenState.selectedShop.apply { value = Field.Loaded(shop) }

        screenState.note.apply { value = Field.Loaded(transaction?.note) }
    }

    suspend fun updateTransaction(transactionId: Long): Boolean {
        screenState.attemptedToSubmit.value = true

        val result =
            updateTransactionEntityUseCase(
                id = transactionId,
                date = screenState.date.value.data,
                totalCost = screenState.totalCost.value.data,
                note = screenState.note.value.data,
                shopId = screenState.selectedShop.value.data?.id,
            )

        return when (result) {
            is UpdateTransactionEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        UpdateTransactionEntityUseCaseResult.TransactionIdInvalid -> {
                            Log.e(
                                "ModifyTransaction",
                                "Insert invalid transaction `${transactionId}`",
                            )
                        }
                        UpdateTransactionEntityUseCaseResult.DateNoValue -> {
                            screenState.date.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                        UpdateTransactionEntityUseCaseResult.ShopIdInvalid -> {
                            screenState.selectedShop.apply {
                                value = value.toError(FieldError.InvalidValueError)
                            }
                        }
                        UpdateTransactionEntityUseCaseResult.TotalCostInvalid -> {
                            screenState.totalCost.apply {
                                value = value.toError(FieldError.InvalidValueError)
                            }
                        }
                        UpdateTransactionEntityUseCaseResult.TotalCostNoValue -> {
                            screenState.totalCost.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                    }
                }

                false
            }
            is UpdateTransactionEntityUseCaseResult.Success -> {
                true
            }
        }
    }

    suspend fun deleteTransaction(transactionId: Long): Boolean {
        val result =
            deleteTransactionEntityUseCase(transactionId, screenState.deleteWarningConfirmed.value)

        return when (result) {
            is DeleteTransactionEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        DeleteTransactionEntityUseCaseResult.DangerousDelete -> {
                            screenState.showDeleteWarning.value = true
                        }
                        DeleteTransactionEntityUseCaseResult.TransactionIdInvalid -> {
                            Log.e(
                                "ModifyTransaction",
                                "Tried to delete transaction with invalid id",
                            )
                        }
                    }
                }

                false
            }
            is DeleteTransactionEntityUseCaseResult.Success -> {
                true
            }
        }
    }
}
