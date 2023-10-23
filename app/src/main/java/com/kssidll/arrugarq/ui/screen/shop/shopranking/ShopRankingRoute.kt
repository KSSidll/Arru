package com.kssidll.arrugarq.ui.screen.shop.shopranking

import androidx.compose.runtime.*
import androidx.compose.ui.res.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.ui.screen.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ShopRankingRoute(
    onBack: () -> Unit,
    onItemClick: (shopId: Long) -> Unit,
) {
    val viewModel: ShopRankingViewModel = hiltViewModel()

    RankingScreen(
        onBack = onBack,
        title = stringResource(R.string.shops),
        data = viewModel.getSpentByShop()
            .collectAsState(emptyList()).value,
        onItemClick = {
            onItemClick(it.shop.id)
        }
    )
}