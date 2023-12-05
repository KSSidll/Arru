package com.kssidll.arrugarq.ui.screen.modify.producer.editproducer


import android.database.sqlite.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.screen.modify.producer.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditProducerViewModel @Inject constructor(
    override val producerRepository: ProducerRepositorySource,
    private val productRepository: ProductRepositorySource,
    private val variantRepository: VariantRepositorySource,
    private val itemRepository: ItemRepositorySource,
): ModifyProducerViewModel() {

    /**
     * Tries to update product with provided [producerId] with current screen state data
     * @return Whether the update was successful
     */
    suspend fun updateProducer(producerId: Long) = viewModelScope.async {
        screenState.attemptedToSubmit.value = true
        screenState.validate()

        val producer = screenState.extractDataOrNull(producerId) ?: return@async false

        try {
            producerRepository.update(producer)
        } catch (_: SQLiteConstraintException) {
            screenState.name.apply { value = value.toError(FieldError.DuplicateValueError) }
            return@async false
        }

        return@async true
    }
        .await()

    /**
     * Tries to delete producer with provided [producerId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return True if operation started, false otherwise
     */
    suspend fun deleteProducer(producerId: Long) = viewModelScope.async {
        // return true if no such producer exists
        val producer = producerRepository.get(producerId) ?: return@async true

        val products = productRepository.getByProducerId(producerId)

        val items = buildList {
            products.forEach {
                addAll(itemRepository.getByProductId(it.id))
            }
        }.toList()

        val variants = buildList {
            products.forEach {
                addAll(variantRepository.getByProductId(it.id))
            }
        }.toList()

        if ((products.isNotEmpty() || items.isNotEmpty() || variants.isNotEmpty()) && !screenState.deleteWarningConfirmed.value) {
            screenState.showDeleteWarning.value = true
            return@async false
        } else {
            itemRepository.delete(items)
            variantRepository.delete(variants)
            productRepository.delete(products)
            producerRepository.delete(producer)
            return@async true
        }
    }
        .await()
}
