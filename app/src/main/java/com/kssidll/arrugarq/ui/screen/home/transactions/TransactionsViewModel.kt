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

/**
 * Page fetch size
 */
internal const val fullItemFetchCount = 8

/**
 * Maximum prefetched items
 */
internal const val fullItemMaxPrefetchCount = fullItemFetchCount * 6

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

    /**
     * Requests a query of [fullItemFetchCount] items to be appended to transactions list
     */
    fun queryMoreFullItems() {
        if (fullItemsDataQuery.isCompleted) {
            fullItemsDataQuery = performFullItemsQuery(fullItemOffset)
            fullItemOffset += fullItemFetchCount
        }
    }

    /**
     * Performs a query of [fullItemFetchCount] items, with [queryOffset] offset, and appends the result to the transactions list
     */
    private fun performFullItemsQuery(queryOffset: Int = 0) = viewModelScope.launch {
        fullItemsData.addAll(
            itemRepository.getFullItems(
                offset = queryOffset,
                count = fullItemFetchCount
            )
        )
    }
}
