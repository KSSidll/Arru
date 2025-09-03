package com.kssidll.arru.ui.screen.modify.shop.addshop

import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.GetAllShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertShopEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertShopEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopEvent
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopEventResult
import com.kssidll.arru.ui.screen.modify.shop.ModifyShopViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update

@HiltViewModel
class AddShopViewModel
@Inject
constructor(
    private val insertShopEntityUseCase: InsertShopEntityUseCase,
    override val getAllShopEntityUseCase: GetAllShopEntityUseCase,
) : ModifyShopViewModel() {

    init {
        init()
    }

    override suspend fun handleEvent(event: ModifyShopEvent): ModifyShopEventResult {
        return when (event) {
            is ModifyShopEvent.Submit -> {
                val state = uiState.value
                val result = insertShopEntityUseCase(name = state.name.data)

                when (result) {
                    is InsertShopEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                InsertShopEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currenState ->
                                        currenState.copy(
                                            name =
                                                currenState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }
                                InsertShopEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currenState ->
                                        currenState.copy(
                                            name = currenState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                            }
                        }

                        ModifyShopEventResult.Failure
                    }
                    is InsertShopEntityUseCaseResult.Success -> {
                        ModifyShopEventResult.SuccessInsert(result.id)
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
