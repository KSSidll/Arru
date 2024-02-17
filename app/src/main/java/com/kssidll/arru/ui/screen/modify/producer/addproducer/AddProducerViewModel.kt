package com.kssidll.arru.ui.screen.modify.producer.addproducer

import androidx.lifecycle.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.data.repository.ProducerRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.modify.producer.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProducerViewModel @Inject constructor(
    override val producerRepository: ProducerRepositorySource,
): ModifyProducerViewModel() {

    /**
     * Tries to add a product producer to the repository
     * @return resulting [InsertResult]
     */
    suspend fun addProducer() = viewModelScope.async {
        screenState.attemptedToSubmit.value = true

        val result = producerRepository.insert(screenState.name.value.data.orEmpty())

        if (result.isError()) {
            when (result.error!!) {
                InsertResult.InvalidName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.InvalidValueError)
                    }
                }

                InsertResult.DuplicateName -> {
                    screenState.name.apply {
                        value = value.toError(FieldError.DuplicateValueError)
                    }
                }
            }
        }

        return@async result
    }
        .await()
}