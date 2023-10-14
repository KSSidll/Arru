package com.kssidll.arrugarq.ui.screen.shop


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ShopRoute(
    shopId: Long,
    onBack: () -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onCategoryClick: (categoryId: Long) -> Unit,
    onProducerClick: (producerId: Long) -> Unit,
) {
    val shopViewModel: ShopViewModel = hiltViewModel()

    LaunchedEffect(shopId) {
        shopViewModel.performDataUpdate(shopId)
    }

    ShopScreen(
        onBack = onBack,
        state = shopViewModel.shopScreenState,
        onSpentByTimePeriodSwitch = {
            shopViewModel.switchPeriod(it)
        },
        requestMoreItems = {
            shopViewModel.queryMoreFullItems()
        },
        onItemClick = {
            onItemClick(it.embeddedItem.item.productId)
        },
        onCategoryClick = {
            onCategoryClick(it.id)
        },
        onProducerClick = {
            onProducerClick(it.id)
        },
    )
}