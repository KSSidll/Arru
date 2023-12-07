package com.kssidll.arrugarq.ui.screen.home.transactions


import android.content.res.Configuration.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.res.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.component.list.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.*
import java.sql.Date
import java.text.*
import java.util.*

/**
 * @param requestMoreItems Callback called as request to append more items to [items]
 * @param items List of items to display in the transactions list
 * @param onSearchAction Callback called when the 'search' action is triggered
 * @param onItemClick Callback called when the transaction item is clicked. Provides product id as parameter
 * @param onItemLongClick Callback called when the transaction item is long clicked/pressed. Provides item id as parameter
 * @param onItemCategoryClick Callback called when the transaction item category label is clicked. Provides category id as parameter
 * @param onItemProducerClick Callback called when the transaction item producer label is clicked. Provides producer id as parameter
 * @param onItemShopClick Callback called when the transaction item shop label is clicked. Provides shop id as parameter
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TransactionsScreen(
    requestMoreItems: () -> Unit,
    items: List<FullItem>,
    onSearchAction: () -> Unit,
    onItemClick: (productId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val grouppedItems: SnapshotStateList<Pair<Long, List<FullItem>>> =
        remember { mutableStateListOf() }

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

        if (firstVisibleItemIndex + fullItemMaxPrefetchCount > items.size) {
            requestMoreItems()
        }
    }

    LaunchedEffect(items.size) {
        if (items.isEmpty()) {
            listState.scrollToItem(0)
        }
        grouppedItems.clear()
        grouppedItems.addAll(
            items.groupBy { it.embeddedItem.item.date / 86400000 }
                .toList()
                .sortedByDescending { it.first })
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
            modifier = Modifier
                .nestedScroll(scrollConnection)
                .padding(paddingValues),
        ) {
            grouppedItems.forEachIndexed { index, group ->
                item {
                    Column(
                        modifier = Modifier.fillParentMaxWidth()
                    ) {
                        val shape = if (index != 0) RoundedCornerShape(
                            topStart = 24.dp,
                            topEnd = 24.dp
                        )
                        else RectangleShape

                        Surface(
                            modifier = Modifier.fillParentMaxWidth(),
                            shape = shape,
                            color = MaterialTheme.colorScheme.surfaceContainer,
                        ) {
                            Box(
                                Modifier
                                    .fillParentMaxWidth()
                                    .padding(vertical = 8.dp)
                            ) {
                                Text(
                                    modifier = Modifier.align(Alignment.Center),
                                    text = SimpleDateFormat(
                                        "d MMMM, yyyy",
                                        Locale.getDefault()
                                    ).format(group.first * 86400000),
                                    style = Typography.headlineMedium,
                                )
                            }
                        }
                    }
                }

                items(group.second) { item ->
                    FullItemCard(
                        fullItem = item,
                        onItemClick = {
                            onItemClick(it.embeddedProduct.product.id)
                        },
                        onItemLongClick = {
                            onItemLongClick(it.embeddedItem.item.id)
                        },
                        onCategoryClick = {
                            onItemCategoryClick(it.id)
                        },
                        onProducerClick = {
                            onItemProducerClick(it.id)
                        },
                        onShopClick = {
                            onItemShopClick(it.id)
                        },
                    )
                }
            }
        }
    }
}

@Preview(
    group = "TransactionsScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "TransactionsScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun TransactionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreen(
                requestMoreItems = {},
                items = generateRandomFullItemList(
                    itemDateTimeFrom = Date.valueOf("2022-06-01").time,
                    itemDateTimeUntil = Date.valueOf("2022-06-04").time,
                ),
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
