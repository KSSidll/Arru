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
 * @property computeStartState Initializes start state, should be called as child in init of inheriting view model
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
    protected fun computeStartState() = viewModelScope.launch {
        screenState.selectedShop.apply { value = value.toLoading() }
        screenState.date.apply { value = value.toLoading() }

        val lastItem: Item? = itemRepository.getLast()

        screenState.selectedShop.apply {
            value = value.data?.let { value.toLoaded() }
                ?: Field.Loaded(lastItem?.shopId?.let { shopRepository.get(it) })
        }

        screenState.date.apply {
            value = value.data?.let { value.toLoaded() } ?: Field.Loaded(lastItem?.date)
        }
    }

    /**
     * Updates the screen state representation property values related to product to represent the product that is currently selected, should be called after the selected product changes
     */
    fun onProductChange() = viewModelScope.launch {
        screenState.selectedShop.apply { value = value.toLoading() }
        screenState.selectedVariant.apply { value = value.toLoading() }
        screenState.price.apply { value = value.toLoading() }
        screenState.quantity.apply { value = value.toLoading() }

        updateProductVariants()

        val lastItemByProduct: Item? = screenState.selectedProduct.value.data?.let {
            itemRepository.getLastByProductId(it.id)
        }

        screenState.selectedShop.apply {
            val shop: Shop? = lastItemByProduct?.shopId?.let { shopRepository.get(it) }
            value = Field.Loaded(shop)
        }

        screenState.selectedVariant.apply {
            val variant: ProductVariant? =
                lastItemByProduct?.variantId?.let { variantsRepository.get(it) }
            value = Field.Loaded(variant)
        }

        screenState.price.apply {
            val price: String = lastItemByProduct?.let {
                String.format(
                    "%.2f",
                    lastItemByProduct.price.toFloat() / Item.PRICE_DIVISOR
                )
            } ?: String()
            value = Field.Loaded(price)
        }

        screenState.quantity.apply {
            val quantity: String = lastItemByProduct?.let {
                String.format(
                    "%.3f",
                    lastItemByProduct.quantity.toFloat() / Item.QUANTITY_DIVISOR
                )
            } ?: String()
            value = Field.Loaded(quantity)
        }
    }

    /**
     * @return List of all shops
     */
    fun allShops(): Flow<List<Shop>> {
        return shopRepository.getAllFlow()
    }

    /**
     * @return List of all products
     */
    fun allProducts(): Flow<List<ProductWithAltNames>> {
        return productRepository.getAllWithAltNamesFlow()
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
                screenState.selectedProduct.value.data?.let { variantsRepository.getByProductIdFlow(it.id) }
                    ?: emptyFlow()
        }
    }

    /**
     * Updates data in the screen state
     * @return true if provided [itemId] was valid, false otherwise
     */
    suspend fun updateState(itemId: Long) = viewModelScope.async {
        screenState.selectedProduct.apply { value = value.toLoading() }
        screenState.selectedVariant.apply { value = value.toLoading() }
        screenState.selectedShop.apply { value = value.toLoading() }
        screenState.quantity.apply { value = value.toLoading() }
        screenState.price.apply { value = value.toLoading() }
        screenState.date.apply { value = value.toLoading() }

        val item: Item? = itemRepository.get(itemId)
        val product: Product? = item?.let { productRepository.get(item.productId) }
        val variant: ProductVariant? = item?.variantId?.let { variantsRepository.get(it) }
        val shop: Shop? = item?.shopId?.let { shopRepository.get(it) }

        screenState.selectedProduct.apply {
            value = item?.let { Field.Loaded(product) } ?: value.toLoadedOrError()
        }
        screenState.selectedVariant.apply {
            value = item?.let { Field.Loaded(variant) } ?: value.toLoaded()
        }
        screenState.selectedShop.apply {
            value = item?.let { Field.Loaded(shop) } ?: value.toLoaded()
        }
        screenState.quantity.apply {
            value = item?.let {
                Field.Loaded(
                    it.actualQuantity()
                        .toString()
                )
            } ?: value.toLoadedOrError()
        }
        screenState.price.apply {
            value = item?.let {
                Field.Loaded(
                    it.actualPrice()
                        .toString()
                )
            } ?: value.toLoadedOrError()
        }
        screenState.date.apply {
            value = item?.let { Field.Loaded(it.date) } ?: value.toLoadedOrError()
        }

        if (item == null) return@async false

        onProductChange()
        return@async true
    }
        .await()
}

/**
 * Data representing [ModifyItemScreenImpl] screen state
 */
data class ModifyItemScreenState(
    val selectedProduct: MutableState<Field<Product>> = mutableStateOf(Field.Loaded()),
    val selectedVariant: MutableState<Field<ProductVariant?>> = mutableStateOf(Field.Loaded()),
    val selectedShop: MutableState<Field<Shop?>> = mutableStateOf(Field.Loaded()),
    val quantity: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
    val price: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
    val date: MutableState<Field<Long>> = mutableStateOf(Field.Loaded()),

    var isDatePickerDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isShopSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isProductSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isVariantSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
): ModifyScreenState<Item>() {

    /**
     * Validates selectedProduct field and updates its error flag
     * @return true if field is of correct value, false otherwise
     */
    fun validateSelectedProduct(): Boolean {
        selectedProduct.apply {
            if (value.data == null) {
                value = value.toError(FieldError.NoValueError)
            }

            return value.isNotError()
        }
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

    /**
     * Validates date field and updates its error flag
     * @return true if field is of correct value, false otherwise
     */
    fun validateDate(): Boolean {
        date.apply {
            if (value.data == null) {
                value = value.toError(FieldError.NoValueError)
            }

            return value.isNotError()
        }
    }

    override fun validate(): Boolean {
        val product = validateSelectedProduct()
        val quantity = validateQuantity()
        val price = validatePrice()
        val date = validateDate()

        return product && quantity && price && date
    }

    override fun extractDataOrNull(id: Long): Item? {
        if (!validate()) return null

        return Item(
            id = id,
            productId = selectedProduct.value.data?.id ?: return null,
            variantId = selectedVariant.value.data?.id,
            shopId = selectedShop.value.data?.id,
            actualQuantity = quantity.value.data?.let { StringHelper.toDoubleOrNull(it) }
                ?: return null,
            actualPrice = price.value.data?.let { StringHelper.toDoubleOrNull(it) } ?: return null,
            date = date.value.data ?: return null,
        )
    }

}