package com.kssidll.arru.ui.screen.display.productproducer

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.repository.ProductProducerRepositorySource
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.TimePeriodFlowHandler
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.usecase.data.GetItemsForProductProducerUseCase
import com.kssidll.arru.ui.component.SpendingSummaryPeriod
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class DisplayProductProducerViewModel
@Inject
constructor(
    private val producerRepository: ProductProducerRepositorySource,
    private val getItemsForProductProducerUseCase: GetItemsForProductProducerUseCase,
) : ViewModel() {
    private val mProducer: MutableState<ProductProducerEntity?> = mutableStateOf(null)
    val producer: ProductProducerEntity? by mProducer

    private var mProducerListener: Job? = null

    val chartEntryModelProducer: CartesianChartModelProducer = CartesianChartModelProducer()

    private var mTimePeriodFlowHandler: TimePeriodFlowHandler<ImmutableList<ChartSource>>? = null
    val spentByTimePeriod: SpendingSummaryPeriod?
        get() =
            mTimePeriodFlowHandler?.currentPeriod?.let { SpendingSummaryPeriod.valueOf(it.name) }

    val spentByTimeData: Flow<ImmutableList<ChartSource>>?
        get() = mTimePeriodFlowHandler?.spentByTimeData

    fun producerTotalSpent(): Flow<Float?>? {
        return producer?.let { producerRepository.totalSpent(it.id) }
    }

    /** @return paging data of full item for current producer as flow */
    fun transactions(): Flow<PagingData<Item>> {
        return producer?.let { getItemsForProductProducerUseCase(it.id) } ?: emptyFlow()
    }

    /**
     * Switches the state period to [newPeriod]
     *
     * @param newPeriod Period to switch the state to
     */
    fun switchPeriod(newPeriod: SpendingSummaryPeriod) {
        val nPeriod = TimePeriodFlowHandler.Periods.valueOf(newPeriod.name)
        mTimePeriodFlowHandler?.switchPeriod(nPeriod)
    }

    /** @return true if provided [producerId] was valid, false otherwise */
    suspend fun performDataUpdate(producerId: Long) =
        viewModelScope
            .async {
                val producer = producerRepository.get(producerId).first() ?: return@async false

                // We ignore the possiblity of changing category while one is already loaded
                // as not doing that would increase complexity too much
                // and if it happens somehow, it would be considered a bug
                if (mProducer.value != null || producerId == mProducer.value?.id) return@async true

                mProducerListener?.cancel()
                mProducerListener =
                    viewModelScope.launch {
                        producerRepository.get(producerId).collectLatest { mProducer.value = it }
                    }

                mProducer.value = producer

                mTimePeriodFlowHandler =
                    TimePeriodFlowHandler(
                        scope = viewModelScope,
                        day = { producerRepository.totalSpentByDay(producerId) },
                        week = { producerRepository.totalSpentByWeek(producerId) },
                        month = { producerRepository.totalSpentByMonth(producerId) },
                        year = { producerRepository.totalSpentByYear(producerId) },
                    )

                return@async true
            }
            .await()
}
