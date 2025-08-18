package com.kssidll.arru.ui.screen.display.productproducer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun DisplayProductProducerRoute(
    producerId: Long,
    navigateBack: () -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateEditProductProducer: () -> Unit,
    viewModel: DisplayProductProducerViewModel = hiltViewModel(),
) {
    LaunchedEffect(producerId) {
        if (!viewModel.performDataUpdate(producerId)) {
            navigateBack()
        }
    }

    DisplayProductProducerScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                is DisplayProductProducerEvent.NavigateBack -> navigateBack()
                is DisplayProductProducerEvent.NavigateDisplayProduct ->
                    navigateDisplayProduct(event.productId)
                is DisplayProductProducerEvent.NavigateDisplayProductCategory ->
                    navigateDisplayProductCategory(event.productCategoryId)
                is DisplayProductProducerEvent.NavigateDisplayShop ->
                    navigateDisplayShop(event.shopId)
                is DisplayProductProducerEvent.NavigateEditItem -> navigateEditItem(event.itemId)
                is DisplayProductProducerEvent.NavigateEditProductProducer ->
                    navigateEditProductProducer()
                is DisplayProductProducerEvent.SetSpentByTimePeriod -> viewModel.handleEvent(event)
            }
        },
    )
}
