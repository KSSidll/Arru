package com.kssidll.arru.ui.screen.display.category


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun DisplayCategoryRoute(
    categoryId: Long,
    navigateBack: () -> Unit,
    navigateCategoryEdit: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    viewModel: DisplayCategoryViewModel = hiltViewModel()
) {
    LaunchedEffect(categoryId) {
        if (!viewModel.performDataUpdate(categoryId)) {
            navigateBack()
        }
    }

    DisplayCategoryScreen(
        uiState = viewModel.uiState.collectAsStateWithLifecycle().value,
        onEvent = { event ->
            when (event) {
                is DisplayCategoryEvent.NavigateBack -> navigateBack()
                is DisplayCategoryEvent.NavigateCategoryEdit -> navigateCategoryEdit()
                is DisplayCategoryEvent.NavigateProduct -> navigateProduct(event.productId)
                is DisplayCategoryEvent.NavigateItemEdit -> navigateItemEdit(event.itemId)
                is DisplayCategoryEvent.NavigateProducer -> navigateProducer(event.productProducerId)
                is DisplayCategoryEvent.NavigateShop -> navigateShop(event.shopId)
                is DisplayCategoryEvent.SetSpentByTimePeriod -> viewModel.handleEvent(event)
            }
        }
    )
}