package com.kssidll.arru.ui.screen.modify.product.addproduct

import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.InsertProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.product.ModifyProductViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO refactor uiState Event UseCase

@HiltViewModel
class AddProductViewModel
@Inject
constructor(
    override val productRepository: ProductRepositorySource,
    override val categoryRepository: ProductCategoryRepositorySource,
    override val producerRepository: ProductProducerRepositorySource,
    private val insertProductEntityUseCase: InsertProductEntityUseCase,
) : ModifyProductViewModel() {

    suspend fun addProduct(): Long? {
        screenState.attemptedToSubmit.value = true

        val result =
            insertProductEntityUseCase(
                name = screenState.name.value.data,
                productProducerEntityId = screenState.selectedProductProducer.value.data?.id,
                productCategoryEntityId = screenState.selectedProductCategory.value.data?.id,
            )

        return when (result) {
            is InsertProductEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        InsertProductEntityUseCaseResult.NameDuplicateValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.DuplicateValueError)
                            }
                        }
                        InsertProductEntityUseCaseResult.NameNoValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                        InsertProductEntityUseCaseResult.ProductCategoryIdInvalid -> {
                            screenState.selectedProductCategory.apply {
                                value = value.toError(FieldError.InvalidValueError)
                            }
                        }
                        InsertProductEntityUseCaseResult.ProductCategoryNoValue -> {
                            screenState.selectedProductCategory.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                        InsertProductEntityUseCaseResult.ProductProducerIdInvalid -> {
                            screenState.selectedProductProducer.apply {
                                value = value.toError(FieldError.InvalidValueError)
                            }
                        }
                    }
                }

                null
            }
            is InsertProductEntityUseCaseResult.Success -> {
                result.id
            }
        }
    }
}
