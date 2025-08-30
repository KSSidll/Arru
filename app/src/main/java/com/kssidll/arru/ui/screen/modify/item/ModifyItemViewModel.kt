package com.kssidll.arru.ui.screen.modify.item

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.usecase.data.GetAllProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetNewestItemEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetNewestItemEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductEntityUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityByProductUseCase
import com.kssidll.arru.domain.usecase.data.GetProductVariantEntityUseCase
import com.kssidll.arru.helper.RegexHelper
import com.kssidll.arru.helper.StringHelper
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Immutable
data class ModifyItemUiState(
    val currentItem: ItemEntity? = null,
    val allProducts: ImmutableList<ProductEntity> = emptyImmutableList(),
    val allProductVariants: ImmutableList<ProductVariantEntity> = emptyImmutableList(),
    val selectedProduct: Field<ProductEntity?> = Field.Loaded(null),
    val selectedProductVariant: Field<ProductVariantEntity?> = Field.Loaded(null),
    val quantity: Field<String> = Field.Loaded(String()),
    val price: Field<String> = Field.Loaded(String()),
    val isDatePickerDialogExpanded: Boolean = false,
    val isProductSearchDialogExpanded: Boolean = false,
    val isProductVariantSearchDialogExpanded: Boolean = false,
    val isDeleteEnabled: Boolean = false,
)

@Immutable
sealed class ModifyItemEvent {
    data object NavigateBack : ModifyItemEvent()

    data object Submit : ModifyItemEvent()

    data object DeleteItem : ModifyItemEvent()

    data class SelectProduct(val productId: Long?) : ModifyItemEvent()

    data class SelectProductVariant(val productVariantId: Long?) : ModifyItemEvent()

    data class SetProductSearchDialogVisibility(val visible: Boolean) : ModifyItemEvent()

    data class SetProductVariantSearchDialogVisibility(val visible: Boolean) : ModifyItemEvent()

    data class SetPrice(val price: String) : ModifyItemEvent()

    data object IncrementPrice : ModifyItemEvent()

    data object DecrementPrice : ModifyItemEvent()

    data class SetQuantity(val quantity: String) : ModifyItemEvent()

    data object IncrementQuantity : ModifyItemEvent()

    data object DecrementQuantity : ModifyItemEvent()

    data class NavigateEditProduct(val productId: Long) : ModifyItemEvent()

    data class NavigateEditProductVariant(val productVariantId: Long) : ModifyItemEvent()

    data class NavigateAddProduct(val name: String) : ModifyItemEvent()

    data class NavigateAddProductVariant(val productVariantId: Long, val name: String) :
        ModifyItemEvent()
}

sealed class ModifyItemEventResult {
    data object Success : ModifyItemEventResult()

    data object Failure : ModifyItemEventResult()

    data class SuccessInsert(val id: Long) : ModifyItemEventResult()

    data object SuccessUpdate : ModifyItemEventResult()

    data object SuccessDelete : ModifyItemEventResult()
}

abstract class ModifyItemViewModel : ViewModel() {
    @Suppress("PropertyName") protected val _uiState = MutableStateFlow(ModifyItemUiState())
    val uiState = _uiState.asStateFlow()

    protected abstract val getNewestItemEntityUseCase: GetNewestItemEntityUseCase
    protected abstract val getNewestItemEntityByProductUseCase: GetNewestItemEntityByProductUseCase
    protected abstract val getProductEntityUseCase: GetProductEntityUseCase
    protected abstract val getAllProductEntityUseCase: GetAllProductEntityUseCase
    protected abstract val getProductVariantEntityUseCase: GetProductVariantEntityUseCase
    protected abstract val getProductVariantEntityByProductUseCase:
        GetProductVariantEntityByProductUseCase

    protected var manuallySetProductVariant = false
    protected var manuallySetPrice = false
    protected var manuallySetQuantity = false

    private var _productListener: Job? = null
    private var _productVariantListener: Job? = null

    fun init() {
        viewModelScope.launch {
            getAllProductEntityUseCase().collectLatest {
                _uiState.update { currentState -> currentState.copy(allProducts = it) }
            }
        }
    }

    open suspend fun handleEvent(event: ModifyItemEvent): ModifyItemEventResult {
        when (event) {
            is ModifyItemEvent.NavigateBack -> {}
            is ModifyItemEvent.NavigateEditProduct -> {}
            is ModifyItemEvent.NavigateEditProductVariant -> {}
            is ModifyItemEvent.NavigateAddProduct -> {}
            is ModifyItemEvent.NavigateAddProductVariant -> {}
            is ModifyItemEvent.Submit -> {}
            is ModifyItemEvent.DeleteItem -> {}
            is ModifyItemEvent.SelectProduct -> handleSelectProduct(event.productId)
            is ModifyItemEvent.SelectProductVariant ->
                handleSelectProductVariant(event.productVariantId)
            is ModifyItemEvent.SetProductSearchDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isProductSearchDialogExpanded = event.visible)
                }
            }
            is ModifyItemEvent.SetProductVariantSearchDialogVisibility -> {
                _uiState.update { currentState ->
                    currentState.copy(isProductVariantSearchDialogExpanded = event.visible)
                }
            }

            is ModifyItemEvent.SetPrice -> {
                manuallySetPrice = true
                if (event.price.isBlank()) {
                    _uiState.update { currentState ->
                        currentState.copy(price = Field.Loaded(String()))
                    }
                } else if (RegexHelper.isFloat(event.price, 2)) {
                    _uiState.update { currentState ->
                        currentState.copy(price = Field.Loaded(event.price))
                    }
                }
            }
            is ModifyItemEvent.IncrementPrice -> {
                manuallySetPrice = true
                _uiState.update { currentState ->
                    if (currentState.price.data.isBlank()) {
                        currentState.copy(price = Field.Loaded("%.2f".format(0f)))
                    } else {
                        val value = currentState.price.data.let { StringHelper.toDoubleOrNull(it) }

                        if (value != null) {
                            currentState.copy(price = Field.Loaded("%.2f".format(value.plus(0.5f))))
                        } else currentState
                    }
                }
            }
            is ModifyItemEvent.DecrementPrice -> {
                manuallySetPrice = true
                _uiState.update { currentState ->
                    if (currentState.price.data.isBlank()) {
                        currentState.copy(price = Field.Loaded("%.2f".format(0f)))
                    } else {
                        val value = currentState.price.data.let { StringHelper.toDoubleOrNull(it) }

                        if (value != null) {
                            currentState.copy(
                                price =
                                    Field.Loaded(
                                        "%.2f"
                                            .format(
                                                if (value > 0.5f) value.minus(0.5f)
                                                else {
                                                    0f
                                                }
                                            )
                                    )
                            )
                        } else currentState
                    }
                }
            }
            is ModifyItemEvent.SetQuantity -> {
                manuallySetQuantity = true
                if (event.quantity.isBlank()) {
                    _uiState.update { currentState ->
                        currentState.copy(quantity = Field.Loaded(String()))
                    }
                } else if (RegexHelper.isFloat(event.quantity, 3)) {
                    _uiState.update { currentState ->
                        currentState.copy(quantity = Field.Loaded(event.quantity))
                    }
                }
            }
            is ModifyItemEvent.IncrementQuantity -> {
                manuallySetQuantity = true
                _uiState.update { currentState ->
                    if (currentState.quantity.data.isBlank()) {
                        currentState.copy(quantity = Field.Loaded("%.3f".format(0f)))
                    } else {
                        val value =
                            currentState.quantity.data.let { StringHelper.toDoubleOrNull(it) }

                        if (value != null) {
                            currentState.copy(
                                quantity = Field.Loaded("%.3f".format(value.plus(0.5f)))
                            )
                        } else currentState
                    }
                }
            }
            is ModifyItemEvent.DecrementQuantity -> {
                manuallySetQuantity = true
                _uiState.update { currentState ->
                    if (currentState.quantity.data.isBlank()) {
                        currentState.copy(quantity = Field.Loaded("%.3f".format(0f)))
                    } else {
                        val value =
                            currentState.quantity.data.let { StringHelper.toDoubleOrNull(it) }

                        if (value != null) {
                            currentState.copy(
                                quantity =
                                    Field.Loaded(
                                        "%.3f"
                                            .format(
                                                if (value > 0.5f) value.minus(0.5f)
                                                else {
                                                    0f
                                                }
                                            )
                                    )
                            )
                        } else currentState
                    }
                }
            }
        }

        return ModifyItemEventResult.Success
    }

    protected fun handleSelectProduct(productId: Long?) =
        viewModelScope.launch {
            cancelProductListener()
            cancelProductVariantListener()

            if (productId == null) {
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedProduct = Field.Loaded(null),
                        selectedProductVariant = Field.Loaded(null),
                    )
                }
                return@launch
            }

            val productVariantId = _uiState.value.selectedProductVariant.data?.id

            // loading kinda not necessary here since it only makes a visual glitch, could be ok on
            // extremely slow devices maybe?
            // _uiState.update { currentState ->
            //     currentState.copy(
            //         selectedProduct = Field.Loading(),
            //         selectedProductVariant = Field.Loading(),
            //     )
            // }

            val newProduct = getProductEntityUseCase(productId).first()
            val matchingByNameVariant =
                if (manuallySetProductVariant && newProduct != null && productVariantId != null) {
                    getProductVariantEntityUseCase(productVariantId).first()?.name?.let {
                        variantName ->
                        getProductVariantEntityByProductUseCase(newProduct.id).first().firstOrNull {
                            it.name == variantName
                        }
                    }
                } else {
                    null
                }

            loadLastItemForProductDataIfNotManuallySet(productId)

            if (newProduct == null) {
                Log.e("ModifyItemViewModel", "Selected null product")
                manuallySetProductVariant = false
                _uiState.update { currentState ->
                    currentState.copy(
                        selectedProduct = Field.Loaded(null),
                        selectedProductVariant = Field.Loaded(null),
                    )
                }
            } else if (manuallySetProductVariant && productVariantId == null) {
                // if variant was manually set and is null we want to preserve it
                setProductListener(productId)
                _uiState.update { currentState ->
                    currentState.copy(selectedProductVariant = Field.Loaded(null))
                }
            } else if (manuallySetProductVariant && matchingByNameVariant != null) {
                // if variant was manually set and name exists in new product we want to preserve it
                setProductListener(productId)
                setProductVariantListener(matchingByNameVariant.id)
            } else {
                // else change to last used variant for the product
                manuallySetProductVariant = false
                val lastProductVariantId =
                    getNewestItemEntityByProductUseCase(productId).first()?.productVariantEntityId

                if (lastProductVariantId == null) {
                    setProductListener(productId)
                    _uiState.update { currentState ->
                        currentState.copy(selectedProductVariant = Field.Loaded(null))
                    }
                } else {
                    setProductListener(productId)
                    setProductVariantListener(lastProductVariantId)
                }
            }
        }

    protected fun handleSelectProductVariant(productVariantId: Long?) {
        manuallySetProductVariant = true
        setProductVariantListener(productVariantId)
    }

    protected suspend fun loadLastItemForProductDataIfNotManuallySet(productId: Long) {
        if (manuallySetPrice && manuallySetQuantity) return

        getNewestItemEntityByProductUseCase(productId).first()?.let { item ->
            if (!manuallySetPrice && !manuallySetQuantity) {
                _uiState.update { currentState ->
                    currentState.copy(
                        price = Field.Loaded("%.2f".format(item.actualPrice())),
                        quantity = Field.Loaded("%.3f".format(item.actualQuantity())),
                    )
                }
                return@let
            }

            if (!manuallySetPrice) {
                _uiState.update { currentState ->
                    currentState.copy(price = Field.Loaded("%.2f".format(item.actualPrice())))
                }
                return@let
            }

            if (!manuallySetQuantity) {
                _uiState.update { currentState ->
                    currentState.copy(quantity = Field.Loaded("%.3f".format(item.actualQuantity())))
                }

                return@let
            }
        }
    }

    protected fun cancelProductListener() {
        _productListener?.cancel()
        _uiState.update { currentState ->
            currentState.copy(allProductVariants = emptyImmutableList())
        }
    }

    protected fun setProductListener(productId: Long?) {
        cancelProductListener()
        if (productId == null) {
            _uiState.update { currentState ->
                cancelProductVariantListener()
                currentState.copy(
                    selectedProduct = Field.Loaded(null),
                    selectedProductVariant = Field.Loaded(null),
                )
            }
            return
        }

        _productListener =
            viewModelScope.launch {
                viewModelScope.launch {
                    getProductVariantEntityByProductUseCase(productId).collectLatest {
                        _uiState.update { currentState ->
                            currentState.copy(allProductVariants = it)
                        }
                    }
                }

                viewModelScope.launch {
                    getProductEntityUseCase(productId).collectLatest {
                        _uiState.update { currentState ->
                            if (it == null) {
                                // null product entity means it was deleted, so we want to remove
                                // the variant too
                                cancelProductVariantListener()
                                currentState.copy(
                                    selectedProduct = Field.Loaded(null),
                                    selectedProductVariant = Field.Loaded(null),
                                )
                            } else {
                                currentState.copy(selectedProduct = Field.Loaded(it))
                            }
                        }
                    }
                }
            }
    }

    protected fun cancelProductVariantListener() {
        _productVariantListener?.cancel()
    }

    protected fun setProductVariantListener(productVariantId: Long?) {
        cancelProductVariantListener()
        if (productVariantId == null) {
            _uiState.update { currentState ->
                currentState.copy(selectedProductVariant = Field.Loaded(null))
            }
            return
        }

        _productVariantListener =
            viewModelScope.launch {
                getProductVariantEntityUseCase(productVariantId).collectLatest {
                    _uiState.update { currentState ->
                        currentState.copy(selectedProductVariant = Field.Loaded(it))
                    }
                }
            }
    }
}
