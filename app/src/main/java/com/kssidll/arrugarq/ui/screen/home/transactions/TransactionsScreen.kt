package com.kssidll.arrugarq.ui.screen.home.transactions


import android.content.res.Configuration.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.utils.*
import com.kssidll.arrugarq.ui.component.list.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.*
import java.text.*
import java.util.*

/**
 * @param transactions List of transactions to display in the transactions list
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
    transactions: List<TransactionBasketWithItems>,
    onSearchAction: () -> Unit,
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

    LaunchedEffect(transactions.size) {
        if (transactions.isEmpty()) {
            listState.scrollToItem(0)
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
            modifier = Modifier
                .nestedScroll(scrollConnection)
                .padding(paddingValues)
                .padding(top = 12.dp),
        ) {
            items(transactions) { transaction ->
                var itemsVisible by remember {
                    mutableStateOf(false)
                }

                Column(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(bottom = 12.dp)
                        .clickable {
                            itemsVisible = !itemsVisible
                        }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(
                                    start = 20.dp,
                                    end = 8.dp
                                )
                        ) {
                            Text(
                                text = SimpleDateFormat(
                                    "d MMMM, yyyy",
                                    Locale.getDefault()
                                ).format(transaction.date),
                                style = Typography.headlineSmall,
                            )

                            Spacer(Modifier.height(5.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(
                                        start = 8.dp,
                                        end = 20.dp
                                    )
                            ) {
                                if (transaction.shop != null) {
                                    Button(
                                        onClick = {
                                            onItemShopClick(transaction.shop.id)
                                        },
                                        contentPadding = PaddingValues(
                                            vertical = 0.dp,
                                            horizontal = 12.dp
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.tertiary,
                                            contentColor = MaterialTheme.colorScheme.onTertiary,
                                        ),
                                    ) {
                                        Text(
                                            text = transaction.shop.name,
                                            textAlign = TextAlign.Center,
                                            style = Typography.labelMedium,
                                        )
                                        Icon(
                                            imageVector = Icons.Rounded.Store,
                                            contentDescription = null,
                                            modifier = Modifier.size(17.dp),
                                        )
                                    }
                                }
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                end = 20.dp,
                            )
                        ) {
                            Text(
                                text = transaction.totalCost.toFloat()
                                    .div(TransactionBasket.COST_DIVISOR)
                                    .formatToCurrency(),
                                style = Typography.titleLarge,
                            )

                            Spacer(Modifier.width(5.dp))

                            Icon(
                                imageVector = Icons.Outlined.Payment,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = MaterialTheme.colorScheme.tertiary,
                            )
                        }
                    }

                    AnimatedVisibility(visible = itemsVisible) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = ShapeDefaults.Medium
                        ) {
                            Column {
                                transaction.items.forEach { item ->
                                    FullItemCard(
                                        item = item,
                                        onItemClick = {
                                            onItemClick(it.product.id)
                                        },
                                        onItemLongClick = {
                                            onItemLongClick(it.id)
                                        },
                                        onCategoryClick = {
                                            onItemCategoryClick(it.id)
                                        },
                                        onProducerClick = {
                                            onItemProducerClick(it.id)
                                        },
                                    )
                                }
                            }
                        }
                    }

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
                transactions = TransactionBasketWithItems.generateList(),
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
