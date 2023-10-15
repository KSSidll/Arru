package com.kssidll.arrugarq.ui.screen.product


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun ProductRoute(
    productId: Long,
    onBack: () -> Unit,
    onCategoryClick: (categoryId: Long) -> Unit,
    onProducerClick: (producerId: Long) -> Unit,
    onShopClick: (shopId: Long) -> Unit,
) {
    val productViewModel: ProductViewModel = hiltViewModel()

    LaunchedEffect(productId) {
        productViewModel.performDataUpdate(productId)
    }

    ProductScreen(
        onBack = onBack,
        state = productViewModel.productScreenState,
        onSpentByTimePeriodSwitch = {
            productViewModel.switchPeriod(it)
        },
        requestMoreItems = {
            productViewModel.queryMoreFullItems()
        },
        onCategoryClick = {
            onCategoryClick(it.id)
        },
        onProducerClick = {
            onProducerClick(it.id)
        },
        onShopClick = {
            onShopClick(it.id)
        },
    )
}