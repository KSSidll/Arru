package com.kssidll.arru.ui.screen.modify.transaction.addtransaction

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getTransactionDate
import com.kssidll.arru.data.repository.ShopRepositorySource
import com.kssidll.arru.data.repository.TransactionRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.InsertTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertTransactionEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// TODO refactor uiState Event UseCase

@HiltViewModel
class AddTransactionViewModel
@Inject
constructor(
    @param:ApplicationContext private val appContext: Context,
    private val transactionRepository: TransactionRepositorySource,
    override val shopRepository: ShopRepositorySource,
    private val insertTransactionEntityUseCase: InsertTransactionEntityUseCase,
) : ModifyTransactionViewModel() {

    init {
        loadLastest()
    }

    /** Loads data from latest transaction */
    private fun loadLastest() =
        viewModelScope.launch {
            screenState.selectedShop.apply { value = value.toLoading() }
            screenState.date.apply { value = value.toLoading() }

            val latest = transactionRepository.newest().first()

            if (latest != null) {
                val shop = latest.shopEntityId?.let { shopRepository.get(it).first() }

                screenState.selectedShop.value = Field.Loaded(shop)

                screenState.date.apply {
                    value =
                        when (AppPreferences.getTransactionDate(appContext).first()) {
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

    suspend fun addTransaction(): Long? {
        screenState.attemptedToSubmit.value = true

        val result =
            insertTransactionEntityUseCase(
                date = screenState.date.value.data,
                totalCost = screenState.totalCost.value.data,
                note = screenState.note.value.data,
                shopId = screenState.selectedShop.value.data?.id,
            )

        return when (result) {
            is InsertTransactionEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        InsertTransactionEntityUseCaseResult.DateNoValue -> {
                            screenState.date.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                        InsertTransactionEntityUseCaseResult.ShopIdInvalid -> {
                            screenState.selectedShop.apply {
                                value = value.toError(FieldError.InvalidValueError)
                            }
                        }
                        InsertTransactionEntityUseCaseResult.TotalCostInvalid -> {
                            screenState.totalCost.apply {
                                value = value.toError(FieldError.InvalidValueError)
                            }
                        }
                        InsertTransactionEntityUseCaseResult.TotalCostNoValue -> {
                            screenState.totalCost.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                    }
                }

                null
            }
            is InsertTransactionEntityUseCaseResult.Success -> {
                result.id
            }
        }
    }
}
