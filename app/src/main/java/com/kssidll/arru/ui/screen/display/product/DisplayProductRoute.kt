package com.kssidll.arru.ui.screen.display.product


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun DisplayProductRoute(
    productId: Long,
    navigateBack: () -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateEditProduct: () -> Unit,
    viewModel: DisplayProductViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) {
        if (!viewModel.performDataUpdate(productId)) {
            navigateBack()
        }
    }

    DisplayProductScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value, onEvent = { event ->
            when (event) {
                is DisplayProductEvent.NavigateBack                   -> navigateBack()
                is DisplayProductEvent.NavigateDisplayProductCategory -> navigateDisplayProductCategory(event.productCategoryId)
                is DisplayProductEvent.NavigateDisplayProductProducer -> navigateDisplayProductProducer(event.productProducerId)
                is DisplayProductEvent.NavigateDisplayShop            -> navigateDisplayShop(event.shopId)
                is DisplayProductEvent.NavigateEditItem               -> navigateEditItem(event.itemId)
                is DisplayProductEvent.NavigateEditProduct            -> navigateEditProduct()
                is DisplayProductEvent.SetSpentByTimePeriod           -> viewModel.handleEvent(event)
            }
        })
}