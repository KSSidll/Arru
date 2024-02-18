package com.kssidll.arru.ui.screen.home.transactions


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.paging.*
import androidx.paging.compose.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.theme.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

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
    transactions: LazyPagingItems<TransactionBasketWithItems>,
    onSearchAction: () -> Unit,
    onTransactionLongClick: (transactionId: Long) -> Unit,
    onItemAddClick: (transactionId: Long) -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
) {
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

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val scrollConnection = scrollBehavior.nestedScrollConnection

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                actions = {
                    // 'search' action
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
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
                scrollBehavior = scrollBehavior,
            )
        },
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
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                ) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowUpward,
                        contentDescription = null,
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .nestedScroll(scrollConnection)
                .padding(paddingValues)
                .padding(top = 12.dp),
        ) {
            items(
                transactions.itemCount,
                key = transactions.itemKey { it.id }
            ) { index ->
                val transaction = transactions[index]

                if (transaction != null) {
                    Box(modifier = Modifier.widthIn(max = 600.dp)) {
                        TransactionBasketCard(
                            transaction = transaction,
                            onTransactionLongClick = onTransactionLongClick,
                            onItemAddClick = onItemAddClick,
                            onItemClick = onItemClick,
                            onItemLongClick = onItemLongClick,
                            onItemCategoryClick = onItemCategoryClick,
                            onItemProducerClick = onItemProducerClick,
                            onItemShopClick = onItemShopClick,
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewExpanded
@Composable
fun TransactionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreen(
                transactions = flowOf(PagingData.from(TransactionBasketWithItems.generateList())).collectAsLazyPagingItems(),
                onTransactionLongClick = {},
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
