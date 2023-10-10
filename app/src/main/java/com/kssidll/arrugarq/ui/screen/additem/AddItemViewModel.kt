package com.kssidll.arrugarq.ui.screen.additem

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

internal data class AddItemScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val selectedProduct: MutableState<Product?> = mutableStateOf(null),
    val selectedProductError: MutableState<Boolean> = mutableStateOf(false),

    val selectedVariant: MutableState<ProductVariant?> = mutableStateOf(null),

    val selectedShop: MutableState<Shop?> = mutableStateOf(null),

    val quantity: MutableState<String> = mutableStateOf(String()),
    val quantityError: MutableState<Boolean> = mutableStateOf(false),

    val price: MutableState<String> = mutableStateOf(String()),
    val priceError: MutableState<Boolean> = mutableStateOf(false),

    val date: MutableState<Long?> = mutableStateOf(null),
    val dateError: MutableState<Boolean> = mutableStateOf(false),

    var isDatePickerDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isShopSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isProductSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isVariantSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Validates selectedProduct field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddItemScreenState.validateSelectedProduct(): Boolean {
    return !(selectedProduct.value == null).also { selectedProductError.value = it }
}

/**
 * Validates quantity field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddItemScreenState.validateQuantity(): Boolean {
    return !(quantity.value.replace(
        ',',
        '.'
    )
        .toFloatOrNull() == null).also { quantityError.value = it }
}

/**
 * Validates price field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddItemScreenState.validatePrice(): Boolean {
    return !(price.value.replace(
        ',',
        '.'
    )
        .toFloatOrNull() == null).also { priceError.value = it }
}

/**
 * Validates date field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddItemScreenState.validateDate(): Boolean {
    return !(date.value == null).also { dateError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
internal fun AddItemScreenState.validate(): Boolean {
    val product = validateSelectedProduct()
    val quantity = validateQuantity()
    val price = validatePrice()
    val date = validateDate()

    return product && quantity && price && date
}

/**
 * performs data validation and tries to extract embedded data
 * @return Null if validation sets error flags, extracted data otherwise
 */
internal fun AddItemScreenState.extractItemOrNull(): Item? {
    if (!validate()) return null

    return Item(
        productId = selectedProduct.value!!.id,
        variantId = selectedVariant.value?.id,
        shopId = selectedShop.value?.id,
        actualQuantity = quantity.value.replace(
            ',',
            '.'
        )
            .toFloat(),
        actualPrice = price.value.replace(
            ',',
            '.'
        )
            .toFloat(),
        date = date.value!!,
    )
}

@HiltViewModel
class AddItemViewModel @Inject constructor(
    itemRepository: IItemRepository,
    productRepository: IProductRepository,
    variantsRepository: IProductVariantRepository,
    shopRepository: IShopRepository,
): ViewModel() {
    internal val addItemScreenState: AddItemScreenState = AddItemScreenState()

    private val itemRepository: IItemRepository
    private val productRepository: IProductRepository
    private val variantsRepository: IProductVariantRepository
    private val shopRepository: IShopRepository

    private var variantsJob: Job? = null
    var variants: MutableState<Flow<List<ProductVariant>>> = mutableStateOf(flowOf())

    init {
        this.itemRepository = itemRepository
        this.productRepository = productRepository
        this.variantsRepository = variantsRepository
        this.shopRepository = shopRepository
    }

    suspend fun fetch() {
        val lastItem: Item? = itemRepository.getLast()

        if (addItemScreenState.selectedShop.value == null) {
            addItemScreenState.selectedShop.value = lastItem?.shopId?.let { shopRepository.get(it) }
        }

        if (addItemScreenState.date.value == null) {
            addItemScreenState.date.value = lastItem?.date
        }
    }

    /**
     * Tries to add item to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addItem(): Long? = viewModelScope.async {
        addItemScreenState.attemptedToSubmit.value = true
        val item = addItemScreenState.extractItemOrNull() ?: return@async null

        return@async itemRepository.insert(item)
    }
        .await()

    fun getShopsFlow(): Flow<List<Shop>> {
        return shopRepository.getAllFlow()
    }

    fun getProductsWithAltNamesFlow(): Flow<List<ProductWithAltNames>> {
        return productRepository.getAllWithAltNamesFlow()
    }

    fun queryProductVariants() {
        with(addItemScreenState.selectedProduct) {
            if (value != null) {
                variantsJob?.cancel()

                variantsJob = viewModelScope.launch {
                    variants.value = variantsRepository.getByProductFlow(value!!.id)
                        .cancellable()
                }
            }
        }
    }

    suspend fun fillStateWithSelectedProductLatestData() {
        val lastItemByProduct =
            itemRepository.getLastByProductId(addItemScreenState.selectedProduct.value!!.id)
                ?: return

        addItemScreenState.selectedVariant.value = lastItemByProduct.variantId?.let {
            variantsRepository.get(it)
        }

        addItemScreenState.price.value = String.format(
            "%.2f",
            lastItemByProduct.price / 100f
        )

        addItemScreenState.quantity.value = String.format(
            "%.3f",
            lastItemByProduct.quantity / 1000f
        )

        addItemScreenState.validate()
    }

}