package com.kssidll.arrugarq.ui.screen.category.categoryranking

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
    val viewModel: CategoryRankingViewModel = hiltViewModel()

    RankingScreen(
        onBack = onBack,
        title = stringResource(R.string.categories),
        data = viewModel.getSpentByCategory()
            .collectAsState(emptyList()).value,
        onItemClick = {
            onItemClick(it.category.id)
        }
    )
}