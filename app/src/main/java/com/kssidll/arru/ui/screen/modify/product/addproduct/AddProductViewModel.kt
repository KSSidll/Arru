package com.kssidll.arru.ui.screen.modify.product.addproduct

import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.GetAllProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetAllProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetAllProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.product.ModifyProductEvent
import com.kssidll.arru.ui.screen.modify.product.ModifyProductEventResult
import com.kssidll.arru.ui.screen.modify.product.ModifyProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update

@HiltViewModel
class AddProductViewModel
@Inject
constructor(
    private val insertProductEntityUseCase: InsertProductEntityUseCase,
    override val getAllProductEntityUseCase: GetAllProductEntityUseCase,
    override val getAllProductCategoryEntityUseCase: GetAllProductCategoryEntityUseCase,
    override val getAllProductProducerEntityUseCase: GetAllProductProducerEntityUseCase,
    override val getProductCategoryEntityUseCase: GetProductCategoryEntityUseCase,
    override val getProductProducerEntityUseCase: GetProductProducerEntityUseCase,
) : ModifyProductViewModel() {

    init {
        init()
    }

    override suspend fun handleEvent(event: ModifyProductEvent): ModifyProductEventResult {
        return when (event) {
            is ModifyProductEvent.Submit -> {
                val state = uiState.value

                val result =
                    insertProductEntityUseCase(
                        name = state.name.data,
                        productProducerEntityId = state.selectedProductProducer.data?.id,
                        productCategoryEntityId = state.selectedProductCategory.data?.id,
                    )

                when (result) {
                    is InsertProductEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                InsertProductEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }
                                InsertProductEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            name =
                                                currentState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                                InsertProductEntityUseCaseResult.ProductCategoryIdInvalid -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProductCategory =
                                                currentState.selectedProductCategory.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                InsertProductEntityUseCaseResult.ProductCategoryNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProductCategory =
                                                currentState.selectedProductCategory.toError(
                                                    FieldError.NoValueError
                                                )
                                        )
                                    }
                                }
                                InsertProductEntityUseCaseResult.ProductProducerIdInvalid -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProductProducer =
                                                currentState.selectedProductProducer.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                            }
                        }

                        ModifyProductEventResult.Failure
                    }
                    is InsertProductEntityUseCaseResult.Success -> {
                        ModifyProductEventResult.SuccessInsert(result.id)
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
