package com.kssidll.arru.ui.screen.home.transactions


import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.R
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
internal fun TransactionsRoute(
    isExpandedScreen: Boolean,
    navigateSearch: () -> Unit,
    navigateProduct: (productId: Long) -> Unit,
    navigateCategory: (categoryId: Long) -> Unit,
    navigateProducer: (producerId: Long) -> Unit,
    navigateShop: (shopId: Long) -> Unit,
    navigateItemAdd: (transactionId: Long) -> Unit,
    navigateTransactionEdit: (transactionId: Long) -> Unit,
    navigateItemEdit: (itemId: Long) -> Unit,
) {
    val viewModel: TransactionsViewModel = hiltViewModel()

    TransactionsScreen(
        isExpandedScreen = isExpandedScreen,
        transactions = viewModel.transactions()
            .collectAsLazyPagingItems(),
        onSearchAction = navigateSearch,
        onTransactionLongClick = navigateTransactionEdit,
        onTransactionLongClickLabel = stringResource(id = R.string.edit),
        onItemAddClick = navigateItemAdd,
        onItemClick = navigateProduct,
        onItemLongClick = navigateItemEdit,
        onItemCategoryClick = navigateCategory,
        onItemProducerClick = navigateProducer,
        onItemShopClick = navigateShop,
    )
}
