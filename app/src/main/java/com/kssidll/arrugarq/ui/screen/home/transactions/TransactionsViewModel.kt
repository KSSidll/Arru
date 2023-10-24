package com.kssidll.arrugarq.ui.screen.home.transactions


import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.*

internal const val fullItemFetchCount = 8
internal const val fullItemMaxPrefetchCount = 50

@HiltViewModel
class TransactionsViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
): ViewModel() {
    private var fullItemsDataQuery: Job = Job()
    var fullItemsData: SnapshotStateList<FullItem> = mutableStateListOf()
    private val newFullItemFlow: Flow<Item> = itemRepository.getLastFlow()
    private var fullItemOffset: Int = 0

    init {
        viewModelScope.launch {
            newFullItemFlow.collect {
                fullItemOffset = 0
                fullItemsDataQuery.cancel()
                fullItemsData.clear()
                fullItemsDataQuery = performFullItemsQuery()
                fullItemOffset += fullItemFetchCount
            }
        }
    }

    fun queryMoreFullItems() {
        if (fullItemsDataQuery.isCompleted) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        fullItemsData.addAll(
            itemRepository.getFullItems(
                offset = queryOffset,
                count = fullItemFetchCount
            )
        )
    }
}
