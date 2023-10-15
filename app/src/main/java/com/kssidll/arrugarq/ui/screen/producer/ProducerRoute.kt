package com.kssidll.arrugarq.ui.screen.producer


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProducerRoute(
    producerId: Long,
    onBack: () -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onCategoryClick: (categoryId: Long) -> Unit,
    onShopClick: (shopId: Long) -> Unit,
) {
    val producerViewModel: ProducerViewModel = hiltViewModel()

    LaunchedEffect(producerId) {
        producerViewModel.performDataUpdate(producerId)
    }

    ProducerScreen(
        onBack = onBack,
        state = producerViewModel.producerScreenState,
        onSpentByTimePeriodSwitch = {
            producerViewModel.switchPeriod(it)
        },
        requestMoreItems = {
            producerViewModel.queryMoreFullItems()
        },
        onItemClick = {
            onItemClick(it.embeddedItem.item.productId)
        },
        onCategoryClick = {
            onCategoryClick(it.id)
        },
        onShopClick = {
            onShopClick(it.id)
        },
    )
}