package com.kssidll.arrugarq.ui.screen.addproductproducer

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.shared.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProductProducerViewModel @Inject constructor(
    private val productProducerRepository: IProductProducerRepository,
): ViewModel() {
    internal val screenState: EditProductProducerScreenState = EditProductProducerScreenState()

    /**
     * Tries to add a product variant to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addProducer(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        val producer = screenState.extractProducerOrNull() ?: return@async null

        return@async productProducerRepository.insert(producer)
    }
        .await()
}