package com.kssidll.arru.ui.screen.modify.transaction.edittransaction

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.Shop
import com.kssidll.arru.data.data.TransactionBasket
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.DeleteResult
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.UpdateResult
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class EditTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionBasketRepositorySource,
    override val shopRepository: ShopRepositorySource
): ModifyTransactionViewModel() {
    private var mTransaction: TransactionBasket? = null

    /**
     * Updates data in the screen state
     * @return true if provided [transactionId] was valid, false otherwise
     */
    suspend fun updateState(transactionId: Long) = viewModelScope.async {
        // skip state update for repeating transactionId
        if (transactionId == mTransaction?.id) return@async true

        screenState.allToLoading()

        mTransaction = transactionRepository.get(transactionId)

        updateStateForTransaction(mTransaction)

        return@async mTransaction != null
    }
        .await()

    private suspend fun updateStateForTransaction(
        transaction: TransactionBasket?
    ) {
        val shop: Shop? = transaction?.shopId?.let { shopRepository.get(it) }

        screenState.date.apply {
            value = Field.Loaded(transaction?.date)
        }

        screenState.totalCost.apply {
            value = Field.Loaded(
                transaction?.actualTotalCost()
                    ?.toString()
            )
        }

        screenState.selectedShop.apply {
            value = Field.Loaded(shop)
        }
    }

    /**
     * Tries to update transaction with provided [transactionId] with current state data
     * @return resulting [UpdateResult]
     */
    suspend fun updateTransaction(transactionId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = transactionRepository.update(
            transactionId = transactionId,
            date = screenState.date.value.data ?: TransactionBasket.INVALID_DATE,
            totalCost = screenState.totalCost.value.data?.let {
                TransactionBasket.totalCostFromString(
                    it
                )
            }
                ?: TransactionBasket.INVALID_TOTAL_COST,
            shopId = screenState.selectedShop.value.data?.id
        )

        if (result.isError()) {
            when (result.error!!) {
                UpdateResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to update transaction with invalid transaction id in EditTransactionViewModel"
                    )
                    return@async UpdateResult.Success
                }

                UpdateResult.InvalidDate -> {
                    screenState.date.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                UpdateResult.InvalidTotalCost -> {
                    screenState.totalCost.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                UpdateResult.InvalidShopId -> {
                    screenState.selectedShop.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()

    /**
     * Tries to delete transaction with provided [transactionId]
     * @return resulting [DeleteResult]
     */
    suspend fun deleteTransaction(transactionId: Long) = viewModelScope.async {
        val result = transactionRepository.delete(
            transactionId,
            screenState.deleteWarningConfirmed.value
        )

        if (result.isError()) {
            when (result.error!!) {
                DeleteResult.InvalidId -> {
                    Log.e(
                        "InvalidId",
                        "Tried to delete transaction with invalid transaction id in EditTransactionViewModel"
                    )
                    return@async DeleteResult.Success
                }

                DeleteResult.DangerousDelete -> {
                    screenState.showDeleteWarning.value = true
                }
            }
        }

        return@async result
    }
        .await()
}