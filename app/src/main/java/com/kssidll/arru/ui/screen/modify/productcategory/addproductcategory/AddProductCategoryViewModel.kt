package com.kssidll.arru.ui.screen.modify.productcategory.addproductcategory

import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.InsertProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductCategoryEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO refactor uiState Event UseCase

@HiltViewModel
class AddProductCategoryViewModel
@Inject
constructor(
    override val categoryRepository: ProductCategoryRepositorySource,
    private val insertProductCategoryEntityUseCase: InsertProductCategoryEntityUseCase,
) : ModifyProductCategoryViewModel() {

    suspend fun addCategory(): Long? {
        screenState.attemptedToSubmit.value = true

        val result = insertProductCategoryEntityUseCase(name = screenState.name.value.data)

        return when (result) {
            is InsertProductCategoryEntityUseCaseResult.Error -> {
                result.errors.forEach {
                    when (it) {
                        InsertProductCategoryEntityUseCaseResult.NameDuplicateValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.DuplicateValueError)
                            }
                        }
                        InsertProductCategoryEntityUseCaseResult.NameNoValue -> {
                            screenState.name.apply {
                                value = value.toError(FieldError.NoValueError)
                            }
                        }
                    }
                }

                null
            }
            is InsertProductCategoryEntityUseCaseResult.Success -> {
                result.id
            }
        }
    }
}
