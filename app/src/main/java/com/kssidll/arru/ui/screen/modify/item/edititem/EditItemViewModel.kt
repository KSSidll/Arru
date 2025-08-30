package com.kssidll.arru.ui.screen.modify.item.edititem

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.domain.usecase.data.DeleteItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.DeleteItemEntityUseCaseResult
import com.kssidll.arru.domain.usecase.data.GetAllProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetNewestItemEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetNewestItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.UpdateItemEntityUseCaseResult
import com.kssidll.arru.ui.screen.modify.item.ModifyItemEvent
import com.kssidll.arru.ui.screen.modify.item.ModifyItemEventResult
import com.kssidll.arru.ui.screen.modify.item.ModifyItemViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class EditItemViewModel
@Inject
constructor(
    private val getItemEntityUseCase: GetItemEntityUseCase,
    private val updateItemEntityUseCase: UpdateItemEntityUseCase,
    private val deleteItemEntityUseCase: DeleteItemEntityUseCase,
    override val getNewestItemEntityUseCase: GetNewestItemEntityUseCase,
    override val getNewestItemEntityByProductUseCase: GetNewestItemEntityByProductUseCase,
    override val getProductEntityUseCase: GetProductEntityUseCase,
    override val getAllProductEntityUseCase: GetAllProductEntityUseCase,
    override val getProductVariantEntityUseCase: GetProductVariantEntityUseCase,
    override val getProductVariantEntityByProductUseCase: GetProductVariantEntityByProductUseCase,
) : ModifyItemViewModel() {
    suspend fun checkExists(id: Long): Boolean {
        return getItemEntityUseCase(id).first() != null
    }

    fun updateState(itemId: Long) =
        viewModelScope.launch {
            val state = uiState.value

            // skip state update for repeating itemId
            if (itemId == state.currentItem?.id) return@launch

            _uiState.update { currentState ->
                currentState.copy(
                    price = currentState.price.toLoading(),
                    quantity = currentState.quantity.toLoading(),
                    selectedProduct = currentState.selectedProduct.toLoading(),
                    selectedProductVariant = currentState.selectedProductVariant.toLoading(),
                )
            }

            val item = getItemEntityUseCase(itemId).first()

            _uiState.update { currentState -> currentState.copy(currentItem = item) }

            updateStateForItem(item)
        }

    fun updateStateForItem(item: ItemEntity?) {
        val productId: Long? = item?.productEntityId
        val productVariantId: Long? = item?.productVariantEntityId
        val price: String? = item?.actualPrice()?.toString()
        val quantity: String? = item?.actualQuantity()?.toString()

        productId?.let { setProductListener(it) }
        setProductVariantListener(productVariantId)

        _uiState.update { currentState ->
            currentState.copy(price = Field.Loaded(price), quantity = Field.Loaded(quantity))
        }
    }

    init {
        init()

        _uiState.update { currentState -> currentState.copy(isDeleteEnabled = true) }
    }

    override suspend fun handleEvent(event: ModifyItemEvent): ModifyItemEventResult {
        when (event) {
            is ModifyItemEvent.Submit -> {
                val state = uiState.value
                val result =
                    state.currentItem?.let { item ->
                        updateItemEntityUseCase(
                            id = item.id,
                            transactionEntityId = item.transactionEntityId,
                            productEntityId = state.selectedProduct.data?.id,
                            productVariantEntityId = state.selectedProductVariant.data?.id,
                            quantity = state.quantity.data,
                            price = state.price.data,
                        )
                    } ?: return ModifyItemEventResult.Failure

                return when (result) {
                    is UpdateItemEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                UpdateItemEntityUseCaseResult.ItemIdInvalid -> {
                                    Log.e(
                                        "ModifyItem",
                                        "Update invalid item `${state.currentItem.id}`",
                                    )
                                }
                                UpdateItemEntityUseCaseResult.PriceNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            price =
                                                currentState.price.toError(FieldError.NoValueError)
                                        )
                                    }
                                }
                                UpdateItemEntityUseCaseResult.PriceInvalid -> {
                                    Log.e(
                                        "ModifyItem",
                                        "Update invalid price `${state.price.data}`",
                                    )
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            price =
                                                currentState.price.toError(
                                                    FieldError.InvalidValueError
                                                )
                                        )
                                    }
                                }
                                UpdateItemEntityUseCaseResult.ProductIdNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            selectedProduct =
                                                currentState.selectedProduct.toError(
                                                    FieldError.NoValueError
                                                )
                                        )
                                    }
                                }
                                UpdateItemEntityUseCaseResult.ProductIdInvalid -> {
                                    Log.e(
                                        "ModifyItem",
                                        "Update invalid product `${state.selectedProduct.data?.id}`",
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
                                UpdateItemEntityUseCaseResult.ProductVariantIdInvalid -> {
                                    Log.e(
                                        "ModifyItem",
                                        "Update invalid product variant `${state.selectedProductVariant.data?.id}`",
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
                                UpdateItemEntityUseCaseResult.QuantityNoValue -> {
                                    _uiState.update { currentState ->
                                        currentState.copy(
                                            quantity =
                                                currentState.quantity.toError(
                                                    FieldError.NoValueError
                                                )
                                        )
                                    }
                                }
                                UpdateItemEntityUseCaseResult.QuantityInvalid -> {
                                    Log.e(
                                        "ModifyItem",
                                        "Update invalid quantity `${state.quantity.data}`",
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
                                UpdateItemEntityUseCaseResult.TransactionIdInvalid -> {
                                    Log.e(
                                        "ModifyItem",
                                        "Update invalid transaction `${state.currentItem.transactionEntityId}`",
                                    )
                                }
                            }
                        }

                        ModifyItemEventResult.Failure
                    }
                    is UpdateItemEntityUseCaseResult.Success -> ModifyItemEventResult.SuccessUpdate
                }
            }
            is ModifyItemEvent.DeleteItem -> {
                val state = uiState.value

                val result =
                    state.currentItem?.let { item -> deleteItemEntityUseCase(id = item.id) }
                        ?: return ModifyItemEventResult.SuccessDelete

                return when (result) {
                    is DeleteItemEntityUseCaseResult.Error -> {
                        result.errors.forEach {
                            when (it) {
                                DeleteItemEntityUseCaseResult.ItemIdInvalid -> {
                                    Log.e(
                                        "ModifyItem",
                                        "Delete invalid item id `${state.currentItem.id}`",
                                    )
                                }
                            }
                        }

                        ModifyItemEventResult.Failure
                    }

                    is DeleteItemEntityUseCaseResult.Success -> ModifyItemEventResult.SuccessDelete
                }
            }
            else -> return super.handleEvent(event)
        }
    }
}
