package com.kssidll.arru.ui.screen.ranking.categoryranking

import androidx.lifecycle.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.data.repository.*
import com.kssidll.arru.domain.data.*
import dagger.hilt.android.lifecycle.*
import kotlinx.coroutines.flow.*
import javax.inject.*

@HiltViewModel
class CategoryRankingViewModel @Inject constructor(
    private val categoryRepository: CategoryRepositorySource,
): ViewModel() {

    /**
     * @return List of data points representing shop spending in time as flow
     */
    fun categoryTotalSpentFlow(): Flow<Data<List<ItemSpentByCategory>>> {
        return categoryRepository.totalSpentByCategoryFlow()
    }
}