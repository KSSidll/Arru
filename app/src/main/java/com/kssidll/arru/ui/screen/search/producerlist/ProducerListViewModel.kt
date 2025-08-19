package com.kssidll.arru.ui.screen.search.producerlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

// TODO refactor uiState Event UseCase

@HiltViewModel
class ProducerListViewModel
@Inject
constructor(private val producerRepository: ProductProducerRepositorySource) : ViewModel() {
    private val _filter = mutableStateOf(String())
    var filter by _filter

    fun items() = producerRepository.all()
}
