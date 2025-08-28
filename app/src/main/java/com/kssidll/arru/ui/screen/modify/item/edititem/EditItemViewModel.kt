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
    private var mItem: ItemEntity? = null

    suspend fun checkExists(id: Long): Boolean {
        return getItemEntityUseCase(id).first() != null
    }

    fun updateState(itemId: Long) =
        viewModelScope.launch {
            // skip state update for repeating itemId
            if (itemId == mItem?.id) return@launch

            _uiState.update { currentState ->
                currentState.copy(
                    price = currentState.price.toLoading(),
                    quantity = currentState.quantity.toLoading(),
                    selectedProduct = currentState.selectedProduct.toLoading(),
                    selectedProductVariant = currentState.selectedProductVariant.toLoading(),
                )
            }

            mItem = getItemEntityUseCase(itemId).first()

            updateStateForItem(mItem)
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

        _uiState.update { currentState -> currentState.copy(isDeleteVisible = true) }
    }

    override suspend fun handleEvent(event: ModifyItemEvent): Boolean {
        when (event) {
            is ModifyItemEvent.Submit -> {
                val state = uiState.value
                val updateResult =
                    mItem?.let { item ->
                        updateItemEntityUseCase(
                            id = item.id,
                            transactionEntityId = item.transactionEntityId,
                            productEntityId = state.selectedProduct.data?.id,
                            productVariantEntityId = state.selectedProductVariant.data?.id,
                            quantity = state.quantity.data,
                            price = state.price.data,
                        )
                    } ?: return true

                return when (updateResult) {
                    is UpdateItemEntityUseCaseResult.Error -> {
                        updateResult.errors.forEach {
                            when (it) {
                                UpdateItemEntityUseCaseResult.ItemIdInvalid -> {
                                    Log.e("ModifyItem", "Insert invalid item `${mItem?.id}`")
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
                                        "Insert invalid price `${state.price.data}`",
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
                                UpdateItemEntityUseCaseResult.ProductVariantIdInvalid -> {
                                    Log.e(
                                        "ModifyItem",
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
                                UpdateItemEntityUseCaseResult.TransactionIdInvalid -> {
                                    Log.e(
                                        "ModifyItem",
                                        "Insert invalid transaction `${mItem?.transactionEntityId}`",
                                    )
                                }
                            }
                        }

                        false
                    }
                    is UpdateItemEntityUseCaseResult.Success -> true
                }
            }
            is ModifyItemEvent.DeleteItem -> {
                val deleteResult =
                    mItem?.let { item -> deleteItemEntityUseCase(id = item.id) } ?: return true

                return when (deleteResult) {
                    is DeleteItemEntityUseCaseResult.Error -> {
                        deleteResult.errors.forEach {
                            when (it) {
                                DeleteItemEntityUseCaseResult.ItemIdInvalid -> {
                                    Log.e("EditItem", "Delete invalid item id `${mItem?.id}`")
                                }
                            }
                        }

                        false
                    }

                    is DeleteItemEntityUseCaseResult.Success -> true
                }
            }
            else -> return super.handleEvent(event)
        }
    }
}
