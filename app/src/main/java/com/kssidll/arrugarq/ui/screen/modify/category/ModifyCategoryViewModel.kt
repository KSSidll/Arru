package com.kssidll.arrugarq.ui.screen.modify.category

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
import kotlinx.coroutines.*

/**
 * Base [ViewModel] class for Category modification view models
 * @property screenState A [ModifyCategoryScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Category matching provided id, only changes representation data and loading state
 */
abstract class ModifyCategoryViewModel: ViewModel() {
    protected abstract val categoryRepository: CategoryRepositorySource
    internal val screenState: ModifyCategoryScreenState = ModifyCategoryScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [categoryId] was valid, false otherwise
     */
    suspend fun updateState(categoryId: Long) = viewModelScope.async {
        screenState.name.value = screenState.name.value.toLoading()

        val category = categoryRepository.get(categoryId)

        screenState.name.apply {
            value = category?.let { Field.Loaded(it.name) } ?: value.toLoadedOrError()
        }

        return@async category != null
    }
        .await()
}

/**
 * Data representing [ModifyCategoryScreenImpl] screen state
 */
data class ModifyCategoryScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded(String())),
): ModifyScreenState<ProductCategory>() {

    /**
     * Validates name field and sets it to error state if value is not valid
     * @return true if field is of correct value, false otherwise
     */
    fun validateName(): Boolean {
        name.apply {
            if (value.data.isNullOrBlank()) {
                value = value.toError(FieldError.NoValueError)
            }

            return value.isNotError()
        }
    }

    override fun validate(): Boolean {
        return validateName()
    }

    override fun extractDataOrNull(id: Long): ProductCategory? {
        if (!validate()) return null

        return ProductCategory(
            id = id,
            name = name.value.data?.trim() ?: return null,
        )
    }
}