package com.kssidll.arru.ui.screen.display.shop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun DisplayShopRoute(
    shopId: Long,
    navigateBack: () -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateEditShop: () -> Unit,
    viewModel: DisplayShopViewModel = hiltViewModel(),
) {
    LaunchedEffect(shopId) {
        if (!viewModel.performDataUpdate(shopId)) {
            navigateBack()
        }
    }

    DisplayShopScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                is DisplayShopEvent.NavigateBack -> navigateBack()
                is DisplayShopEvent.NavigateDisplayProduct ->
                    navigateDisplayProduct(event.productId)
                is DisplayShopEvent.NavigateDisplayProductCategory ->
                    navigateDisplayProductCategory(event.productCategoryId)
                is DisplayShopEvent.NavigateDisplayProductProducer ->
                    navigateDisplayProductProducer(event.productProducerId)
                is DisplayShopEvent.NavigateEditItem -> navigateEditItem(event.itemId)
                is DisplayShopEvent.NavigateEditShop -> navigateEditShop()
                is DisplayShopEvent.SetSpentByTimePeriod -> viewModel.handleEvent(event)
            }
        },
    )
}
