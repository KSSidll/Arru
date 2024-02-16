package com.kssidll.arrugarq.ui.screen.modify.product

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
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
    protected var mProduct: Product? = null
    internal val screenState: ModifyProductScreenState = ModifyProductScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [productId] was valid, false otherwise
     */
    open suspend fun updateState(productId: Long) = viewModelScope.async {
        screenState.name.apply { value = value.toLoading() }
        screenState.selectedProductProducer.apply { value = value.toLoading() }
        screenState.selectedProductCategory.apply { value = value.toLoading() }

        mProduct = productRepository.get(productId)
        val producer: ProductProducer? = mProduct?.producerId?.let { producerRepository.get(it) }
        val category = mProduct?.categoryId?.let { categoryRepository.get(it) }

        screenState.name.apply {
            value = mProduct?.name?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        screenState.selectedProductProducer.apply {
            value = producer?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        screenState.selectedProductCategory.apply {
            value = category?.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        return@async mProduct != null
    }
        .await()

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

    /**
     * @return list of merge candidates as flow
     */
    fun allMergeCandidates(productId: Long): Flow<List<Product>> {
        return productRepository.allFlow()
            .onEach { it.filter { item -> item.id != productId } }
            .distinctUntilChanged()
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