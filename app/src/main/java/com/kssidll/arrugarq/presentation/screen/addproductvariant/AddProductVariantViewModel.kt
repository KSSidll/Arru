package com.kssidll.arrugarq.presentation.screen.addproductvariant

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProductVariantViewModel @Inject constructor(
    productVariantRepository: IProductVariantRepository,
): ViewModel() {
    private val productVariantRepository: IProductVariantRepository

    init {
        this.productVariantRepository = productVariantRepository
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addVariant(variantData: AddProductVariantData) = viewModelScope.launch {
        productVariantRepository.insert(
            ProductVariant(
                productId = variantData.productId,
                name = variantData.name.trim(),
            )
        )
    }
}