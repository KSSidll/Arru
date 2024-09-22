package com.kssidll.arru.ui.screen.home.transactions


import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.data.data.TransactionBasketWithItems
import com.kssidll.arru.domain.data.loadedEmpty
import com.kssidll.arru.ui.component.list.transactionBasketCard
import com.kssidll.arru.ui.component.list.transactionBasketCardHeaderPlaceholder
import com.kssidll.arru.ui.screen.home.component.ExpandedHomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.screen.home.component.HomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.theme.ArrugarqTheme
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

private val BOTTOM_SHEET_PEEK_HEIGHT: Dp = 48.dp

/**
 * @param transactions Transactions to display in the transactions list
 * @param onSearchAction Callback called when the 'search' action is triggered
 * @param onTransactionLongClick Callback called when the transaction is long clicked/pressed. Provides transaction id as parameter
 * @param onItemAddClick Callback called when the transaction item add button is clicked. Provides transaction id as parameter
 * @param onItemClick Callback called when the transaction item is clicked. Provides product id as parameter
 * @param onItemLongClick Callback called when the transaction item is long clicked/pressed. Provides item id as parameter
 * @param onItemCategoryClick Callback called when the transaction item category label is clicked. Provides category id as parameter
 * @param onItemProducerClick Callback called when the transaction item producer label is clicked. Provides producer id as parameter
 * @param onItemShopClick Callback called when the transaction item shop label is clicked. Provides shop id as parameter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionsScreen(
    isExpandedScreen: Boolean,
    transactions: LazyPagingItems<TransactionBasketDisplayData>,
    onSearchAction: () -> Unit,
    onTransactionLongClick: (transactionId: Long) -> Unit,
    onTransactionLongClickLabel: String,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
) {
    Scaffold { paddingValues ->
        // overlay displayed when there is no data available
        AnimatedVisibility(
            visible = transactions.loadedEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
            ) {
                if (isExpandedScreen) {
                    ExpandedHomeScreenNothingToDisplayOverlay()
                } else {
                    HomeScreenNothingToDisplayOverlay()
                }
            }
        }

        AnimatedVisibility(
            visible = transactions.itemCount != 0,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            val layoutDirection = LocalLayoutDirection.current

            if (isExpandedScreen) {
                val horizontalPaddingValues = PaddingValues(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    end = paddingValues.calculateEndPadding(layoutDirection)
                )

                Box {
                    TransactionScreenContent(
                        transactions = transactions,
                        onTransactionLongClick = onTransactionLongClick,
                        onTransactionLongClickLabel = onTransactionLongClickLabel,
                        onItemAddClick = onItemAddClick,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick,
                        onItemCategoryClick = onItemCategoryClick,
                        onItemProducerClick = onItemProducerClick,
                        onItemShopClick = onItemShopClick,
                        modifier = Modifier
                            // apply padding only for horizontal insets because we want vertical edge to edge
                            .padding(horizontalPaddingValues)
                            .consumeWindowInsets(paddingValues)
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(paddingValues)
                            .consumeWindowInsets(paddingValues)
                    ) {
                        IconButton(
                            onClick = {
                                onSearchAction()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = stringResource(R.string.navigate_to_search_description),
                            )
                        }
                    }
                }
            } else {
                val scaffoldState = rememberBottomSheetScaffoldState()

                // For some reason the sheet sometimes hides even if it has skip hidden set to true
                // so when it does, we want to partially expand it
                LaunchedEffect(scaffoldState.bottomSheetState.isVisible) {
                    if (!scaffoldState.bottomSheetState.isVisible) {
                        scaffoldState.bottomSheetState.partialExpand()
                    }
                }

                BottomSheetScaffold(
                    scaffoldState = scaffoldState,
                    sheetContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    sheetContent = {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    onSearchAction()
                                },
                                modifier = Modifier.minimumInteractiveComponentSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(R.string.navigate_to_search_description),
                                )
                            }
                        }
                    },
                    sheetPeekHeight = BOTTOM_SHEET_PEEK_HEIGHT,
                ) { innerPaddingValues ->
                    TransactionScreenContent(
                        transactions = transactions,
                        onTransactionLongClick = onTransactionLongClick,
                        onTransactionLongClickLabel = onTransactionLongClickLabel,
                        onItemAddClick = onItemAddClick,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick,
                        onItemCategoryClick = onItemCategoryClick,
                        onItemProducerClick = onItemProducerClick,
                        onItemShopClick = onItemShopClick,
                        fabPadding = innerPaddingValues,
                        modifier = Modifier
                            // don't add padding for inner insets to allow edge to edge over the bottom sheet
                            .consumeWindowInsets(innerPaddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionScreenContent(
    transactions: LazyPagingItems<TransactionBasketDisplayData>,
    onTransactionLongClick: (transactionId: Long) -> Unit,
    onTransactionLongClickLabel: String,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
    modifier: Modifier = Modifier,
    fabPadding: PaddingValues = PaddingValues(),
) {
    val onTransactionClickLabel = stringResource(id = R.string.transaction_items_toggle)
    val headerColor = MaterialTheme.colorScheme.background

    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    var previousFirstVisibleItemIndex by remember { mutableIntStateOf(0) }

    var returnActionButtonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(firstVisibleItemIndex) {
        if (
            previousFirstVisibleItemIndex > firstVisibleItemIndex + 1 &&
            firstVisibleItemIndex >= 10
        ) {
            // scrolling up
            returnActionButtonVisible = true
            previousFirstVisibleItemIndex = firstVisibleItemIndex
        } else if (
            previousFirstVisibleItemIndex < firstVisibleItemIndex - 1 ||
            firstVisibleItemIndex < 10
        ) {
            // scrolling down
            returnActionButtonVisible = false
            previousFirstVisibleItemIndex = firstVisibleItemIndex
        }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = returnActionButtonVisible,
                enter = slideInHorizontally(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = EaseOut
                    ),
                    initialOffsetX = { it }
                ),
                exit = slideOutHorizontally(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = EaseIn
                    ),
                    targetOffsetX = { it }
                )
            ) {
                Box(modifier = Modifier.padding(fabPadding)) {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                listState.animateScrollToItem(0)
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowUpward,
                            contentDescription = null,
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxWidth()
        ) {
            // Offset the fab top padding for the list so edge to edge doesn't hide any transaction cards
            if (fabPadding.calculateTopPadding() != 0.dp) {
                item {
                    Box(modifier = Modifier.height(fabPadding.calculateTopPadding()))
                }
            }

            val transactionCount = transactions.itemCount

            // FIXME this will iterate through the whole loop every time item fetch happens, check if paging3 can add sticky headers as separators when you see this
            for (index in 0 until transactionCount) {
                val etherealTransaction = transactions.peek(index)

                if (etherealTransaction == null) {
                    transactionBasketCardHeaderPlaceholder(modifier = Modifier.widthIn(max = 600.dp))
                }

                if (etherealTransaction != null) {
                    val transaction = transactions[index]!!

                    transactionBasketCard(
                        transaction = transaction.basket,
                        itemsVisible = transaction.itemsVisible.value,
                        onTransactionClick = {
                            transaction.itemsVisible.value = !transaction.itemsVisible.value
                            scope.launch {
                                // the transaction basket card has 2 separate lazy list scope DSL calls
                                if (listState.firstVisibleItemIndex > index.times(2)) {
                                    listState.animateScrollToItem(index.times(2))
                                }
                            }
                        },
                        onTransactionClickLabel = onTransactionClickLabel,
                        onTransactionLongClick = onTransactionLongClick,
                        onTransactionLongClickLabel = onTransactionLongClickLabel,
                        onItemAddClick = onItemAddClick,
                        onItemClick = onItemClick,
                        onItemLongClick = onItemLongClick,
                        onItemCategoryClick = onItemCategoryClick,
                        onItemProducerClick = onItemProducerClick,
                        onItemShopClick = onItemShopClick,
                        headerColor = headerColor,
                        modifier = Modifier.widthIn(max = 600.dp)
                    )
                }
            }

            // Offset the fab bottom padding for the list so edge to edge doesn't hide any transaction cards
            if (fabPadding.calculateBottomPadding() != 0.dp) {
                item {
                    Box(modifier = Modifier.height(fabPadding.calculateBottomPadding()))
                }
            }
        }
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@PreviewLightDark
@Composable
private fun TransactionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreen(
                isExpandedScreen = false,
                transactions = flowOf(PagingData.from(TransactionBasketWithItems.generateList())).toDisplayData()
                    .collectAsLazyPagingItems(),
                onTransactionLongClick = {},
                onTransactionLongClickLabel = String(),
                onItemAddClick = {},
                onItemLongClick = {},
                onItemProducerClick = {},
                onItemCategoryClick = {},
                onItemClick = {},
                onItemShopClick = {},
                onSearchAction = {},
            )
        }
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@PreviewExpanded
@Composable
private fun ExpandedTransactionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreen(
                isExpandedScreen = true,
                transactions = flowOf(PagingData.from(TransactionBasketWithItems.generateList())).toDisplayData()
                    .collectAsLazyPagingItems(),
                onTransactionLongClick = {},
                onTransactionLongClickLabel = String(),
                onItemAddClick = {},
                onItemLongClick = {},
                onItemProducerClick = {},
                onItemCategoryClick = {},
                onItemClick = {},
                onItemShopClick = {},
                onSearchAction = {},
            )
        }
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@PreviewLightDark
@Composable
private fun EmptyTransactionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreen(
                isExpandedScreen = false,
                transactions = flowOf(PagingData.from(emptyList<TransactionBasketDisplayData>()))
                    .collectAsLazyPagingItems(),
                onTransactionLongClick = {},
                onTransactionLongClickLabel = String(),
                onItemAddClick = {},
                onItemLongClick = {},
                onItemProducerClick = {},
                onItemCategoryClick = {},
                onItemClick = {},
                onItemShopClick = {},
                onSearchAction = {},
            )
        }
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@PreviewLightDark
@PreviewExpanded
@Composable
private fun ExpandedEmptyTransactionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreen(
                isExpandedScreen = true,
                transactions = flowOf(PagingData.from(emptyList<TransactionBasketDisplayData>()))
                    .collectAsLazyPagingItems(),
                onTransactionLongClick = {},
                onTransactionLongClickLabel = String(),
                onItemAddClick = {},
                onItemLongClick = {},
                onItemProducerClick = {},
                onItemCategoryClick = {},
                onItemClick = {},
                onItemShopClick = {},
                onSearchAction = {},
            )
        }
    }
}
