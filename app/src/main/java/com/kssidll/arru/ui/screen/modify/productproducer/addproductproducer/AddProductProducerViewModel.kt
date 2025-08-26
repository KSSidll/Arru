package com.kssidll.arru.ui.screen.modify.productproducer.addproductproducer

import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.InsertProductProducerEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductProducerEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO refactor uiState Event UseCase

@HiltViewModel
class AddProductProducerViewModel
@Inject
constructor(
    override val producerRepository: ProductProducerRepositorySource,
    private val insertProductProducerEntityUseCase: InsertProductProducerEntityUseCase,
) : ModifyProductProducerViewModel() {

    suspend fun addProducer(): Long? {
        screenState.attemptedToSubmit.value = true

        val result = insertProductProducerEntityUseCase(name = screenState.name.value.data)

        return when (result) {
            is InsertProductProducerEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        InsertProductProducerEntityUseCaseResult.NameDuplicateValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.DuplicateValueError)
                            }
                        }
                        InsertProductProducerEntityUseCaseResult.NameNoValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                    }
                }

                null
            }
            is InsertProductProducerEntityUseCaseResult.Success -> {
                result.id
            }
        }
    }
}
