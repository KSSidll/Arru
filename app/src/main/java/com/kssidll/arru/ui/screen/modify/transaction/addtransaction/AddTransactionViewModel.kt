package com.kssidll.arru.ui.screen.modify.transaction.addtransaction

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.TransactionEntity
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getTransactionDate
import com.kssidll.arru.data.preference.setTransactionDate
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddTransactionViewModel @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val transactionRepository: TransactionRepositorySource,
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

        val latest = transactionRepository.newest().first()

        if (latest != null) {
            val shop = latest.shopEntityId?.let { shopRepository.get(it).first() }

            screenState.selectedShop.value = Field.Loaded(shop)

            screenState.date.apply {
                value = when (AppPreferences.getTransactionDate(appContext)
                    .first()) {
                    AppPreferences.Transaction.Date.Values.CURRENT -> {
                        Field.Loaded(Calendar.getInstance().timeInMillis)
                    }

                    AppPreferences.Transaction.Date.Values.LAST -> {
                        Field.Loaded(latest.date)
                    }
                }
            }
        } else {
            screenState.selectedShop.apply { value = value.toLoaded() }
            screenState.date.apply { value = Field.Loaded(Calendar.getInstance().timeInMillis) }
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
            totalCost = screenState.totalCost.value.data?.let {
                TransactionEntity.totalCostFromString(
                    it
                )
            }
                ?: TransactionEntity.INVALID_TOTAL_COST,
            shopId = screenState.selectedShop.value.data?.id,
            note = screenState.note.value.data?.trim(),
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

        AppPreferences.setTransactionDate(appContext, AppPreferences.Transaction.Date.Values.LAST)

        return@async result
    }
        .await()
}