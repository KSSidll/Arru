package com.kssidll.arrugarq.ui.screen.home.search.shoplist


import androidx.compose.runtime.*
import com.kssidll.arrugarq.ui.screen.home.search.shared.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
internal fun ShopListRoute(
    onShopSelect: (shopId: Long) -> Unit,
    onShopEdit: (shopId: Long) -> Unit,
) {
    val viewModel: ShopListViewModel = hiltViewModel()

    ListScreen(
        state = viewModel.screenState,
        onItemSelect = {
            onShopSelect(it.id)
        },
        onItemEdit = {
            onShopEdit(it.id)
        },
    )
}
