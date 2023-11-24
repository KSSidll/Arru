package com.kssidll.arrugarq.ui.screen.search.shoplist


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun ShopListRoute(
    onShopClick: (shopId: Long) -> Unit,
    onShopLongClick: (shopId: Long) -> Unit,
) {
    val viewModel: ShopListViewModel = hiltViewModel()

    ListScreen(
        state = viewModel.screenState,
        onItemClick = {
            onShopClick(it.id)
        },
        onItemLongClick = {
            onShopLongClick(it.id)
        },
    )
}
