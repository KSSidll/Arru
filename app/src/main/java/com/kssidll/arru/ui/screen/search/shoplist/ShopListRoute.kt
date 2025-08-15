package com.kssidll.arru.ui.screen.search.shoplist


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.ui.screen.search.shared.SearchList
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
internal fun ShopListRoute(
    onShopClick: (shopId: Long) -> Unit,
    onShopLongClick: (shopId: Long) -> Unit,
    viewModel: ShopListViewModel = hiltViewModel()
) {
    SearchList(
        filter = viewModel.filter,
        onFilterChange = {
            viewModel.filter = it
        },
        items = viewModel.items()
            .collectAsState(initial = emptyImmutableList()).value,
        onItemClick = {
            onShopClick(it.id)
        },
        onItemLongClick = {
            onShopLongClick(it.id)
        },
    )
}
