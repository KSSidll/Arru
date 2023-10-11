package com.kssidll.arrugarq.ui.screen.categoryranking

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryRankingRoute(
    onBack: () -> Unit,
    onItemClick: (categoryId: Long) -> Unit,
) {
    val categoryRankingViewModel: CategoryRankingViewModel = hiltViewModel()

    RankingScreen(
        onBack = onBack,
        title = stringResource(R.string.categories),
        data = categoryRankingViewModel.getSpentByCategory()
            .collectAsState(emptyList()).value,
        onItemClick = {
            onItemClick(it.category.id)
        }
    )
}