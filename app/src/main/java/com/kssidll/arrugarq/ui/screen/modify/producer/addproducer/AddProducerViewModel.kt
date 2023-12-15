package com.kssidll.arrugarq.ui.screen.modify.producer.addproducer

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.producer.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProducerViewModel @Inject constructor(
    override val producerRepository: ProducerRepositorySource,
): ModifyProducerViewModel() {

    /**
     * Tries to add a product variant to the repository
     * @return Id of newly inserted row, null if operation failed
     */
    suspend fun addProducer(): Long? = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        screenState.validate()

        val producer = screenState.extractDataOrNull() ?: return@async null
        val other = producerRepository.getByName(producer.name)

        if (other != null) {
            screenState.name.apply { value = value.toError(FieldError.DuplicateValueError) }

            return@async null
        } else {
            return@async producerRepository.insert(producer)
        }
    }
        .await()
}