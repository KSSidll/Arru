package com.kssidll.arrugarq.ui.screen.home.transactions

import android.content.res.*
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
import com.kssidll.arrugarq.ui.screen.home.transactions.component.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.*
import java.sql.Date
import java.text.*
import java.util.*

private const val pageSize = 25 // called twice when scrolling, so effectively 50

/**
 * @param requestItems: Function called when the screen request more items with param count,
 * which the new expected size of the item list, is never lower than previous requests
 * but can request the same amount several times
 */
@Composable
fun TransactionsScreen(
    requestItems: (count: Int) -> Unit,
    items: List<FullItem>,
) {
    TransactionsScreenContent(
        requestItems = requestItems,
        items = items,
    )
}

@Composable
private fun TransactionsScreenContent(
    requestItems: (count: Int) -> Unit,
    items: List<FullItem>,
) {
    val scope = rememberCoroutineScope()
    val grouppedItems: SnapshotStateList<Pair<Long, List<FullItem>>> =
        remember { mutableStateListOf() }

    val listState = rememberLazyListState()
    val firstItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    LaunchedEffect(firstItemIndex + pageSize > items.size) {
        requestItems(items.size + pageSize)
    }

    LaunchedEffect(items.size) {
        grouppedItems.clear()
        grouppedItems.addAll(
            items.groupBy { it.embeddedItem.item.date / 86400000 }
                .toList()
                .sortedByDescending { it.first })
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = firstItemIndex >= 10,
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
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(it),
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

                items(group.second) { embeddedItem ->
                    TransactionItem(embeddedItem)
                }
            }
        }
    }
}

@Preview(
    group = "Transactions Screen",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Transactions Screen",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun TransactionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreenContent(
                requestItems = {},
                items = generateRandomFullItemList(
                    itemDateTimeFrom = Date.valueOf("2022-06-01").time,
                    itemDateTimeUntil = Date.valueOf("2022-06-04").time,
                ),
            )
        }
    }
}
