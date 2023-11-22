package com.kssidll.arrugarq.ui.screen.producer.addproducer

import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.producer.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProducerViewModel @Inject constructor(
    override val producerRepository: IProducerRepository,
): ModifyProducerViewModel() {

    /**
     * Tries to add a product variant to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addProducer(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        val producer = screenState.extractProducerOrNull() ?: return@async null

        return@async producerRepository.insert(producer)
    }
        .await()
}