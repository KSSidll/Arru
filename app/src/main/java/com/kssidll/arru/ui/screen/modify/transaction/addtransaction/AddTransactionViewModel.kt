package com.kssidll.arru.ui.screen.modify.transaction.addtransaction

import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.transaction.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionBasketRepositorySource,
    override val shopRepository: ShopRepositorySource
): ModifyTransactionViewModel() {
    // TODO load last transaction shop and date on init

    /**
     * Tries to add a transaction to the repository
     * @return resulting [InsertResult]
     */
    suspend fun addTransaction() = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = transactionRepository.insert(
            date = screenState.date.value.data ?: TransactionBasket.INVALID_DATE,
            totalCost = screenState.totalCost.value.data?.let { TransactionBasket.totalCostFromString(it) }
                ?: TransactionBasket.INVALID_TOTAL_COST,
            shopId = screenState.selectedShop.value.data?.id
        )

        if (result.isError()) {
            when (result.error!!) {
                InsertResult.InvalidDate -> {
                    screenState.date.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                InsertResult.InvalidTotalCost -> {
                    screenState.totalCost.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                InsertResult.InvalidShopId -> {
                    screenState.selectedShop.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()
}