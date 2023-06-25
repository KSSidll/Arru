package com.kssidll.arrugarq.ui.addshop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.Shop
import com.kssidll.arrugarq.data.repository.IShopRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddShopViewModel @Inject constructor(
    shopRepository: IShopRepository,
) : ViewModel() {
    private val shopRepository: IShopRepository

    init {
        this.shopRepository = shopRepository
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addShop(shopData: AddShopData) = viewModelScope.launch {
        shopRepository.insert(
            Shop(
                name = shopData.name.trim(),
            )
        )
    }
}