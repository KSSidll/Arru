package com.kssidll.arru.ui.screen.modify.transaction.addtransaction

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.preference.AppPreferences
import com.kssidll.arru.data.preference.getTransactionDate
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.GetAllShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetNewestTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertTransactionEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionEvent
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionEventResult
import com.kssidll.arru.ui.screen.modify.transaction.ModifyTransactionViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AddTransactionViewModel
@Inject
constructor(
    @param:ApplicationContext private val appContext: Context,
    private val insertTransactionEntityUseCase: InsertTransactionEntityUseCase,
    private val getNewestTransactionEntityUseCase: GetNewestTransactionEntityUseCase,
    override val getAllShopEntityUseCase: GetAllShopEntityUseCase,
    override val getShopEntityUseCase: GetShopEntityUseCase,
) : ModifyTransactionViewModel() {

    init {
        init()
        loadNewest()
    }

    private fun loadNewest() =
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(
                    selectedShop = currentState.selectedShop.toLoading(),
                    date = currentState.date.toLoading(),
                )
            }

            val newest = getNewestTransactionEntityUseCase().first()

            if (newest != null) {
                val shop = newest.shopEntityId?.let { getShopEntityUseCase(it).first() }

                val date =
                    when (AppPreferences.getTransactionDate(appContext).first()) {
                        AppPreferences.Transaction.Date.Values.CURRENT -> {
                            Calendar.getInstance().timeInMillis
                        }

                        AppPreferences.Transaction.Date.Values.LAST -> {
                            newest.date
                        }
                    }

                _uiState.update { currentState ->
                    currentState.copy(selectedShop = Field.Loaded(shop), date = Field.Loaded(date))
                }
            } else {
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedShop = currentState.selectedShop.toLoaded(),
                        date = Field.Loaded(Calendar.getInstance().timeInMillis),
                    )
                }
            }
        }

    override suspend fun handleEvent(event: ModifyTransactionEvent): ModifyTransactionEventResult {
        return when (event) {
            is ModifyTransactionEvent.Submit -> {
                val state = uiState.value
                val result =
                    insertTransactionEntityUseCase(
                        date = state.date.data,
                        totalCost = state.totalCost.data,
                        note = state.note.data,
                        shopId = state.selectedShop.data?.id,
                    )

                when (result) {
                    is InsertTransactionEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                InsertTransactionEntityUseCaseResult.DateNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            date =
                                                currentState.date.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                                InsertTransactionEntityUseCaseResult.ShopIdInvalid -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedShop =
                                                currentState.selectedShop.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                InsertTransactionEntityUseCaseResult.TotalCostInvalid -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            totalCost =
                                                currentState.totalCost.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                InsertTransactionEntityUseCaseResult.TotalCostNoValue -> {
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
                    is InsertTransactionEntityUseCaseResult.Success -> {
                        ModifyTransactionEventResult.SuccessInsert(result.id)
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
