package com.kssidll.arrugarq.ui.screen.product.addproduct

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.product.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProductViewModel @Inject constructor(
    private val productRepository: IProductRepository,
    private val categoryRepository: ICategoryRepository,
    private val producerRepository: IProducerRepository,
): ViewModel() {
    internal val screenState: ModifyProductScreenState = ModifyProductScreenState()

    init {
        fillStateCategoriesWithAltNames()
        fillStateProducers()
    }

    /**
     * Tries to add a product to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addProduct(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        val product = screenState.extractProductOrNull() ?: return@async null

        return@async productRepository.insert(product)
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