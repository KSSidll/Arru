package com.kssidll.arrugarq.ui.screen.display.producer


import androidx.compose.runtime.*
import androidx.lifecycle.*
import androidx.paging.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.data.repository.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.domain.data.*
import com.patrykandpatrick.vico.core.entry.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class ProducerViewModel @Inject constructor(
    private val producerRepository: ProducerRepositorySource,
): ViewModel() {
    private val mProducer: MutableState<ProductProducer?> = mutableStateOf(null)
    val producer: ProductProducer? by mProducer

    private var mProducerListener: Job? = null

    val chartEntryModelProducer: ChartEntryModelProducer = ChartEntryModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler? = null
    val spentByTimePeriod: TimePeriodFlowHandler.Periods? get() = mTimePeriodFlowHandler?.currentPeriod
    val spentByTimeData: Flow<List<ChartSource>>? get() = mTimePeriodFlowHandler?.spentByTimeData

    fun producerTotalSpent(): Flow<Float>? {
        if (producer == null) return null

        return producerRepository.totalSpentFlow(producer!!)
            .map {
                it.toFloat()
                    .div(Item.PRICE_DIVISOR * Item.QUANTITY_DIVISOR)
            }
    }

    /**
     * @return paging data of full item for current producer as flow
     */
    fun transactions(): Flow<PagingData<FullItem>> {
        if (producer == null) return emptyFlow()
        return producerRepository.fullItemsPagedFlow(producer!!)
    }

    /**
     * Switches the state period to [newPeriod]
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: TimePeriodFlowHandler.Periods) {
        mTimePeriodFlowHandler?.switchPeriod(newPeriod)
    }

    /**
     * @return true if provided [producerId] was valid, false otherwise
     */
    suspend fun performDataUpdate(producerId: Long) = viewModelScope.async {
        val producer = producerRepository.get(producerId) ?: return@async false

        // We ignore the possiblity of changing category while one is already loaded
        // as not doing that would increase complexity too much
        // and if it happens somehow, it would be considered a bug
        if (mProducer.value != null || producerId == mProducer.value?.id) return@async true

        mProducerListener?.cancel()
        mProducerListener = viewModelScope.launch {
            producerRepository.getFlow(producerId)
                .collectLatest {
                    mProducer.value = it
                }
        }

        mProducer.value = producer

        mTimePeriodFlowHandler = TimePeriodFlowHandler(
            scope = viewModelScope,
            dayFlow = {
                producerRepository.totalSpentByDayFlow(producer)
            },
            weekFlow = {
                producerRepository.totalSpentByWeekFlow(producer)
            },
            monthFlow = {
                producerRepository.totalSpentByMonthFlow(producer)
            },
            yearFlow = {
                producerRepository.totalSpentByYearFlow(producer)
            },
        )

        return@async true
    }
        .await()
}
