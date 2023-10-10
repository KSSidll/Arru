package com.kssidll.arrugarq.ui.screen.addproduct

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

internal data class AddProductScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val selectedProductCategory: MutableState<ProductCategory?> = mutableStateOf(null),
    val selectedProductCategoryError: MutableState<Boolean> = mutableStateOf(false),

    val selectedProductProducer: MutableState<ProductProducer?> = mutableStateOf(null),

    val name: MutableState<String> = mutableStateOf(String()),
    val nameError: MutableState<Boolean> = mutableStateOf(false),

    val isCategorySearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
    val isProducerSearchDialogExpanded: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Validates selectedProductCategory field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddProductScreenState.validateSelectedProductCategory(): Boolean {
    return !(selectedProductCategory.value == null).also { selectedProductCategoryError.value = it }
}

/**
 * Validates name field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddProductScreenState.validateName(): Boolean {
    return !(name.value.isBlank()).also { nameError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
internal fun AddProductScreenState.validate(): Boolean {
    val category = validateSelectedProductCategory()
    val name = validateName()

    return category && name
}

/**
 * performs data validation and tries to extract embedded data
 * @return Null if validation sets error flags, extracted data otherwise
 */
internal fun AddProductScreenState.extractProductOrNull(): Product? {
    if (!validate()) return null

    return Product(
        categoryId = selectedProductCategory.value!!.id,
        producerId = selectedProductProducer.value?.id,
        name = name.value.trim(),
    )
}

@HiltViewModel
class AddProductViewModel @Inject constructor(
    productRepository: IProductRepository,
    productCategoryRepository: IProductCategoryRepository,
    productProducerRepository: IProductProducerRepository,
): ViewModel() {
    internal val addProductScreenState: AddProductScreenState = AddProductScreenState()

    private val productRepository: IProductRepository
    private val productCategoryRepository: IProductCategoryRepository
    private val productProducerRepository: IProductProducerRepository

    init {
        this.productRepository = productRepository
        this.productCategoryRepository = productCategoryRepository
        this.productProducerRepository = productProducerRepository
    }

    /**
     * Tries to add a product to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addProduct(): Long? = viewModelScope.async {
        addProductScreenState.attemptedToSubmit.value = true
        val product = addProductScreenState.extractProductOrNull() ?: return@async null

        return@async productRepository.insert(product)
    }
        .await()

    fun getProductCategoriesWithAltNamesFlow(): Flow<List<ProductCategoryWithAltNames>> {
        return productCategoryRepository.getAllWithAltNamesFlow()
    }

    fun getProductProducersFlow(): Flow<List<ProductProducer>> {
        return productProducerRepository.getAllFlow()
    }
}