package com.kssidll.arrugarq.ui.screen.modify.producer

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.*
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
        screenState.name.apply { value = value.toLoading() }

        val producer: ProductProducer? = producerRepository.get(producerId)

        screenState.name.apply {
            value = producer?.name.let { Field.Loaded(it) } ?: value.toLoadedOrError()
        }

        return@async true
    }
        .await()
}

/**
 * Data representing [ModifyProducerScreenImpl] screen state
 */
data class ModifyProducerScreenState(
    val name: MutableState<Field<String>> = mutableStateOf(Field.Loaded())
): ModifyScreenState<ProductProducer>() {

    /**
     * Validates name field and updates its error flag
     * @return true if field is of correct value, false otherwise
     */
    fun validateName(): Boolean {
        name.apply {
            if (value.data.isNullOrBlank()) {
                value = value.toError(FieldError.NoValueError)
            }

            return value.isNotError()
        }
    }

    override fun validate(): Boolean {
        return validateName()
    }

    override fun extractDataOrNull(id: Long): ProductProducer? {
        if (!validate()) return null

        return ProductProducer(
            id = id,
            name = name.value.data?.trim() ?: return null,
        )
    }

}