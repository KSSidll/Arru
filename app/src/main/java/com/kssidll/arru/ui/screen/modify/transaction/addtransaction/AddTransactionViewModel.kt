package com.kssidll.arru.ui.screen.modify.transaction.addtransaction

import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource
import com.kssidll.arru.data.repository.TransactionBasketRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    private val transactionRepository: TransactionBasketRepositorySource,
    override val shopRepository: ShopRepositorySource
): ModifyTransactionViewModel() {

    init {
        loadLastest()
    }

    /**
     * Loads data from latest transaction
     */
    private fun loadLastest() = viewModelScope.launch {
        screenState.selectedShop.apply { value = value.toLoading() }
        screenState.date.apply { value = value.toLoading() }

        val latest = transactionRepository.newest()

        if (latest != null) {
            val shop = latest.shopId?.let { shopRepository.get(it) }

            screenState.selectedShop.value = Field.Loaded(shop)
            screenState.date.value = Field.Loaded(latest.date)
        } else {
            screenState.selectedShop.apply { value = value.toLoaded() }
            screenState.date.apply { value = value.toLoaded() }
        }
    }

    /**
     * Tries to add a transaction to the repository
     * @return resulting [InsertResult]
     */
    suspend fun addTransaction() = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = transactionRepository.insert(
            date = screenState.date.value.data ?: TransactionEntity.INVALID_DATE,
            totalCost = screenState.totalCost.value.data?.let { TransactionEntity.totalCostFromString(it) }
                ?: TransactionEntity.INVALID_TOTAL_COST,
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