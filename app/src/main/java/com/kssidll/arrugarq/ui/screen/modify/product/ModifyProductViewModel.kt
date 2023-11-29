package com.kssidll.arrugarq.ui.screen.modify.product

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Base [ViewModel] class for Product modification view models
 * @property screenState A [ModifyProductScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Product matching provided id, only changes representation data and loading state
 */
abstract class ModifyProductViewModel: ViewModel() {
    protected abstract val productRepository: ProductRepositorySource
    protected abstract val producerRepository: ProducerRepositorySource
    protected abstract val categoryRepository: CategoryRepositorySource

    internal val screenState: ModifyProductScreenState = ModifyProductScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [productId] was valid, false otherwise
     */
    suspend fun updateState(productId: Long) = viewModelScope.async {
        screenState.loadingName.value = true
        screenState.loadingProductProducer.value = true
        screenState.loadingProductCategory.value = true

        val dispose = {
            screenState.loadingName.value = false
            screenState.loadingProductProducer.value = false
            screenState.loadingProductCategory.value = false
        }

        val product = productRepository.get(productId)
        if (product == null) {
            dispose()
            return@async false
        }

        val producer =
            if (product.producerId != null) {
                producerRepository.get(product.producerId)
            } else {
                null
            }

        val category = categoryRepository.get(product.categoryId)
        if (category == null) {
            dispose()
            return@async false
        }


        screenState.name.value = product.name
        screenState.selectedProductProducer.value = producer
        screenState.selectedProductCategory.value = category

        dispose()
        return@async true
    }
        .await()

    /**
     * @return List of all categories
     */
    fun allCategories(): Flow<List<ProductCategoryWithAltNames>> {
        return categoryRepository.getAllWithAltNamesFlow()
    }

    /**
     * @return List of all producers
     */
    fun allProducers(): Flow<List<ProductProducer>> {
        return producerRepository.getAllFlow()
    }
}
