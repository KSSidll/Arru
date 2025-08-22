package com.kssidll.arru.ui.screen.ranking.categoryranking

import androidx.lifecycle.ViewModel
import com.kssidll.arru.data.data.TotalSpentByCategory
import com.kssidll.arru.data.repository.ProductCategoryRepositorySource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.Flow

// TODO refactor uiState Event UseCase

@HiltViewModel
class CategoryRankingViewModel
@Inject
constructor(private val categoryRepository: ProductCategoryRepositorySource) : ViewModel() {

    /** @return List of data points representing shop spending in time as flow */
    fun categoryTotalSpentFlow(): Flow<ImmutableList<TotalSpentByCategory>> {
        return categoryRepository.totalSpentByCategory()
    }
}
