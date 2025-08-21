package com.kssidll.arru.ui.screen.home

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
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.LocalCurrencyFormatLocale
import com.kssidll.arru.R
import com.kssidll.arru.domain.data.data.Transaction
import com.kssidll.arru.domain.data.loadedEmpty
import com.kssidll.arru.helper.BetterNavigationSuiteScaffoldDefaults
import com.kssidll.arru.ui.component.list.transactionBasketCard
import com.kssidll.arru.ui.component.list.transactionBasketCardHeaderPlaceholder
import com.kssidll.arru.ui.screen.home.component.ExpandedHomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.screen.home.component.HomeScreenNothingToDisplayOverlay
import com.kssidll.arru.ui.theme.ArruTheme
import java.util.Locale
import kotlinx.coroutines.launch

private val BOTTOM_SHEET_PEEK_HEIGHT: Dp = 48.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(
    uiState: HomeUiState,
    onEvent: (event: HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navSuiteType =
        BetterNavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    val transactions = uiState.transactions.collectAsLazyPagingItems()

    Scaffold(modifier = modifier) { paddingValues ->
        // overlay displayed when there is no data available
        AnimatedVisibility(
            visible = transactions.loadedEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier =
                    Modifier.fillMaxSize().padding(paddingValues).consumeWindowInsets(paddingValues)
            ) {
                if (navSuiteType != NavigationSuiteType.NavigationBar) {
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

            if (navSuiteType == NavigationSuiteType.NavigationRail) {
                val horizontalPaddingValues =
                    PaddingValues(
                        start = paddingValues.calculateStartPadding(layoutDirection),
                        end = paddingValues.calculateEndPadding(layoutDirection),
                    )

                Box {
                    // apply padding only for horizontal insets because we want vertical edge to
                    // edge
                    Box(
                        modifier =
                            Modifier.padding(horizontalPaddingValues)
                                .consumeWindowInsets(paddingValues)
                    ) {
                        TransactionScreenContent(
                            uiState = uiState,
                            onEvent = onEvent,
                            listState = uiState.transactionsListState,
                            transactions = transactions,
                        )
                    }

                    Box(
                        modifier =
                            Modifier.align(Alignment.TopEnd)
                                .padding(paddingValues)
                                .consumeWindowInsets(paddingValues)
                    ) {
                        IconButton(onClick = { onEvent(HomeEvent.NavigateSearch) }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription =
                                    stringResource(R.string.navigate_to_search_description),
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
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
                        ) {
                            IconButton(
                                onClick = { onEvent(HomeEvent.NavigateSearch) },
                                modifier = Modifier.minimumInteractiveComponentSize(),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription =
                                        stringResource(R.string.navigate_to_search_description),
                                )
                            }
                        }
                    },
                    sheetPeekHeight = BOTTOM_SHEET_PEEK_HEIGHT,
                ) { innerPaddingValues ->
                    // don't add padding for inner insets to allow edge to edge over the bottom
                    // sheet
                    Box(modifier = Modifier.consumeWindowInsets(innerPaddingValues)) {
                        TransactionScreenContent(
                            uiState = uiState,
                            onEvent = onEvent,
                            listState = uiState.transactionsListState,
                            transactions = transactions,
                            fabPadding = innerPaddingValues,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionScreenContent(
    uiState: HomeUiState,
    onEvent: (event: HomeEvent) -> Unit,
    listState: LazyListState,
    transactions: LazyPagingItems<Transaction>,
    modifier: Modifier = Modifier,
    fabPadding: PaddingValues = PaddingValues(),
) {
    val currencyLocale = LocalCurrencyFormatLocale.current ?: Locale.getDefault()

    val headerColor = MaterialTheme.colorScheme.background

    val scope = rememberCoroutineScope()

    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    var previousFirstVisibleItemIndex by remember { mutableIntStateOf(0) }

    var returnActionButtonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(firstVisibleItemIndex) {
        if (
            previousFirstVisibleItemIndex > firstVisibleItemIndex + 1 && firstVisibleItemIndex >= 10
        ) {
            // scrolling up
            returnActionButtonVisible = true
            previousFirstVisibleItemIndex = firstVisibleItemIndex
        } else if (
            previousFirstVisibleItemIndex < firstVisibleItemIndex - 1 || firstVisibleItemIndex < 10
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
                enter =
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 300, easing = EaseOut),
                        initialOffsetX = { it },
                    ),
                exit =
                    slideOutHorizontally(
                        animationSpec = tween(durationMillis = 300, easing = EaseIn),
                        targetOffsetX = { it },
                    ),
            ) {
                Box(modifier = Modifier.padding(fabPadding)) {
                    FloatingActionButton(
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ) {
                        Icon(imageVector = Icons.Rounded.ArrowUpward, contentDescription = null)
                    }
                }
            }
        },
        modifier = modifier,
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.padding(paddingValues).consumeWindowInsets(paddingValues).fillMaxWidth(),
        ) {
            // Offset the fab top padding for the list so edge to edge doesn't hide any transaction
            // cards
            if (fabPadding.calculateTopPadding() != 0.dp) {
                item(contentType = "top fab padding offset") {
                    Box(modifier = Modifier.height(fabPadding.calculateTopPadding()))
                }
            }

            val transactionCount = transactions.itemCount

            // for some reason, when the paging source is invalidated through item edition or
            // insertion
            // it starts loading the data from seemingly random index, thus we notify the source of
            // an access
            // to the first visible item before loading the data, so that it loads it first
            transactions[firstVisibleItemIndex.coerceIn(0, transactionCount - 1)]

            // FIXME this will iterate through the whole loop every time item fetch happens, check
            // if paging3 can add sticky headers as separators when you see this
            for (index in 0 until transactionCount) {
                val etherealTransaction = transactions.peek(index)

                if (etherealTransaction == null) {
                    transactionBasketCardHeaderPlaceholder(
                        modifier = Modifier.widthIn(max = 600.dp)
                    )
                }

                if (etherealTransaction != null) {
                    val transaction = transactions[index]!!

                    transactionBasketCard(
                        transaction = transaction,
                        itemsVisible = uiState.transactionWithVisibleItems.contains(transaction.id),
                        onTransactionClick = {
                            onEvent(HomeEvent.ToggleTransactionItemVisibility(transaction.id))

                            scope.launch {
                                // the transaction basket card has 2 separate lazy list scope DSL
                                // calls
                                if (listState.firstVisibleItemIndex > index.times(2)) {
                                    listState.animateScrollToItem(index.times(2))
                                }
                            }
                        },
                        onTransactionLongClick = {
                            onEvent(HomeEvent.NavigateEditTransaction(transaction.id))
                        },
                        onItemAddClick = { onEvent(HomeEvent.NavigateAddItem(transaction.id)) },
                        onItemClick = { onEvent(HomeEvent.NavigateDisplayProduct(it.productId)) },
                        onItemLongClick = { onEvent(HomeEvent.NavigateEditItem(it.id)) },
                        onItemCategoryClick = {
                            onEvent(HomeEvent.NavigateDisplayProductCategory(it.productCategoryId))
                        },
                        onItemProducerClick = {
                            it.productProducerId?.let { productProducerId ->
                                onEvent(HomeEvent.NavigateDisplayProductProducer(productProducerId))
                            }
                        },
                        onItemShopClick = {
                            transaction.shopId?.let { shopId ->
                                onEvent(HomeEvent.NavigateDisplayShop(shopId))
                            }
                        },
                        headerColor = headerColor,
                        currencyLocale = currencyLocale,
                        modifier = Modifier.widthIn(max = 600.dp),
                    )
                }
            }

            // Offset the fab bottom padding for the list so edge to edge doesn't hide any
            // transaction cards
            if (fabPadding.calculateBottomPadding() != 0.dp) {
                item(contentType = "bottom fab padding offset") {
                    Box(modifier = Modifier.height(fabPadding.calculateBottomPadding()))
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun TransactionsScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreen(uiState = HomeUiState(), onEvent = {})
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedTransactionsScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreen(uiState = HomeUiState(), onEvent = {})
        }
    }
}
