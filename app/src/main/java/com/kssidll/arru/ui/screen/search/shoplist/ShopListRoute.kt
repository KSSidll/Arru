package com.kssidll.arru.ui.screen.search.shoplist


import androidx.compose.runtime.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.screen.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun ShopListRoute(
    onShopClick: (shopId: Long) -> Unit,
    onShopLongClick: (shopId: Long) -> Unit,
) {
    val viewModel: ShopListViewModel = hiltViewModel()

    SearchList(
        filter = viewModel.filter,
        onFilterChange = {
            viewModel.filter = it
        },
        items = viewModel.items()
            .collectAsState(initial = Data.Loading()).value,
        onItemClick = {
            onShopClick(it.id)
        },
        onItemLongClick = {
            onShopLongClick(it.id)
        },
    )
}
