package com.kssidll.arru.ui.screen.display.product


import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.data.data.ItemSpentByTime
import com.kssidll.arru.data.data.ProductPriceByShopByTime
import com.kssidll.arru.domain.data.Data
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import kotlinx.collections.immutable.toImmutableList

@Composable
fun ProductRoute(
    productId: Long,
    navigateBack: () -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
    navigateProductEdit: () -> Unit,
    viewModel: ProductViewModel = hiltViewModel()
) {
    LaunchedEffect(productId) {
        if (!viewModel.performDataUpdate(productId)) {
            navigateBack()
        }
    }

    ProductScreen(
        onBack = navigateBack,
        product = viewModel.product,
        transactionItems = viewModel.transactions()
            .collectAsLazyPagingItems(),
        spentByTimeData = viewModel.spentByTimeData?.collectAsState(initial = Data.Loading())?.value
            ?: Data.Loaded(emptyList<ItemSpentByTime>().toImmutableList()),
        productPriceByShopByTimeData = viewModel.productPriceByShop()
            ?.collectAsState(initial = Data.Loading())?.value ?: Data.Loaded(emptyList<ProductPriceByShopByTime>().toImmutableList()),
        totalSpentData = viewModel.productTotalSpent()
            ?.collectAsState(initial = Data.Loading())?.value ?: Data.Loaded(0F),
        spentByTimePeriod = viewModel.spentByTimePeriod,
        onSpentByTimePeriodSwitch = {
            viewModel.switchPeriod(it)
        },
        chartEntryModelProducer = viewModel.chartEntryModelProducer,
        onItemCategoryClick = navigateCategory,
        onItemProducerClick = navigateProducer,
        onItemShopClick = navigateShop,
        onItemLongClick = navigateItemEdit,
        onEditAction = navigateProductEdit,
    )
}