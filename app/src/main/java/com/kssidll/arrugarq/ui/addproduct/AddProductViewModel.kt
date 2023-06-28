package com.kssidll.arrugarq.ui.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.Product
import com.kssidll.arrugarq.data.data.ProductCategoryWithAltNames
import com.kssidll.arrugarq.data.data.ProductProducer
import com.kssidll.arrugarq.data.repository.IProductCategoryRepository
import com.kssidll.arrugarq.data.repository.IProductProducerRepository
import com.kssidll.arrugarq.data.repository.IProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull

@HiltViewModel
class AddProductViewModel @Inject constructor(
    productRepository: IProductRepository,
    productCategoryRepository: IProductCategoryRepository,
    productProducerRepository: IProductProducerRepository,
) : ViewModel() {
    private val productRepository: IProductRepository
    private val productCategoryRepository: IProductCategoryRepository
    private val productProducerRepository: IProductProducerRepository

    var addProductState: AddProductState = AddProductState()

    init {
        this.productRepository = productRepository
        this.productCategoryRepository = productCategoryRepository
        this.productProducerRepository = productProducerRepository
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addProduct(productData: AddProductData) = viewModelScope.launch {
        productRepository.insert(
            Product(
                categoryId = productData.categoryId,
                producerId = productData.producerId.getOrNull(),
                name = productData.name.trim(),
            )
        )
    }

    fun getProductCategoriesWithAltNamesFlow(): Flow<List<ProductCategoryWithAltNames>> {
        return productCategoryRepository.getAllWithAltNamesFlow()
    }

    fun getProductProducersFlow(): Flow<List<ProductProducer>> {
        return productProducerRepository.getAllFlow()
    }
}