package com.kssidll.arru.ui.screen.home.transactions


import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.loadedEmpty
import com.kssidll.arru.domain.model.TransactionPreview
import com.kssidll.arru.ui.component.model.transactionBasketCardHeaderPlaceholder
import com.kssidll.arru.ui.component.model.transactionPreviewCard
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
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionsScreen(
    isExpandedScreen: Boolean,
    transactions: LazyPagingItems<TransactionPreview>,
    onSearchAction: () -> Unit,
    onTransactionLongClick: (transactionId: Long) -> Unit,
    onTransactionLongClickLabel: String,
    onItemAddClick: (transactionId: Long) -> Unit,
) {
    Box {
        // overlay displayed when there is no data available
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = transactions.loadedEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                if (isExpandedScreen) {
                    ExpandedHomeScreenNothingToDisplayOverlay()
                } else {
                    HomeScreenNothingToDisplayOverlay()
                }
            }
        }

        if (isExpandedScreen) {
            Box(
                modifier = Modifier
            ) {
                TransactionScreenContent(
                    transactions = transactions,
                    onTransactionLongClick = onTransactionLongClick,
                    onTransactionLongClickLabel = onTransactionLongClickLabel,
                    onItemAddClick = onItemAddClick,
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .windowInsetsPadding(WindowInsets.statusBars)
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

            AnimatedVisibility(
                visible = transactions.itemCount != 0,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
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
                ) { paddingValues ->
                    Box(
                        modifier = Modifier
                            .consumeWindowInsets(paddingValues)
                            .windowInsetsPadding(WindowInsets.statusBars)
                    ) {
                        TransactionScreenContent(
                            transactions = transactions,
                            onTransactionLongClick = onTransactionLongClick,
                            onTransactionLongClickLabel = onTransactionLongClickLabel,
                            onItemAddClick = onItemAddClick,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionScreenContent(
    transactions: LazyPagingItems<TransactionPreview>,
    onTransactionLongClick: (transactionId: Long) -> Unit,
    onTransactionLongClickLabel: String,
    onItemAddClick: (transactionId: Long) -> Unit,
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
                // ignores orientation but works well enough on landscape imo
                Box(modifier = Modifier.padding(bottom = BOTTOM_SHEET_PEEK_HEIGHT)) {
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
        },
        contentWindowInsets = WindowInsets(0),
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .consumeWindowInsets(paddingValues)
                .fillMaxWidth()
        ) {
            val transactionCount = transactions.itemCount

            // FIXME this will iterate through the whole loop every time item fetch happens, check if paging3 can add sticky headers as separators when you see this
            for (index in 0 until transactionCount) {
                val etherealTransaction = transactions.peek(index)

                if (etherealTransaction == null) {
                    transactionBasketCardHeaderPlaceholder(modifier = Modifier.widthIn(max = 600.dp))
                }

                if (etherealTransaction != null) {
                    val transaction = transactions[index]!!

                    transactionPreviewCard(
                        transaction = transaction,
                        itemsVisible = transaction.itemsVisible.value,
                        onTransactionClick = {
                            transaction.itemsVisible.value = !transaction.itemsVisible.value
                            scope.launch {
                                // the transaction preview card has 2 separate lazy list scope DSL calls
                                // the header and the item list
                                if (listState.firstVisibleItemIndex > index.times(2)) {
                                    listState.animateScrollToItem(index.times(2))
                                }
                            }
                        },
                        onTransactionClickLabel = onTransactionClickLabel,
                        onTransactionLongClick = onTransactionLongClick,
                        onTransactionLongClickLabel = onTransactionLongClickLabel,
                        onItemAddClick = onItemAddClick,
                        headerColor = headerColor,
                        modifier = Modifier.widthIn(max = 600.dp)
                    )
                }
            }

            // Offset the bottom sheet so it doesn't hide the last transaction card
            item {
                Box(modifier = Modifier.height(BOTTOM_SHEET_PEEK_HEIGHT))
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
                transactions = flowOf(PagingData.from(TransactionPreview.generateList())).collectAsLazyPagingItems(),
                onTransactionLongClick = {},
                onTransactionLongClickLabel = String(),
                onItemAddClick = {},
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
                transactions = flowOf(PagingData.from(TransactionPreview.generateList())).collectAsLazyPagingItems(),
                onTransactionLongClick = {},
                onTransactionLongClickLabel = String(),
                onItemAddClick = {},
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
                transactions = flowOf(PagingData.from(emptyList<TransactionPreview>())).collectAsLazyPagingItems(),
                onTransactionLongClick = {},
                onTransactionLongClickLabel = String(),
                onItemAddClick = {},
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
                transactions = flowOf(PagingData.from(emptyList<TransactionPreview>())).collectAsLazyPagingItems(),
                onTransactionLongClick = {},
                onTransactionLongClickLabel = String(),
                onItemAddClick = {},
                onSearchAction = {},
            )
        }
    }
}
