package com.kssidll.arru.ui.screen.modify.product

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Base [ViewModel] class for Product modification view models
 * @property screenState A [ModifyProductScreenState] instance to use as screen state representation
 */
abstract class ModifyProductViewModel: ViewModel() {
    protected abstract val productRepository: ProductRepositorySource
    protected abstract val producerRepository: ProducerRepositorySource
    protected abstract val categoryRepository: CategoryRepositorySource
    internal val screenState: ModifyProductScreenState = ModifyProductScreenState()

    private var mProducerListener: Job? = null
    private var mCategoryListener: Job? = null

    suspend fun setSelectedProducer(providedProducerId: Long?) {
        if (providedProducerId != null) {
            screenState.selectedProductProducer.apply { value = value.toLoading() }
            onNewProducerSelected(producerRepository.get(providedProducerId))
        }
    }

    suspend fun setSelectedCategory(providedCategoryId: Long?) {
        if (providedCategoryId != null) {
            screenState.selectedProductCategory.apply { value = value.toLoading() }
            onNewCategorySelected(categoryRepository.get(providedCategoryId))
        }
    }

    fun onNewProducerSelected(producer: ProductProducer?) {
        // Don't do anything if the producer is the same as already selected
        if (screenState.selectedProductProducer.value.data == producer) {
            screenState.selectedProductProducer.apply { value = value.toLoaded() }
            return
        }

        screenState.selectedProductProducer.value = Field.Loaded(producer)

        mProducerListener?.cancel()
        if (producer != null) {
            mProducerListener = viewModelScope.launch {
                producerRepository.getFlow(producer.id)
                    .collectLatest {
                        if (it is Data.Loaded) {
                            screenState.selectedProductProducer.value = Field.Loaded(it.data)
                        }
                    }
            }
        }
    }

    fun onNewCategorySelected(category: ProductCategory?) {
        // Don't do anything if the producer is the same as already selected
        if (screenState.selectedProductCategory.value.data == category) {
            screenState.selectedProductCategory.apply { value = value.toLoaded() }
            return
        }

        screenState.selectedProductCategory.value = Field.Loaded(category)

        mCategoryListener?.cancel()
        if (category != null) {
            mCategoryListener = viewModelScope.launch {
                categoryRepository.getFlow(category.id)
                    .collectLatest {
                        if (it is Data.Loaded) {
                            screenState.selectedProductCategory.value = Field.Loaded(it.data)
                        }
                    }
            }
        }
    }

    /**
     * @return List of all categories
     */
    fun allCategories(): Flow<Data<List<ProductCategoryWithAltNames>>> {
        return categoryRepository.allWithAltNamesFlow()
    }

    /**
     * @return List of all producers
     */
    fun allProducers(): Flow<Data<List<ProductProducer>>> {
        return producerRepository.allFlow()
    }
}

/**
 * Data representing [ModifyProductScreenImpl] screen state
 */
data class ModifyProductScreenState(
    val selectedProductCategory: MutableState<Field<ProductCategory>> = mutableStateOf(Field.Loaded()),
    val selectedProductProducer: MutableState<Field<ProductProducer?>> = mutableStateOf(Field.Loaded()),
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded()),

    val isCategorySearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    val isProducerSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
): ModifyScreenState()