package com.kssidll.arrugarq.ui.screen.display.product


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
import com.kssidll.arrugarq.ui.component.chart.*
import com.kssidll.arrugarq.ui.component.list.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*
import com.patrykandpatrick.vico.core.entry.*
import kotlinx.coroutines.*
import java.text.*
import java.util.*

/**
 * @param onBack Called to request a back navigation
 * @param product Product for which the data is displayed
 * @param transactionItems List of transaction items of [product]
 * @param requestMoreTransactionItems Called to request more transaction items to be added to [transactionItems]
 * @param spentByTimeData Data list representing [product] spending for current [spentByTimePeriod]
 * @param productPriceByShopByTimeData Data list representing [product] price per shop in time
 * @param totalSpentData Value representing total [product] spending
 * @param spentByTimePeriod Time period to get the [spentByTimeData] by
 * @param onSpentByTimePeriodSwitch Called to request [spentByTimePeriod] switch, Provides new period as argument
 * @param chartEntryModelProducer Model producer for [spentByTimeData] chart
 * @param onCategorySelect Called to request navigation to category edition, with requested category id as argument
 * @param onProducerSelect Called to request navigation to producer, with requested producer id as argument
 * @param onShopSelect Called to request navigation to shop, with requested shop id as argument
 * @param onItemEdit Called to request navigation to item edition, with requested item id as argument
 * @param onProductEdit Called to request navigation to product edition
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductScreen(
    onBack: () -> Unit,
    product: Product?,
    transactionItems: List<FullItem>,
    requestMoreTransactionItems: () -> Unit,
    spentByTimeData: List<ItemSpentByTime>,
    productPriceByShopByTimeData: List<ProductPriceByShopByTime>,
    totalSpentData: Float,
    spentByTimePeriod: TimePeriodFlowHandler.Periods?,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    chartEntryModelProducer: ChartEntryModelProducer,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onProductEdit: () -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(
                        text = product?.name.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onProductEdit()
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
            ProductScreenContent(
                transactionItems = transactionItems,
                requestMoreTransactionItems = requestMoreTransactionItems,
                spentByTimeData = spentByTimeData,
                productPriceByShopByTimeData = productPriceByShopByTimeData,
                totalSpentData = totalSpentData,
                spentByTimePeriod = spentByTimePeriod,
                onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                chartEntryModelProducer = chartEntryModelProducer,
                onCategorySelect = onCategorySelect,
                onProducerSelect = onProducerSelect,
                onShopSelect = onShopSelect,
                onItemEdit = onItemEdit,
            )
        }
    }
}

/**
 * [ProductScreen] content
 * @param transactionItems List of transaction items of product
 * @param requestMoreTransactionItems Called to request more transaction items to be added to [transactionItems]
 * @param spentByTimeData Data list representing product spending for current [spentByTimePeriod]
 * @param productPriceByShopByTimeData Data list representing product price per shop in time
 * @param totalSpentData Value representing total product spending
 * @param spentByTimePeriod Time period to get the [spentByTimeData] by
 * @param onSpentByTimePeriodSwitch Called to request [spentByTimePeriod] switch, Provides new period as argument
 * @param chartEntryModelProducer Model producer for [spentByTimeData] chart
 * @param onCategorySelect Called to request navigation to category edition, with requested category id as argument
 * @param onProducerSelect Called to request navigation to producer, with requested producer id as argument
 * @param onShopSelect Called to request navigation to shop, with requested shop id as argument
 * @param onItemEdit Called to request navigation to item edition, with requested item id as argument
 */
@Composable
internal fun ProductScreenContent(
    transactionItems: List<FullItem>,
    requestMoreTransactionItems: () -> Unit,
    spentByTimeData: List<ItemSpentByTime>,
    productPriceByShopByTimeData: List<ProductPriceByShopByTime>,
    totalSpentData: Float,
    spentByTimePeriod: TimePeriodFlowHandler.Periods?,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    chartEntryModelProducer: ChartEntryModelProducer,
    onCategorySelect: (categoryId: Long) -> Unit,
    onProducerSelect: (producerId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
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

        if (firstVisibleItemIndex + fullItemMaxPrefetchCount > transactionItems.size) {
            requestMoreTransactionItems()
        }
    }

    LaunchedEffect(transactionItems.size) {
        if (transactionItems.isEmpty()) {
            listState.scrollToItem(0)
        }
        grouppedItems.clear()
        grouppedItems.addAll(
            transactionItems.groupBy { it.embeddedItem.item.date / 86400000 }
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
                        spentByTimeData = spentByTimeData,
                        totalSpentData = totalSpentData,
                    )

                    Spacer(Modifier.height(28.dp))

                    SpendingSummaryComponent(
                        modifier = Modifier.animateContentSize(),
                        spentByTimeData = spentByTimeData,
                        spentByTimePeriod = spentByTimePeriod,
                        onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                        columnChartEntryModelProducer = chartEntryModelProducer,
                    )

                    Spacer(Modifier.height(12.dp))

                    ShopPriceCompareChart(
                        items = productPriceByShopByTimeData,
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
                        onItemClick = {},
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
    group = "ProductScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ProductScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ProductScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProductScreenContent(
                transactionItems = generateRandomFullItemList(),
                requestMoreTransactionItems = {},
                spentByTimeData = generateRandomItemSpentByTimeList(),
                productPriceByShopByTimeData = generateRandomProductPriceByShopByTimeList(),
                totalSpentData = generateRandomFloatValue(),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = ChartEntryModelProducer(),
                onCategorySelect = {},
                onProducerSelect = {},
                onShopSelect = {},
                onItemEdit = {},
            )
        }
    }
}
