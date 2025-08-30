package com.kssidll.arru.ui.screen.display.shop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun DisplayShopRoute(
    shopId: Long?,
    navigateBack: () -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateEditShop: () -> Unit,
    viewModel: DisplayShopViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(shopId)) {
                navigateBack()
            }
        }
    }

    LaunchedEffect(shopId) { viewModel.updateState(shopId) }

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
