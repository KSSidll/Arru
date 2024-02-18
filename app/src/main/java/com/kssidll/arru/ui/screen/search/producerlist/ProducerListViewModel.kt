package com.kssidll.arru.ui.screen.search.producerlist


import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.ui.screen.search.shared.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class ProducerListViewModel @Inject constructor(
    private val producerRepository: ProducerRepositorySource,
): ViewModel() {
    internal val screenState: ListScreenState<ProductProducer> = ListScreenState()

    init {
        fillStateItems()
    }

    /**
     * Fetches new data to screen state
     */
    private fun fillStateItems() {
        screenState.items.value = producerRepository.allFlow()
    }
}