package com.kssidll.arrugarq.ui.screen.home.transactions


import android.content.res.Configuration.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
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
 * @param onProductSelect Callback called as request to navigate to product, Provides product id as parameter
 * @param onCategorySelect Callback called as request to navigate to categary, Provides category id as parameter
 * @param onProducerSelect Callback called as request to navigate to producer, Provides producer id as parameter
 * @param onShopSelect Callback called as request to navigate to shop, Provides shop id as parameter
 */
@Composable
internal fun TransactionsScreen(
    requestMoreItems: () -> Unit,
    items: List<FullItem>,
    onItemEdit: (itemId: Long) -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
) {
    TransactionsScreenContent(
        requestMoreItems = requestMoreItems,
        items = items,
        onProductSelect = onProductSelect,
        onItemEdit = onItemEdit,
        onCategorySelect = onCategorySelect,
        onProducerSelect = onProducerSelect,
        onShopSelect = onShopSelect,
    )
}

/**
 * [TransactionsScreen] content
 * @param requestMoreItems Callback called as request to append more items to [items]
 * @param items List of items to display in the transactions list
 * @param onProductSelect Callback called as request to navigate to product, Provides product id as parameter
 * @param onCategorySelect Callback called as request to navigate to categary, Provides category id as parameter
 * @param onProducerSelect Callback called as request to navigate to producer, Provides producer id as parameter
 * @param onShopSelect Callback called as request to navigate to shop, Provides shop id as parameter
 */
@Composable
private fun TransactionsScreenContent(
    requestMoreItems: () -> Unit,
    items: List<FullItem>,
    onItemEdit: (itemId: Long) -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
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
            modifier = Modifier.padding(paddingValues),
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
                                        "MMM d, yyyy",
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
                            onProductSelect(it.embeddedProduct.product.id)
                        },
                        onItemLongClick = {
                            onItemEdit(it.embeddedItem.item.id)
                        },
                        onCategoryClick = {
                            onCategorySelect(it.id)
                        },
                        onProducerClick = {
                            onProducerSelect(it.id)
                        },
                        onShopClick = {
                            onShopSelect(it.id)
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
            TransactionsScreenContent(
                requestMoreItems = {},
                items = generateRandomFullItemList(
                    itemDateTimeFrom = Date.valueOf("2022-06-01").time,
                    itemDateTimeUntil = Date.valueOf("2022-06-04").time,
                ),
                onItemEdit = {},
                onProducerSelect = {},
                onCategorySelect = {},
                onProductSelect = {},
                onShopSelect = {},
            )
        }
    }
}
