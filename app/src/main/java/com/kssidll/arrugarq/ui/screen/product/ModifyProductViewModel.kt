package com.kssidll.arrugarq.ui.screen.product

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import kotlinx.coroutines.*

/**
 * Base [ViewModel] class for Product modification view models
 * @property initialize Initializes start state, should be called as child in init of inheriting view model
 * @property screenState A [ModifyProductScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Product matching provided id, only changes representation data and loading state
 */
abstract class ModifyProductViewModel: ViewModel() {
    protected abstract val productRepository: IProductRepository
    protected abstract val producerRepository: IProducerRepository
    protected abstract val categoryRepository: ICategoryRepository

    internal val screenState: ModifyProductScreenState = ModifyProductScreenState()

    /**
     * Initializes start state, should be called as child in init of inheriting view model
     */
    protected fun initialize() {
        fillStateCategoriesWithAltNames()
        fillStateProducers()
    }

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

    private var fillStateCategoriesWithAltNamesJob: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateCategoriesWithAltNames() {
        fillStateCategoriesWithAltNamesJob?.cancel()
        fillStateCategoriesWithAltNamesJob = viewModelScope.launch {
            screenState.categoriesWithAltNames.value =
                categoryRepository.getAllWithAltNamesFlow()
        }
    }

    private var fillStateProducers: Job? = null

    /**
     * Clears and then fetches new data to screen state
     */
    private fun fillStateProducers() {
        fillStateProducers?.cancel()
        fillStateProducers = viewModelScope.launch {
            screenState.producers.value = producerRepository.getAllFlow()
        }
    }
}
