package com.kssidll.arru.ui.screen.modify.item.additem

import android.util.Log
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.GetAllProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetNewestItemEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetNewestItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetTransactionEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.InsertItemEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.item.ModifyItemEvent
import com.kssidll.arru.ui.screen.modify.item.ModifyItemViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

@HiltViewModel
class AddItemViewModel
@Inject
constructor(
    private val insertItemEntityUseCase: InsertItemEntityUseCase,
    private val getTransactionEntityUseCase: GetTransactionEntityUseCase,
    override val getNewestItemEntityUseCase: GetNewestItemEntityUseCase,
    override val getNewestItemEntityByProductUseCase: GetNewestItemEntityByProductUseCase,
    override val getProductEntityUseCase: GetProductEntityUseCase,
    override val getAllProductEntityUseCase: GetAllProductEntityUseCase,
    override val getProductVariantEntityUseCase: GetProductVariantEntityUseCase,
    override val getProductVariantEntityByProductUseCase: GetProductVariantEntityByProductUseCase,
) : ModifyItemViewModel() {
    var transactionEntityId: Long? = null

    suspend fun checkExists(id: Long): Boolean {
        transactionEntityId = getTransactionEntityUseCase(id).first()?.id
        return transactionEntityId != null
    }

    init {
        init()
    }

    /** @return true if handled without errors, false otherwise */
    override suspend fun handleEvent(event: ModifyItemEvent): Boolean {
        when (event) {
            is ModifyItemEvent.Submit -> {
                val state = uiState.value
                val insertResult =
                    transactionEntityId?.let { transactionId ->
                        insertItemEntityUseCase(
                            transactionEntityId = transactionId,
                            productEntityId = state.selectedProduct.data?.id,
                            productVariantEntityId = state.selectedProductVariant.data?.id,
                            quantity = state.quantity.data,
                            price = state.price.data,
                        )
                    } ?: return true

                return when (insertResult) {
                    is InsertItemEntityUseCaseResult.Error -> {
                        insertResult.errors.forEach {
                            when (it) {
                                InsertItemEntityUseCaseResult.PriceNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            price =
                                                currentState.price.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                                InsertItemEntityUseCaseResult.PriceInvalid -> {
                                    Log.e("AddItem", "Insert invalid price `${state.price.data}`")
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            price =
                                                currentState.price.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                InsertItemEntityUseCaseResult.ProductIdNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProduct =
                                                currentState.selectedProduct.toError(
                                                    FieldError.NoValueError
                                                )
                                        )
                                    }
                                }
                                InsertItemEntityUseCaseResult.ProductIdInvalid -> {
                                    Log.e(
                                        "AddItem",
                                        "Insert invalid product `${state.selectedProduct.data?.id}`",
                                    )
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProduct =
                                                currentState.selectedProduct.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                InsertItemEntityUseCaseResult.ProductVariantIdInvalid -> {
                                    Log.e(
                                        "AddItem",
                                        "Insert invalid product variant `${state.selectedProductVariant.data?.id}`",
                                    )
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProductVariant =
                                                currentState.selectedProductVariant.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                InsertItemEntityUseCaseResult.QuantityNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            quantity =
                                                currentState.quantity.toError(
                                                    FieldError.NoValueError
                                                )
                                        )
                                    }
                                }
                                InsertItemEntityUseCaseResult.QuantityInvalid -> {
                                    Log.e(
                                        "AddItem",
                                        "Insert invalid quantity `${state.quantity.data}`",
                                    )
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            quantity =
                                                currentState.quantity.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                InsertItemEntityUseCaseResult.TransactionIdInvalid -> {
                                    Log.e(
                                        "AddItem",
                                        "Insert invalid transaction `${transactionEntityId}`",
                                    )
                                }
                            }
                        }

                        false
                    }

                    is InsertItemEntityUseCaseResult.Success -> true
                }
            }
            else -> return super.handleEvent(event)
        }
    }
}
