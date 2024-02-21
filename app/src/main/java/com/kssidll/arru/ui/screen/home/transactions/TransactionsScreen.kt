package com.kssidll.arru.ui.screen.home.transactions


import android.annotation.*
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
import androidx.compose.ui.text.style.*
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
                .fillMaxSize()
                .nestedScroll(scrollConnection)
                .padding(paddingValues)
        ) {
            if (transactions.itemCount == 0) {
                item {
                    AnimatedVisibility(visible = transactions.itemCount == 0) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = stringResource(id = R.string.no_data_to_display_text),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            val transactionCount = transactions.itemCount

            // FIXME this will iterate through the whole loop every time item fetch happens, check if paging3 can add sticky headers as separators when you see this
            for (index in 0 until transactionCount) {
                val transaction = transactions[index]

                if (transaction != null) {
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
                        modifier = Modifier.width(600.dp)
                    )
                }
            }
        }
    }
}

@SuppressLint("FlowOperatorInvokedInComposition")
@PreviewLightDark
@PreviewExpanded
@Composable
fun TransactionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreen(
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
