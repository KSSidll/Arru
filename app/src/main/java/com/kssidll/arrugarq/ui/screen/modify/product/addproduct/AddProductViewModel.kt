package com.kssidll.arrugarq.ui.screen.modify.product.addproduct

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
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
     * @return resulting [InsertResult]
     */
    suspend fun addProduct(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        return@async 1L
        // TODO add use case
    }
        .await()
}