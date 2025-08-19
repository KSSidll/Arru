package com.kssidll.arru.ui.screen.modify.productproducer.addproductproducer

import androidx.lifecycle.viewModelScope
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.data.repository.ProductProducerRepositorySource.Companion.InsertResult
import com.kssidll.arru.domain.data.FieldError
import com.kssidll.arru.ui.screen.modify.productproducer.ModifyProductProducerViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async

// TODO refactor uiState Event UseCase

@HiltViewModel
class AddProductProducerViewModel
@Inject
constructor(override val producerRepository: ProductProducerRepositorySource) :
    ModifyProductProducerViewModel() {

    /**
     * Tries to add a product producer to the repository
     *
     * @return resulting [InsertResult]
     */
    suspend fun addProducer() =
        viewModelScope
            .async {
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
