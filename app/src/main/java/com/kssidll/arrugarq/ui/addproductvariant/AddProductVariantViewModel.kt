package com.kssidll.arrugarq.ui.addproductvariant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.ProductVariant
import com.kssidll.arrugarq.data.repository.IProductVariantRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductVariantViewModel @Inject constructor(
    productVariantRepository: IProductVariantRepository,
) : ViewModel() {
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