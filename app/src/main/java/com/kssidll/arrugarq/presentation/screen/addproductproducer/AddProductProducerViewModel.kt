package com.kssidll.arrugarq.presentation.screen.addproductproducer

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import javax.inject.*

@HiltViewModel
class AddProductProducerViewModel @Inject constructor(
    productProducerRepository: IProductProducerRepository,
): ViewModel() {
    private val productProducerRepository: IProductProducerRepository

    init {
        this.productProducerRepository = productProducerRepository
    }

    /**
     * Doesn't ensure validity of non optional values as they should be validated on Screen level
     * to allow for UI changes depending on data validity
     */
    fun addProducer(producerData: AddProductProducerData) = viewModelScope.launch {
        productProducerRepository.insert(
            ProductProducer(
                name = producerData.name.trim(),
            )
        )
    }
}