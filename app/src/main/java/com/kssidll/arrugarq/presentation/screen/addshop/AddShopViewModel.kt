package com.kssidll.arrugarq.presentation.screen.addshop

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddShopViewModel @Inject constructor(
    shopRepository: IShopRepository,
): ViewModel() {
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