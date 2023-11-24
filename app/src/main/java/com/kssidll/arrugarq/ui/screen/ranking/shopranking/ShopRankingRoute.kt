package com.kssidll.arrugarq.ui.screen.ranking.shopranking

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.ranking.*
import dev.olshevski.navigation.reimagined.hilt.*

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
            .collectAsState(emptyList()).value,
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