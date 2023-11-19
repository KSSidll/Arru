package com.kssidll.arrugarq.ui.screen.shop.shop

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
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.R
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.component.*
import com.kssidll.arrugarq.ui.component.list.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.*
import java.text.*
import java.util.*

/**
 * @param onBack Called to request a back navigation
 * @param state [ShopScreenState] instance representing the screen state
 * @param onShopEdit Called to request navigation to shop edition
 * @param onSpentByTimePeriodSwitch Called to request state period switch, with requested period as argument
 * @param requestMoreItems Called to request more transaction items to be added to the state
 * @param onProductSelect Called to request navigation to product, with requested product id as argument
 * @param onItemEdit Called to request navigation to item edition, with requested item id as argument
 * @param onCategorySelect Called to request navigation to category edition, with requested category id as argument
 * @param onProducerSelect Called to request navigation to producer, with requested producer id as argument
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ShopScreen(
    onBack: () -> Unit,
    state: ShopScreenState,
    onShopEdit: () -> Unit,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    requestMoreItems: () -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(
                        text = state.shop.value?.name.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onShopEdit()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(27.dp),
                        )
                    }
                },
            )
        }
    ) {
        Box(Modifier.padding(it)) {
            ShopScreenContent(
                state = state,
                onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                requestMoreItems = requestMoreItems,
                onProductSelect = onProductSelect,
                onItemEdit = onItemEdit,
                onCategorySelect = onCategorySelect,
                onProducerSelect = onProducerSelect,
            )
        }
    }
}

/**
 * [ShopScreen] content
 * @param state [ShopScreenState] instance representing the screen state
 * @param onSpentByTimePeriodSwitch Called to request state period switch, with requested period as argument
 * @param requestMoreItems Called to request more transaction items to be added to the state
 * @param onProductSelect Called to request navigation to product, with requested product id as argument
 * @param onItemEdit Called to request navigation to item edition, with requested item id as argument
 * @param onCategorySelect Called to request navigation to category edition, with requested category id as argument
 * @param onProducerSelect Called to request navigation to producer, with requested producer id as argument
 */
@Composable
internal fun ShopScreenContent(
    state: ShopScreenState,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    requestMoreItems: () -> Unit,
    onProductSelect: (productId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
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

        if (firstVisibleItemIndex + fullItemMaxPrefetchCount > state.items.size) {
            requestMoreItems()
        }
    }

    LaunchedEffect(state.items.size) {
        if (state.items.isEmpty()) {
            listState.scrollToItem(0)
        }
        grouppedItems.clear()
        grouppedItems.addAll(
            state.items.groupBy { it.embeddedItem.item.date / 86400000 }
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
            item {
                Column {
                    Spacer(Modifier.height(40.dp))

                    TotalAverageAndMedianSpendingComponent(
                        spentByTimeData = state.chartData.value.collectAsState(initial = emptyList()).value,
                        totalSpentData = state.totalSpentData.value.collectAsState(initial = 0F).value,
                    )

                    Spacer(Modifier.height(28.dp))

                    SpendingSummaryComponent(
                        modifier = Modifier.animateContentSize(),
                        spentByTimeData = state.chartData.value.collectAsState(initial = emptyList()).value,
                        spentByTimePeriod = state.spentByTimePeriod.value,
                        onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                        columnChartEntryModelProducer = state.columnChartEntryModelProducer,
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }

            grouppedItems.forEach { group ->
                item {
                    Column(
                        modifier = Modifier.fillParentMaxWidth()
                    ) {
                        Surface(
                            modifier = Modifier.fillParentMaxWidth(),
                            shape = RoundedCornerShape(
                                topStart = 24.dp,
                                topEnd = 24.dp
                            ),
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
                        onShopClick = {},
                        showShop = false,
                    )
                }
            }
        }
    }
}

@Preview(
    group = "Shop Screen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "Shop Screen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ShopScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ShopScreenContent(
                state = ShopScreenState(
                    items = generateRandomFullItemList().toMutableStateList(),
                    chartData = remember { mutableStateOf(generateRandomItemSpentByTimeListFlow()) }
                ),
                onSpentByTimePeriodSwitch = {},
                requestMoreItems = {},
                onProductSelect = {},
                onItemEdit = {},
                onCategorySelect = {},
                onProducerSelect = {},
            )
        }
    }
}
