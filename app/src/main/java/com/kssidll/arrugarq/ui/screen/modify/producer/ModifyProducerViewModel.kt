package com.kssidll.arrugarq.ui.screen.modify.producer

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import kotlinx.coroutines.*

/**
 * Base [ViewModel] class for Producer modification view models
 * @property screenState A [ModifyProducerScreenState] instance to use as screen state representation
 * @property updateState Updates the screen state representation property values to represent the Producer matching provided id, only changes representation data and loading state
 */
abstract class ModifyProducerViewModel: ViewModel() {
    protected abstract val producerRepository: ProducerRepositorySource

    internal val screenState: ModifyProducerScreenState = ModifyProducerScreenState()

    /**
     * Updates data in the screen state
     * @return true if provided [producerId] was valid, false otherwise
     */
    suspend fun updateState(producerId: Long) = viewModelScope.async {
        screenState.loadingName.value = true

        val producer = producerRepository.get(producerId)
        if (producer == null) {
            screenState.loadingName.value = false
            return@async false
        }

        screenState.name.value = producer.name

        screenState.loadingName.value = false
        return@async true
    }
        .await()
}
