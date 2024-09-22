package com.kssidll.arru.ui.screen.modify.producer.addproducer

import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.repository.ProducerRepositorySource
import com.kssidll.arru.data.repository.ProducerRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.producer.ModifyProducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

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