package com.kssidll.arrugarq.ui.screen.modify.product

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
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

    suspend fun setSelectedProducer(providedProducerId: Long?) {
        if (providedProducerId != null) {
            screenState.selectedProductProducer.apply { value = value.toLoading() }
            screenState.selectedProductProducer.apply {
                value = Field.Loaded(producerRepository.get(providedProducerId))
            }
        }
    }

    suspend fun setSelectedCategory(providedCategoryId: Long?) {
        if (providedCategoryId != null) {
            screenState.selectedProductCategory.apply { value = value.toLoading() }
            screenState.selectedProductCategory.apply {
                value = Field.Loaded(categoryRepository.get(providedCategoryId))
            }
        }
    }

    /**
     * @return List of all categories
     */
    fun allCategories(): Flow<List<ProductCategoryWithAltNames>> {
        return categoryRepository.allWithAltNamesFlow()
    }

    /**
     * @return List of all producers
     */
    fun allProducers(): Flow<List<ProductProducer>> {
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