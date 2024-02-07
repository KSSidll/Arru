package com.kssidll.arrugarq.ui.screen.modify.category

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * Base [ViewModel] class for Category modification view models
 * @property screenState A [ModifyCategoryScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Category matching provided id, only changes representation data and loading state
 */
abstract class ModifyCategoryViewModel: ViewModel() {
    protected abstract val categoryRepository: CategoryRepositorySource
    protected var mCategory: ProductCategory? = null
    internal val screenState: ModifyCategoryScreenState = ModifyCategoryScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [categoryId] was valid, false otherwise
     */
    open suspend fun updateState(categoryId: Long) = viewModelScope.async {
        screenState.name.value = screenState.name.value.toLoading()

        mCategory = categoryRepository.get(categoryId)

        screenState.name.apply {
            value = mCategory?.let { Field.Loaded(it.name) } ?: value.toLoadedOrError()
        }

        return@async mCategory != null
    }
        .await()

    /**
     * @return list of merge candidates as flow
     */
    fun allMergeCandidates(categoryId: Long): Flow<List<ProductCategory>> {
        return categoryRepository.allFlow()
            .onEach { it.filter { item -> item.id != categoryId } }
    }
}

/**
 * Data representing [ModifyCategoryScreenImpl] screen state
 */
data class ModifyCategoryScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded(String())),
): ModifyScreenState<ProductCategory>()