package com.kssidll.arrugarq.ui.additem

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.Item
import com.kssidll.arrugarq.data.data.ProductWithAltNames
import com.kssidll.arrugarq.data.data.Shop
import com.kssidll.arrugarq.data.repository.IItemRepository
import com.kssidll.arrugarq.data.repository.IProductRepository
import com.kssidll.arrugarq.data.repository.IShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class AddItemViewModel @Inject constructor(
    itemRepository: IItemRepository,
    productRepository: IProductRepository,
    shopRepository: IShopRepository,
) : ViewModel() {
    private val itemRepository: IItemRepository
    private val productRepository: IProductRepository
    private val shopRepository: IShopRepository

    var addItemState: AddItemState = AddItemState()

    init {
        this.itemRepository = itemRepository
        this.productRepository = productRepository
        this.shopRepository = shopRepository
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addItem(itemData: AddItemData) = viewModelScope.launch {
        itemRepository.insert(
            Item(
                productId = itemData.productId,
                shopId = itemData.shopId.getOrNull(),
                quantity = itemData.quantity,
                unitMeasure = itemData.unitMeasure.getOrNull(),
                price = (itemData.price * 100).toLong(),
                date = itemData.date,
            )
        )
    }

    fun getShopsFlow(): Flow<List<Shop>> {
        return shopRepository.getAllFlow()
    }

    fun getProductsWithAltNamesFlow(): Flow<List<ProductWithAltNames>> {
        return productRepository.getAllWithAltNamesFlow()
    }
}