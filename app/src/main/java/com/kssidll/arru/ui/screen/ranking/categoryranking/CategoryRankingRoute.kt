package com.kssidll.arru.ui.screen.ranking.categoryranking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.ranking.RankingScreen
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
fun CategoryRankingRoute(
    navigateBack: () -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateCategoryEdit: (categoryId: Long) -> Unit,
) {
    val viewModel: CategoryRankingViewModel = hiltViewModel()

    RankingScreen(
        onBack = navigateBack,
        title = stringResource(R.string.categories),
        data = viewModel.categoryTotalSpentFlow()
            .collectAsState(emptyImmutableList()).value,
        onItemClick = {
            navigateCategory(it.category.id)
        },
        onItemClickLabel = stringResource(id = R.string.select),
        onItemLongClick = {
            navigateCategoryEdit(it.category.id)
        },
        onItemLongClickLabel = stringResource(id = R.string.edit),
    )
}