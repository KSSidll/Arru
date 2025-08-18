package com.kssidll.arru.ui.screen.display.transaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel

@Composable
fun DisplayTransactionRoute(
    transactionId: Long,
    navigateBack: () -> Unit,
    navigateEditTransaction: (transactionId: Long) -> Unit,
    navigateAddItem: (transactionId: Long) -> Unit,
    navigateDisplayProduct: (productId: Long) -> Unit,
    navigateEditItem: (itemId: Long) -> Unit,
    navigateDisplayProductCategory: (categoryId: Long) -> Unit,
    navigateDisplayProductProducer: (producerId: Long) -> Unit,
    navigateDisplayShop: (shopId: Long) -> Unit,
    viewModel: DisplayTransactionViewModel = hiltViewModel()
) {

    DisplayTransactionScreen(
        onBack = navigateBack,
        transaction = viewModel.transaction(transactionId).collectAsState(initial = null).value,
        onEditAction = {
            navigateEditTransaction(transactionId)
        },
        onItemAddClick = navigateAddItem,
        onItemClick = navigateDisplayProduct,
        onItemLongClick = navigateEditItem,
        onItemCategoryClick = navigateDisplayProductCategory,
        onItemProducerClick = navigateDisplayProductProducer,
        onItemShopClick = navigateDisplayShop,
    )
}