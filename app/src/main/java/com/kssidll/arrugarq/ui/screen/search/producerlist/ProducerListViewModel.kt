package com.kssidll.arrugarq.ui.screen.search.producerlist


import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import com.kssidll.arrugarq.ui.screen.search.shared.*
import dagger.hilt.android.lifecycle.*
import javax.inject.*

@HiltViewModel
class ProducerListViewModel @Inject constructor(
    private val producerRepository: IProducerRepository,
): ViewModel() {
    internal val screenState: ListScreenState<ProductProducer> = ListScreenState()

    init {
        fillStateItems()
    }

    /**
     * Fetches new data to screen state
     */
    private fun fillStateItems() {
        screenState.items.value = producerRepository.getAllFlow()
    }
}
