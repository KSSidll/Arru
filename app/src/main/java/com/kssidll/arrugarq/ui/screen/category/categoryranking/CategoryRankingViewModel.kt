package com.kssidll.arrugarq.ui.screen.category.categoryranking

import androidx.lifecycle.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.repository.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class CategoryRankingViewModel @Inject constructor(
    private val itemRepository: IItemRepository,
): ViewModel() {

    /**
     * @return List of data points representing shop spending in time as flow
     */
    fun categoryTotalSpentFlow(): Flow<List<ItemSpentByCategory>> {
        return itemRepository.getCategoryTotalSpentFlow()
    }
}