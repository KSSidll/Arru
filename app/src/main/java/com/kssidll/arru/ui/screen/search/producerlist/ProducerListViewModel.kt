package com.kssidll.arru.ui.screen.search.producerlist


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.repository.ProducerRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProducerListViewModel @Inject constructor(
    private val producerRepository: ProducerRepositorySource,
): ViewModel() {
    private val _filter = mutableStateOf(String())
    var filter by _filter

    fun items() = producerRepository.allFlow()
}
