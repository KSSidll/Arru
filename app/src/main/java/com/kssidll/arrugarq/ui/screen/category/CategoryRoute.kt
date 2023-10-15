package com.kssidll.arrugarq.ui.screen.category


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryRoute(
    categoryId: Long,
    onBack: () -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onProducerClick: (producerId: Long) -> Unit,
    onShopClick: (shopId: Long) -> Unit,
) {
    val categoryViewModel: CategoryViewModel = hiltViewModel()

    LaunchedEffect(categoryId) {
        categoryViewModel.performDataUpdate(categoryId)
    }

    CategoryScreen(
        onBack = onBack,
        state = categoryViewModel.categoryScreenState,
        onSpentByTimePeriodSwitch = {
            categoryViewModel.switchPeriod(it)
        },
        requestMoreItems = {
            categoryViewModel.queryMoreFullItems()
        },
        onItemClick = {
            onItemClick(it.embeddedItem.item.productId)
        },
        onProducerClick = {
            onProducerClick(it.id)
        },
        onShopClick = {
            onShopClick(it.id)
        },
    )
}