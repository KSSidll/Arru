package com.kssidll.arrugarq.ui.screen.modify.producer.editproducer


import androidx.lifecycle.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.modify.producer.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class EditProducerViewModel @Inject constructor(
    override val producerRepository: IProducerRepository,
    private val productRepository: IProductRepository,
    private val variantRepository: IVariantRepository,
    private val itemRepository: IItemRepository,
): ModifyProducerViewModel() {

    /**
     * Tries to update product with provided [producerId] with current screen state data
     */
    fun updateProducer(producerId: Long) = viewModelScope.launch {
        screenState.attemptedToSubmit.value = true
        val producer = screenState.extractProducerOrNull(producerId) ?: return@launch

        producerRepository.update(producer)
    }

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
