package com.kssidll.arrugarq.ui.screen.additem

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.shared.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddItemViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
    private val productRepository: IProductRepository,
    private val variantsRepository: IProductVariantRepository,
    private val shopRepository: IShopRepository,
): ViewModel() {
    internal val screenState: EditItemScreenState = EditItemScreenState()

    init {
        computeStartState()
        fillShops()
        fillProductsWithAltNames()
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
        screenState.loadingVariants.value = true

        run {
            val lastItemByProduct =
                itemRepository.getLastByProductId(screenState.selectedProduct.value!!.id)
                    ?: return@run

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

            fillProductVariants()
        }

        screenState.loadingVariants.value = false
        screenState.loadingQuantity.value = false
        screenState.loadingPrice.value = false
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

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillShops() = viewModelScope.launch {
        screenState.shops.clear()
        screenState.shops.addAll(shopRepository.getAll())
    }

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillProductsWithAltNames() = viewModelScope.launch {
        screenState.productsWithAltNames.clear()
        screenState.productsWithAltNames.addAll(productRepository.getAllWithAltNames())
    }

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillProductVariants() = viewModelScope.launch {
        with(screenState.selectedProduct) {
            if (value != null) {
                screenState.loadingVariants.value = true

                screenState.variants.clear()
                screenState.variants.addAll(variantsRepository.getByProduct(value!!.id))

                screenState.loadingVariants.value = false
            }
        }
    }

}