package com.kssidll.arrugarq.ui.screen.addproductcategory

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

internal data class AddProductCategoryScreenState(
    val attemptedToSubmit: MutableState<Boolean> = mutableStateOf(false),

    val name: MutableState<String> = mutableStateOf(String()),
    val nameError: MutableState<Boolean> = mutableStateOf(false),
)

/**
 * Validates name field and updates its error flag
 * @return true if field is of correct value, false otherwise
 */
internal fun AddProductCategoryScreenState.validateName(): Boolean {
    return name.value.isBlank()
        .also { nameError.value = it }
}

/**
 * Validates state fields and updates state flags
 * @return true if all fields are of correct value, false otherwise
 */
internal fun AddProductCategoryScreenState.validate(): Boolean {
    return validateName()
}

/**
 * performs data validation and tries to extract embedded data
 * @return Null if validation sets error flags, extracted data otherwise
 */
internal fun AddProductCategoryScreenState.extractCategoryOrNull(): ProductCategory? {
    if (!validate()) return null

    return ProductCategory(
        name = name.value.trim(),
    )
}

@HiltViewModel
class AddProductCategoryViewModel @Inject constructor(
    categoryRepository: IProductCategoryRepository,
): ViewModel() {
    internal val addProductCategoryScreenState: AddProductCategoryScreenState =
        AddProductCategoryScreenState()

    private val categoryRepository: IProductCategoryRepository

    init {
        this.categoryRepository = categoryRepository
    }

    /**
     * Tries to add a product category to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addCategory(): Long? = viewModelScope.async {
        addProductCategoryScreenState.attemptedToSubmit.value = true
        val category = addProductCategoryScreenState.extractCategoryOrNull() ?: return@async null

        return@async categoryRepository.insert(category)
    }
        .await()
}