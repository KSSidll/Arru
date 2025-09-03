package com.kssidll.arru.ui.screen.ranking.productcategory

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.ranking.RankingScreen
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ProductCategoryRankingRoute(
    navigateBack: () -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateEditProductCategory: (categoryId: Long) -> Unit,
    viewModel: ProductCategoryRankingViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    RankingScreen(
        onBack = navigateBack,
        title = stringResource(R.string.categories),
        data = uiState.totalSpentByProductCategory,
        onItemClick = { navigateDisplayProductCategory(it.category.id) },
        onItemClickLabel = stringResource(id = R.string.select),
        onItemLongClick = { navigateEditProductCategory(it.category.id) },
        onItemLongClickLabel = stringResource(id = R.string.edit),
    )
}
