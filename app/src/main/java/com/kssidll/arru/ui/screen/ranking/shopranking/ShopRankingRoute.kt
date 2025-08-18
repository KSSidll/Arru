package com.kssidll.arru.ui.screen.ranking.shopranking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.ranking.RankingScreen
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun ShopRankingRoute(
    navigateBack: () -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditShop: (shopId: Long) -> Unit,
) {
    val viewModel: ShopRankingViewModel = hiltViewModel()

    RankingScreen(
        onBack = navigateBack,
        title = stringResource(R.string.shops),
        data = viewModel.shopTotalSpentFlow()
            .collectAsState(emptyImmutableList()).value,
        onItemClick = {
            navigateDisplayShop(it.shop.id)
        },
        onItemClickLabel = stringResource(id = R.string.select),
        onItemLongClick = {
            navigateEditShop(it.shop.id)
        },
        onItemLongClickLabel = stringResource(id = R.string.edit),
    )
}