package com.kssidll.arrugarq.ui.screen.modify.producer.editproducer


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
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
    private val mMergeMessageProducerName: MutableState<String> = mutableStateOf(String())
    val mergeMessageProducerName get() = mMergeMessageProducerName.value

    val chosenMergeCandidate: MutableState<ProductProducer?> = mutableStateOf(null)
    val showMergeConfirmDialog: MutableState<Boolean> = mutableStateOf(false)

    override suspend fun updateState(producerId: Long): Boolean {
        return super.updateState(producerId)
            .also {
                mMergeMessageProducerName.value = mProducer?.name.orEmpty()
            }
    }

    /**
     * Tries to update product with provided [producerId] with current screen state data
     * @return Whether the update was successful
     */
    suspend fun updateProducer(producerId: Long) = viewModelScope.async {
        //        screenState.attemptedToSubmit.value = true
        //        screenState.validate()
        //
        //        val producer = screenState.extractDataOrNull(producerId) ?: return@async false
        //        val other = producerRepository.byName(producer.name)
        //
        //        if (other != null) {
        //            if (other.id == producerId) return@async true
        //
        //            screenState.name.apply {
        //                value = value.toError(FieldError.DuplicateValueError)
        //            }
        //
        //            chosenMergeCandidate.value = other
        //            showMergeConfirmDialog.value = true
        //
        //            return@async false
        //        } else {
        //            producerRepository.update(producer)
        //            return@async true
        //        }
        return@async true
        // TODO add use case
    }
        .await()

    /**
     * Tries to delete producer with provided [producerId], sets showDeleteWarning flag in state if operation would require deleting foreign constrained data,
     * state deleteWarningConfirmed flag needs to be set to start foreign constrained data deletion
     * @return True if operation started, false otherwise
     */
    suspend fun deleteProducer(producerId: Long) = viewModelScope.async {
        // return true if no such producer exists
        //        val producer = producerRepository.get(producerId) ?: return@async true
        //
        //        val products = productRepository.byProducer(producer)
        //
        //        val items = buildList {
        //            products.forEach {
        //                addAll(itemRepository.byProduct(it))
        //            }
        //        }.toList()
        //
        //        val variants = buildList {
        //            products.forEach {
        //                addAll(variantRepository.byProduct(it))
        //            }
        //        }.toList()
        //
        //        if ((products.isNotEmpty() || items.isNotEmpty() || variants.isNotEmpty()) && !screenState.deleteWarningConfirmed.value) {
        //            screenState.showDeleteWarning.value = true
        //            return@async false
        //        } else {
        //            itemRepository.delete(items)
        //            variantRepository.delete(variants)
        //            productRepository.delete(products)
        //            producerRepository.delete(producer)
        //            return@async true
        //        }
        return@async true
        // TODO add use case
    }
        .await()

    /**
     * Tries to delete merge category into provided [mergeCandidate]
     * @return True if operation succeded, false otherwise
     */
    suspend fun mergeWith(mergeCandidate: ProductProducer) = viewModelScope.async {
        //        val products = mProducer?.let { productRepository.byProducer(it) } ?: return@async false
        //
        //        if (products.isNotEmpty()) {
        //            products.forEach { it.producerId = mergeCandidate.id }
        //            productRepository.update(products)
        //        }
        //
        //        mProducer?.let { producerRepository.delete(it) }
        //
        //        return@async true
        return@async true
        // TODO add use case
    }
        .await()
}
