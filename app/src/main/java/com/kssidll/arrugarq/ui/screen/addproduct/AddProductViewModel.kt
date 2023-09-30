package com.kssidll.arrugarq.ui.screen.addproduct

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*
import kotlin.jvm.optionals.*

@HiltViewModel
class AddProductViewModel @Inject constructor(
    productRepository: IProductRepository,
    productCategoryRepository: IProductCategoryRepository,
    productProducerRepository: IProductProducerRepository,
): ViewModel() {
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