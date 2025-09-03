package com.kssidll.arru.ui.screen.search.shoplist

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kssidll.arru.ui.screen.search.shared.SearchList
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
internal fun ShopListRoute(
    onShopClick: (shopId: Long) -> Unit,
    onShopLongClick: (shopId: Long) -> Unit,
    viewModel: ShopListViewModel = hiltViewModel(),
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value

    SearchList(
        filter = uiState.filter,
        onFilterChange = { viewModel.handleEvent(ShopListSearchEvent.SetFilter(it)) },
        items = uiState.allShops,
        onItemClick = { onShopClick(it.id) },
        onItemLongClick = { onShopLongClick(it.id) },
    )
}
