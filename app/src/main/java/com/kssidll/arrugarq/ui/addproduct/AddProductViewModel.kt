package com.kssidll.arrugarq.ui.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.Product
import com.kssidll.arrugarq.data.repository.IProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductViewModel @Inject constructor(
    productRepository: IProductRepository,
) : ViewModel() {
    private val productRepository: IProductRepository

    init {
        this.productRepository = productRepository
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addProduct(productData: AddProductData) = viewModelScope.launch {
        productRepository.insert(
            Product(
                categoryId = productData.categoryId,
                name = productData.name,
            )
        )
    }
}