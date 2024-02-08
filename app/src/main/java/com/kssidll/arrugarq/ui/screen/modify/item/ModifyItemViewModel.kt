package com.kssidll.arrugarq.ui.screen.modify.item

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.screen.modify.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Base [ViewModel] class for Item modification view models
 * @property loadLastItem Initializes start state, should be called as child in init of inheriting view model
 * @property screenState A [ModifyItemScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Item matching provided id, only changes representation data and loading state
 * @property onProductChange Updates the screen state representation property values related to product to represent the product that is currently selected, should be called after the selected product changes
 */
abstract class ModifyItemViewModel: ViewModel() {
    protected abstract val itemRepository: ItemRepositorySource
    protected abstract val productRepository: ProductRepositorySource
    protected abstract val variantsRepository: VariantRepositorySource
    protected abstract val shopRepository: ShopRepositorySource

    internal val screenState: ModifyItemScreenState = ModifyItemScreenState()

    /**
     * Fetches start data to state
     */
    protected fun loadLastItem() = viewModelScope.launch {
        screenState.allToLoading()

        val lastItem: Item? = itemRepository.newest()

        updateStateForItem(lastItem)
    }

    /**
     * Updates the screen state representation property values related to product to represent the product that is currently selected, should be called after the selected product changes
     */
    fun onProductChange() = viewModelScope.launch {
        screenState.selectedVariant.apply { value = value.toLoading() }
        screenState.price.apply { value = value.toLoading() }
        screenState.quantity.apply { value = value.toLoading() }

        val lastItemByProduct: Item? = screenState.selectedProduct.value.data?.let {
            productRepository.newestItem(it)
        }

        updateStateForItem(
            item = lastItemByProduct,
            updateProduct = false,
        )

        updateProductVariants()
    }

    /**
     * @return List of all products
     */
    fun allProducts(): Flow<List<ProductWithAltNames>> {
        return productRepository.allWithAltNamesFlow()
    }

    private val mProductVariants: MutableState<Flow<List<ProductVariant>>> =
        mutableStateOf(flowOf())
    val productVariants: Flow<List<ProductVariant>> by mProductVariants
    private var mUpdateProductVariantsJob: Job? = null

    /**
     * Updates [productVariants] to represent available variants for currently set product
     */
    private fun updateProductVariants() {
        mUpdateProductVariantsJob?.cancel()
        mUpdateProductVariantsJob = viewModelScope.launch {
            mProductVariants.value =
                screenState.selectedProduct.value.data?.let { variantsRepository.byProductFlow(it) }
                    ?: emptyFlow()
        }
    }

    /**
     * Updates data in the screen state
     * @return true if provided [itemId] was valid, false otherwise
     */
    suspend fun updateState(itemId: Long) = viewModelScope.async {
        screenState.allToLoading()

        val item: Item? = itemRepository.get(itemId)

        updateStateForItem(item)

        return@async item != null
    }
        .await()

    /**
     * Updates the state to represent [item], doesn't switch state to loading status as it should be done before fetching the item
     */
    private suspend fun updateStateForItem(
        item: Item?,
        updatePrice: Boolean = true,
        updateQuantity: Boolean = true,
        updateProduct: Boolean = true,
        updateVariant: Boolean = true,
    ) {
        val itemProduct: Product? = item?.productId?.let { productRepository.get(it) }
        val itemProductVariant: ProductVariant? =
            item?.variantId?.let { variantsRepository.get(it) }

        if (updatePrice) {
            screenState.price.apply {
                value = Field.Loaded(
                    item?.actualPrice()
                        ?.toString()
                )
            }
        }

        if (updateQuantity) {
            screenState.quantity.apply {
                value = Field.Loaded(
                    item?.actualQuantity()
                        ?.toString()
                )
            }
        }

        if (updateProduct) {
            screenState.selectedProduct.apply {
                value = Field.Loaded(itemProduct)
                updateProductVariants()
            }
        }

        if (updateVariant) {
            screenState.selectedVariant.apply {
                value = Field.Loaded(itemProductVariant)
            }
        }
    }
}

/**
 * Data representing [ModifyItemScreenImpl] screen state
 */
data class ModifyItemScreenState(
    val selectedProduct: MutableState<Field<Product>> = mutableStateOf(Field.Loaded()),
    val selectedVariant: MutableState<Field<ProductVariant?>> = mutableStateOf(Field.Loaded()),
    val quantity: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
    val price: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),

    var isDatePickerDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isProductSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isVariantSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
): ModifyScreenState<Item>() {

    /**
     * Sets all fields to Loading status
     */
    fun allToLoading() {
        price.apply { value = value.toLoading() }
        quantity.apply { value = value.toLoading() }
        selectedProduct.apply { value = value.toLoading() }
        selectedVariant.apply { value = value.toLoading() }
    }

    /**
     * Validates quantity field and updates its error flag
     * @return true if field is of correct value, false otherwise
     */
    fun validateQuantity(): Boolean {
        quantity.apply {
            if (value.data.isNullOrBlank()) {
                value = value.toError(FieldError.NoValueError)
            } else if (StringHelper.toDoubleOrNull(value.data!!) == null) {
                value = value.toError(FieldError.InvalidValueError)
            }

            return value.isNotError()
        }
    }

    /**
     * Validates price field and updates its error flag
     * @return true if field is of correct value, false otherwise
     */
    fun validatePrice(): Boolean {
        price.apply {
            if (value.data.isNullOrBlank()) {
                value = value.toError(FieldError.NoValueError)
            } else if (StringHelper.toDoubleOrNull(value.data!!) == null) {
                value = value.toError(FieldError.InvalidValueError)
            }

            return value.isNotError()
        }
    }

}