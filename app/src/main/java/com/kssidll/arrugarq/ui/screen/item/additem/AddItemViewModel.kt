package com.kssidll.arrugarq.ui.screen.item.additem

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.item.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val productRepository: IProductRepository,
    private val variantsRepository: IVariantRepository,
    private val shopRepository: IShopRepository,
): ViewModel() {
    internal val screenState: EditItemScreenState = EditItemScreenState()

    init {
        computeStartState()
        fillStateShops()
        fillStateProductsWithAltNames()
    }

    private fun computeStartState() = viewModelScope.launch {
        screenState.loadingShop.value = true
        screenState.loadingDate.value = true

        val lastItem: Item? = itemRepository.getLast()

        if (screenState.selectedShop.value == null) {
            screenState.selectedShop.value = lastItem?.shopId?.let { shopRepository.get(it) }
        }

        if (screenState.date.value == null) {
            screenState.date.value = lastItem?.date
        }

        screenState.loadingDate.value = false
        screenState.loadingShop.value = false
    }

    fun onProductChange() = viewModelScope.launch {
        screenState.loadingPrice.value = true
        screenState.loadingQuantity.value = true

        fillStateProductVariants()

        val lastItemByProduct =
            itemRepository.getLastByProductId(screenState.selectedProduct.value!!.id)
                ?: return@launch

        screenState.selectedVariant.value = lastItemByProduct.variantId?.let {
            variantsRepository.get(it)
        }

        screenState.price.value = String.format(
            "%.2f",
            lastItemByProduct.price / 100f
        )

        screenState.quantity.value = String.format(
            "%.3f",
            lastItemByProduct.quantity / 1000f
        )
    }
        .invokeOnCompletion {
            screenState.loadingQuantity.value = false
            screenState.loadingPrice.value = false

            screenState.validateQuantity()
            screenState.validatePrice()
        }

    /**
     * Tries to add item to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addItem(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        val item = screenState.extractItemOrNull() ?: return@async null

        return@async itemRepository.insert(item)
    }
        .await()

    private var fillStateShopsJob: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateShops() {
        fillStateShopsJob?.cancel()
        fillStateShopsJob = viewModelScope.launch {
            screenState.shops.value = shopRepository.getAllFlow()
        }
    }

    private var fillStateProductsWithAltNamesJob: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateProductsWithAltNames() {
        fillStateProductsWithAltNamesJob?.cancel()
        fillStateProductsWithAltNamesJob = viewModelScope.launch {
            screenState.productsWithAltNames.value = productRepository.getAllWithAltNamesFlow()
        }
    }

    private var fillStateProductVariantsJob: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateProductVariants() {
        fillStateProductVariantsJob?.cancel()
        fillStateProductVariantsJob = viewModelScope.launch {
            with(screenState.selectedProduct) {
                if (value != null) {
                    screenState.variants.value = variantsRepository.getByProductIdFlow(value!!.id)
                }
            }
        }
    }

}