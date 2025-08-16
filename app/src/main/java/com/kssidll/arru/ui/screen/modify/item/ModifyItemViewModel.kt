package com.kssidll.arru.ui.screen.modify.item

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ItemEntity
import com.kssidll.arru.data.data.ProductEntity
import com.kssidll.arru.data.data.ProductVariantEntity
import com.kssidll.arru.data.repository.ItemRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.data.repository.ProductVariantRepositorySource
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.ModifyScreenState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Base [ViewModel] class for Item modification view models
 * @property loadLastItem Initializes start state, should be called as child in init of inheriting view model
 * @property screenState A [ModifyItemScreenState] instance to use as screen state representation
 */
abstract class ModifyItemViewModel: ViewModel() {
    private var mProductListener: Job? = null
    private var mVariantListener: Job? = null

    protected abstract val itemRepository: ItemRepositorySource
    protected abstract val productRepository: ProductRepositorySource
    protected abstract val variantsRepository: ProductVariantRepositorySource

    internal val screenState: ModifyItemScreenState = ModifyItemScreenState()

    suspend fun setSelectedProductToProvided(
        providedProductId: Long?,
        providedVariantId: Long?
    ) {
        if (providedProductId != null) {
            screenState.selectedProduct.apply { value = value.toLoading() }
            screenState.selectedVariant.apply { value = value.toLoading() }

            val product: ProductEntity? = providedProductId.let { productRepository.get(it).first() }
            val variant: ProductVariantEntity? = providedVariantId?.let { variantsRepository.get(it).first() }

            // providedVariantId is null only when we create a new product
            // doing this allows us to skip data re-update on variant change
            // not doing this would wipe user input data to last item data on variant change
            // which is an unexpected behavior
            onNewProductSelected(
                product,
                providedVariantId == null
            )

            onNewVariantSelected(variant)
        }
    }

    suspend fun onNewProductSelected(
        product: ProductEntity?,
        loadLastItemProductData: Boolean = true
    ) {
        // Don't do anything if the product is the same as already selected
        if (screenState.selectedProduct.value.data == product) {
            screenState.selectedProduct.apply { value = value.toLoaded() }
            return
        }

        screenState.selectedProduct.value = Field.Loaded(product)
        updateProductVariants()

        setNewProductListener(product)
        setNewVariantListener(null)

        if (loadLastItemProductData) {
            loadLastItemDataForProduct(product)
        }
    }

    fun onNewVariantSelected(variant: ProductVariantEntity?) {
        // Don't do anything if the variant is the same as already selected
        if (screenState.selectedVariant.value.data == variant) {
            screenState.selectedVariant.apply { value = value.toLoaded() }
            return
        }

        screenState.selectedVariant.value = Field.Loaded(variant)
        setNewVariantListener(variant)
    }

    private fun setNewProductListener(product: ProductEntity?) {
        mProductListener?.cancel()
        if (product != null) {
            mProductListener = viewModelScope.launch {
                productRepository.get(product.id)
                    .collectLatest {
                        screenState.selectedProduct.value = Field.Loaded(it)
                    }
            }
        }
    }

    private fun setNewVariantListener(variant: ProductVariantEntity?) {
        mVariantListener?.cancel()
        if (variant != null) {
            mVariantListener = viewModelScope.launch {
                variantsRepository.get(variant.id)
                    .collectLatest {
                        screenState.selectedVariant.value = Field.Loaded(it)
                    }
            }
        }
    }

    private suspend fun loadLastItemDataForProduct(product: ProductEntity?) {
        screenState.selectedVariant.apply { value = value.toLoading() }
        screenState.price.apply { value = value.toLoading() }
        screenState.quantity.apply { value = value.toLoading() }

        val lastItem: ItemEntity? = product?.let {
            productRepository.newestItem(it)
        }

        val variant: ProductVariantEntity? = lastItem?.productVariantEntityId?.let { variantsRepository.get(it).first() }
        val price: String? = lastItem?.actualPrice()
            ?.toString()
        val quantity: String? = lastItem?.actualQuantity()
            ?.toString()

        onNewVariantSelected(variant)

        screenState.price.value = Field.Loaded(price)
        screenState.quantity.value = Field.Loaded(quantity)
    }

    /**
     * Fetches start data to state
     */
    protected fun loadLastItem() = viewModelScope.launch {
        screenState.allToLoading()

        val lastItem: ItemEntity? = itemRepository.newest().first()

        updateStateForItem(lastItem)
    }

    /**
     * @return List of all products
     */
    fun allProducts(): Flow<ImmutableList<ProductEntity>> {
        return productRepository.all()
    }

    private val mProductVariants: MutableState<Flow<ImmutableList<ProductVariantEntity>>> =
        mutableStateOf(emptyFlow())
    val productVariants: Flow<ImmutableList<ProductVariantEntity>> by mProductVariants
    private var mUpdateProductVariantsJob: Job? = null

    /**
     * Updates [productVariants] to represent available variants for currently set product
     */
    private fun updateProductVariants() {
        mUpdateProductVariantsJob?.cancel()
        mUpdateProductVariantsJob = viewModelScope.launch {
            mProductVariants.value =
                screenState.selectedProduct.value.data?.let { variantsRepository.byProduct(it, true) }
                    ?: emptyFlow()
        }
    }

    /**
     * Updates the state to represent [item], doesn't switch state to loading status as it should be done before fetching the item
     */
    protected suspend fun updateStateForItem(
        item: ItemEntity?,
    ) {
        val product: ProductEntity? = item?.productEntityId?.let { productRepository.get(it).first() }
        val variant: ProductVariantEntity? = item?.productVariantEntityId?.let { variantsRepository.get(it).first() }
        val price: String? = item?.actualPrice()
            ?.toString()
        val quantity: String? = item?.actualQuantity()
            ?.toString()

        onNewProductSelected(
            product,
            false
        )
        onNewVariantSelected(variant)
        screenState.price.value = Field.Loaded(price)
        screenState.quantity.value = Field.Loaded(quantity)
    }
}

/**
 * Data representing [ModifyItemScreenImpl] screen state
 */
data class ModifyItemScreenState(
    val selectedProduct: MutableState<Field<ProductEntity>> = mutableStateOf(Field.Loaded()),
    val selectedVariant: MutableState<Field<ProductVariantEntity?>> = mutableStateOf(Field.Loaded()),
    val quantity: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),
    val price: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),

    var isDatePickerDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isProductSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    var isVariantSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
): ModifyScreenState() {

    /**
     * Sets all fields to Loading status
     */
    fun allToLoading() {
        price.apply { value = value.toLoading() }
        quantity.apply { value = value.toLoading() }
        selectedProduct.apply { value = value.toLoading() }
        selectedVariant.apply { value = value.toLoading() }
    }
}