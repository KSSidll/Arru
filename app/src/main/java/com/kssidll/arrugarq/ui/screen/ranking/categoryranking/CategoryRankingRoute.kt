package com.kssidll.arrugarq.ui.screen.ranking.categoryranking

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.ranking.*
import dev.olshevski.navigation.reimagined.hilt.*

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
            .collectAsState(emptyList()).value,
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