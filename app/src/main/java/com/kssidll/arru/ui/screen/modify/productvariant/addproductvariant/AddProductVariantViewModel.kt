package com.kssidll.arru.ui.screen.modify.productvariant.addproductvariant

import android.util.Log
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.GetAllGlobalProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertProductVariantEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantEvent
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantEventResult
import com.kssidll.arru.ui.screen.modify.productvariant.ModifyProductVariantViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.update

@HiltViewModel
class AddProductVariantViewModel
@Inject
constructor(
    private val insertProductVariantEntityUseCase: InsertProductVariantEntityUseCase,
    override val getProductEntityUseCase: GetProductEntityUseCase,
    override val getProductVariantEntityByProductUseCase: GetProductVariantEntityByProductUseCase,
    override val getAllGlobalProductVariantEntityUseCase: GetAllGlobalProductVariantEntityUseCase,
) : ModifyProductVariantViewModel() {

    init {
        _uiState.update { currentState -> currentState.copy(isVariantGlobalChangeEnabled = true) }
    }

    override suspend fun handleEvent(
        event: ModifyProductVariantEvent
    ): ModifyProductVariantEventResult {
        return when (event) {
            is ModifyProductVariantEvent.Submit -> {
                val state = uiState.value
                val result =
                    insertProductVariantEntityUseCase(
                        productId = if (state.isVariantGlobal.data == true) null else mProduct?.id,
                        name = state.name.data,
                    )

                when (result) {
                    is InsertProductVariantEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                InsertProductVariantEntityUseCaseResult.ProductIdInvalid -> {
                                    Log.e(
                                        "ModifyProductVariant",
                                        "Insert invalid product id ${mProduct?.id}",
                                    )
                                }

                                InsertProductVariantEntityUseCaseResult.NameDuplicateValue -> {
                                    _uiState.update { currenState ->
                                        currenState.copy(
                                            name =
                                                currenState.name.toError(
                                                    FieldError.DuplicateValueError
                                                )
                                        )
                                    }
                                }

                                InsertProductVariantEntityUseCaseResult.NameNoValue -> {
                                    _uiState.update { currenState ->
                                        currenState.copy(
                                            name = currenState.name.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                            }
                        }

                        ModifyProductVariantEventResult.Failure
                    }

                    is InsertProductVariantEntityUseCaseResult.Success -> {
                        ModifyProductVariantEventResult.SuccessInsert(result.id)
                    }
                }
            }

            else -> super.handleEvent(event)
        }
    }
}
