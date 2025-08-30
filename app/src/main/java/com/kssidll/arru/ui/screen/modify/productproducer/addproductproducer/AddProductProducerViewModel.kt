package com.kssidll.arru.ui.screen.modify.productproducer.addproductproducer

import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.GetAllProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductProducerEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerEvent
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerEventResult
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update

@HiltViewModel
class AddProductProducerViewModel
@Inject
constructor(
    private val insertProductProducerEntityUseCase: InsertProductProducerEntityUseCase,
    override val getAllProductProducerEntityUseCase: GetAllProductProducerEntityUseCase,
) : ModifyProductProducerViewModel() {

    init {
        init()
    }

    override suspend fun handleEvent(
        event: ModifyProductProducerEvent
    ): ModifyProductProducerEventResult {
        return when (event) {
            is ModifyProductProducerEvent.Submit -> {
                val state = uiState.value
                val result = insertProductProducerEntityUseCase(name = state.name.data)

                when (result) {
                    is InsertProductProducerEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                InsertProductProducerEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currenState ->
                                        currenState.copy(
                                            name =
                                                currenState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }
                                InsertProductProducerEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currenState ->
                                        currenState.copy(
                                            name = currenState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                            }
                        }

                        ModifyProductProducerEventResult.Failure
                    }
                    is InsertProductProducerEntityUseCaseResult.Success -> {
                        ModifyProductProducerEventResult.SuccessInsert(result.id)
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
