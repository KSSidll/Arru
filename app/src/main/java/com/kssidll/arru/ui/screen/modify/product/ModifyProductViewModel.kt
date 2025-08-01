package com.kssidll.arru.ui.screen.modify.product

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.data.ProductCategory
import com.kssidll.arru.data.data.ProductCategoryWithAltNames
import com.kssidll.arru.data.data.ProductProducer
import com.kssidll.arru.data.repository.CategoryRepositorySource
import com.kssidll.arru.data.repository.ProducerRepositorySource
import com.kssidll.arru.data.repository.ProductRepositorySource
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.domain.data.Field
import com.kssidll.arru.ui.screen.modify.ModifyScreenState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
    fun allCategories(): Flow<Data<ImmutableList<ProductCategoryWithAltNames>>> {
        return categoryRepository.allWithAltNamesFlow()
    }

    /**
     * @return List of all producers
     */
    fun allProducers(): Flow<Data<ImmutableList<ProductProducer>>> {
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