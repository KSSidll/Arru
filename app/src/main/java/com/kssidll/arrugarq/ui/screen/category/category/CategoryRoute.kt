package com.kssidll.arrugarq.ui.screen.category.category


import androidx.compose.runtime.*
import dev.olshevski.navigation.reimagined.hilt.*

@Composable
fun CategoryRoute(
    categoryId: Long,
    onBack: () -> Unit,
    onCategoryEdit: () -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
) {
    val viewModel: CategoryViewModel = hiltViewModel()

    LaunchedEffect(categoryId) {
        if (!viewModel.performDataUpdate(categoryId)) {
            onBack()
        }
    }

    CategoryScreen(
        onBack = onBack,
        state = viewModel.screenState,
        onCategoryEdit = onCategoryEdit,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        requestMoreItems = {
            viewModel.queryMoreFullItems()
        },
        onProductSelect = onProductSelect,
        onItemEdit = onItemEdit,
        onProducerSelect = onProducerSelect,
        onShopSelect = onShopSelect,
    )
}