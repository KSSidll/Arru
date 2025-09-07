package com.kssidll.arru.ui.screen.display.productcategory

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.coroutines.launch

@Composable
fun DisplayProductCategoryRoute(
    categoryId: Long?,
    navigateBack: () -> Unit,
    navigateEditProductCategory: () -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateEditProductProducer: (producerId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    navigateEditShop: (shopId: Long) -> Unit,
    viewModel: DisplayProductCategoryViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()

    SideEffect {
        scope.launch {
            if (!viewModel.checkExists(categoryId)) {
                navigateBack()
            }
        }
    }

    LaunchedEffect(categoryId) { viewModel.updateState(categoryId) }

    DisplayProductCategoryScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                is DisplayProductCategoryEvent.NavigateBack -> navigateBack()
                is DisplayProductCategoryEvent.NavigateEditProductCategory ->
                    navigateEditProductCategory()
                is DisplayProductCategoryEvent.NavigateDisplayProduct ->
                    navigateDisplayProduct(event.productId)
                is DisplayProductCategoryEvent.NavigateEditItem -> navigateEditItem(event.itemId)
                is DisplayProductCategoryEvent.NavigateDisplayProductProducer ->
                    navigateDisplayProductProducer(event.productProducerId)
                is DisplayProductCategoryEvent.NavigateEditProductProducer ->
                    navigateEditProductProducer(event.productProducerId)
                is DisplayProductCategoryEvent.NavigateDisplayShop ->
                    navigateDisplayShop(event.shopId)
                is DisplayProductCategoryEvent.NavigateEditShop -> navigateEditShop(event.shopId)
                is DisplayProductCategoryEvent.SetSpentByTimePeriod -> viewModel.handleEvent(event)
            }
        },
    )
}
