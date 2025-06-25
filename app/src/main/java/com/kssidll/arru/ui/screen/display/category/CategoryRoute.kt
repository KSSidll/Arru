package com.kssidll.arru.ui.screen.display.category


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.domain.data.Data
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
fun CategoryRoute(
    categoryId: Long,
    navigateBack: () -> Unit,
    navigateCategoryEdit: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    viewModel: CategoryViewModel = hiltViewModel()
) {
    LaunchedEffect(categoryId) {
        if (!viewModel.performDataUpdate(categoryId)) {
            navigateBack()
        }
    }

    CategoryScreen(
        onBack = navigateBack,
        category = viewModel.category,
        transactionItems = viewModel.transactions()
            .collectAsLazyPagingItems(),
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = Data.Loading())?.value
            ?: Data.Loaded(emptyList<ItemSpentByTime>().toImmutableList()),
        totalSpentData = viewModel.categoryTotalSpent()
            ?.collectAsState(initial = Data.Loading())?.value ?: Data.Loading(),
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onItemClick = navigateProduct,
        onItemProducerClick = navigateProducer,
        onItemShopClick = navigateShop,
        onItemLongClick = navigateItemEdit,
        onEditAction = navigateCategoryEdit,
    )
}