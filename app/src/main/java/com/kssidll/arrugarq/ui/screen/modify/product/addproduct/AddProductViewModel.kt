package com.kssidll.arrugarq.ui.screen.modify.product.addproduct

import android.database.sqlite.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.product.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProductViewModel @Inject constructor(
    override val productRepository: ProductRepositorySource,
    override val categoryRepository: CategoryRepositorySource,
    override val producerRepository: ProducerRepositorySource,
): ModifyProductViewModel() {

    /**
     * Tries to add a product to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addProduct(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        screenState.validate()

        val product = screenState.extractDataOrNull() ?: return@async null

        try {
            return@async productRepository.insert(product)
        } catch (_: SQLiteConstraintException) {
            screenState.name.apply { value = value.toError(FieldError.DuplicateValueError) }
            return@async null
        }
    }
        .await()
}