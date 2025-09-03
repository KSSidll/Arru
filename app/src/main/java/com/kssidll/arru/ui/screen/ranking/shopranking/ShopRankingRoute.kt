package com.kssidll.arru.ui.screen.ranking.shopranking

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.R
import com.kssidll.arru.ui.screen.ranking.RankingScreen
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ShopRankingRoute(
    navigateBack: () -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditShop: (shopId: Long) -> Unit,
    viewModel: ShopRankingViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    RankingScreen(
        onBack = navigateBack,
        title = stringResource(R.string.shops),
        data = uiState.totalSpentByShop,
        onItemClick = { navigateDisplayShop(it.shop.id) },
        onItemClickLabel = stringResource(id = R.string.select),
        onItemLongClick = { navigateEditShop(it.shop.id) },
        onItemLongClickLabel = stringResource(id = R.string.edit),
    )
}
