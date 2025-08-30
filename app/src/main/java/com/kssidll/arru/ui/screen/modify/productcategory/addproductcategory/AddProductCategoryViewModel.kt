package com.kssidll.arru.ui.screen.modify.productcategory.addproductcategory

import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.GetAllProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductCategoryEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductCategoryEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryEvent
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryEventResult
import com.kssidll.arru.ui.screen.modify.productcategory.ModifyProductCategoryViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update

@HiltViewModel
class AddProductCategoryViewModel
@Inject
constructor(
    private val insertProductCategoryEntityUseCase: InsertProductCategoryEntityUseCase,
    override val getAllProductCategoryEntityUseCase: GetAllProductCategoryEntityUseCase,
) : ModifyProductCategoryViewModel() {

    init {
        init()
    }

    override suspend fun handleEvent(
        event: ModifyProductCategoryEvent
    ): ModifyProductCategoryEventResult {
        return when (event) {
            is ModifyProductCategoryEvent.Submit -> {
                val state = uiState.value
                val result = insertProductCategoryEntityUseCase(name = state.name.data)

                when (result) {
                    is InsertProductCategoryEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                InsertProductCategoryEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currenState ->
                                        currenState.copy(
                                            name =
                                                currenState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }
                                InsertProductCategoryEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currenState ->
                                        currenState.copy(
                                            name = currenState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                            }
                        }

                        ModifyProductCategoryEventResult.Failure
                    }
                    is InsertProductCategoryEntityUseCaseResult.Success -> {
                        ModifyProductCategoryEventResult.SuccessInsert(result.id)
                    }
                }
            }
            else -> super.handleEvent(event)
        }
    }
}
