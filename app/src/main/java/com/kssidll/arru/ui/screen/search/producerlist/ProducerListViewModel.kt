package com.kssidll.arru.ui.screen.search.producerlist


import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.kssidll.arru.data.repository.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class ProducerListViewModel @Inject constructor(
    private val producerRepository: ProducerRepositorySource,
): ViewModel() {
    private val _filter = mutableStateOf(String())
    var filter by _filter

    fun items() = producerRepository.allFlow()
}
