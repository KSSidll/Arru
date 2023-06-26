package com.kssidll.arrugarq.ui.addproductproducer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kssidll.arrugarq.data.data.ProductProducer
import com.kssidll.arrugarq.data.repository.IProductProducerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddProductProducerViewModel @Inject constructor(
    productProducerRepository: IProductProducerRepository,
) : ViewModel() {
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