package com.kssidll.arru.ui.screen.ranking.shopranking

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.stringResource
import com.kssidll.arru.R
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.ui.screen.ranking.RankingScreen
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ShopRankingRoute(
    navigateBack: () -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateShopEdit: (shopId: Long) -> Unit,
) {
    val viewModel: ShopRankingViewModel = hiltViewModel()

    RankingScreen(
        onBack = navigateBack,
        title = stringResource(R.string.shops),
        data = viewModel.shopTotalSpentFlow()
            .collectAsState(emptyList<TransactionTotalSpentByShop>().toImmutableList()).value,
        onItemClick = {
            navigateShop(it.shop.id)
        },
        onItemClickLabel = stringResource(id = R.string.select),
        onItemLongClick = {
            navigateShopEdit(it.shop.id)
        },
        onItemLongClickLabel = stringResource(id = R.string.edit),
    )
}