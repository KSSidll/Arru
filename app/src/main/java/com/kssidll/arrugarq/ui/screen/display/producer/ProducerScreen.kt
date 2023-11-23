package com.kssidll.arrugarq.ui.screen.display.producer


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
import com.patrykandpatrick.vico.core.entry.*
import kotlinx.coroutines.*
import java.text.*
import java.util.*

/**
 * @param onBack Called to request a back navigation
 * @param producer Producer for which the data is displayed
 * @param transactionItems List of transaction items of [producer]
 * @param requestMoreTransactionItems Called to request more transaction items to be added to [transactionItems]
 * @param spentByTimeData Data list representing [producer] spending for current [spentByTimePeriod]
 * @param totalSpentData Value representing total [producer] spending
 * @param spentByTimePeriod Time period to get the [spentByTimeData] by
 * @param onSpentByTimePeriodSwitch Called to request [spentByTimePeriod] switch, Provides new period as argument
 * @param chartEntryModelProducer Model producer for [spentByTimeData] chart
 * @param onProductSelect Called to request navigation to product. Provides requested product id as argument
 * @param onCategorySelect Called to request navigation to category. Provides requested category id as argument
 * @param onShopSelect Called to request navigation to shop. Provides requested shop id as argument
 * @param onItemEdit Called to request navigation to item edition. Provides requested item id as argument
 * @param onProducerEdit Called to request navigation to [producer] edition
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProducerScreen(
    onBack: () -> Unit,
    producer: ProductProducer?,
    transactionItems: List<FullItem>,
    requestMoreTransactionItems: () -> Unit,
    spentByTimeData: List<ItemSpentByTime>,
    totalSpentData: Float,
    spentByTimePeriod: TimePeriodFlowHandler.Periods?,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    chartEntryModelProducer: ChartEntryModelProducer,
    onProductSelect: (productId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
    onShopSelect: (shopId: Long) -> Unit,
    onItemEdit: (itemId: Long) -> Unit,
    onProducerEdit: () -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(
                        text = producer?.name.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    IconButton(
                        onClick = {
                            onProducerEdit()
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
            ProducerScreenContent(
                transactionItems = transactionItems,
                requestMoreTransactionItems = requestMoreTransactionItems,
                spentByTimeData = spentByTimeData,
                totalSpentData = totalSpentData,
                spentByTimePeriod = spentByTimePeriod,
                onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                chartEntryModelProducer = chartEntryModelProducer,
                onProductSelect = onProductSelect,
                onCategorySelect = onCategorySelect,
                onShopSelect = onShopSelect,
                onItemEdit = onItemEdit,
            )
        }
    }
}

/**
 * [ProducerScreen] content
 * @param transactionItems List of transaction items of producer
 * @param requestMoreTransactionItems Called to request more transaction items to be added to [transactionItems]
 * @param spentByTimeData Data list representing producer spending for current [spentByTimePeriod]
 * @param totalSpentData Value representing total producer spending
 * @param spentByTimePeriod Time period to get the [spentByTimeData] by
 * @param onSpentByTimePeriodSwitch Called to request [spentByTimePeriod] switch, Provides new period as argument
 * @param chartEntryModelProducer Model producer for [spentByTimeData] chart
 * @param onProductSelect Called to request navigation to product. Provides requested product id as argument
 * @param onCategorySelect Called to request navigation to category. Provides requested category id as argument
 * @param onShopSelect Called to request navigation to shop. Provides requested shop id as argument
 * @param onItemEdit Called to request navigation to item edition. Provides requested item id as argument
 */
@Composable
internal fun ProducerScreenContent(
    transactionItems: List<FullItem>,
    requestMoreTransactionItems: () -> Unit,
    spentByTimeData: List<ItemSpentByTime>,
    totalSpentData: Float,
    spentByTimePeriod: TimePeriodFlowHandler.Periods?,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    chartEntryModelProducer: ChartEntryModelProducer,
    onProductSelect: (productId: Long) -> Unit,
    onCategorySelect: (categoryId: Long) -> Unit,
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
                        onProducerClick = {},
                        onShopClick = {
                            onShopSelect(it.id)
                        },
                        showProducer = false,
                    )
                }
            }
        }
    }
}

@Preview(
    group = "ProducerScreen",
    name = "Dark",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_YES
)
@Preview(
    group = "ProducerScreen",
    name = "Light",
    showBackground = true,
    uiMode = UI_MODE_NIGHT_NO
)
@Composable
fun ProducerScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProducerScreenContent(
                transactionItems = generateRandomFullItemList(),
                requestMoreTransactionItems = {},
                spentByTimeData = generateRandomItemSpentByTimeList(),
                totalSpentData = generateRandomFloatValue(),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = ChartEntryModelProducer(),
                onProductSelect = {},
                onCategorySelect = {},
                onShopSelect = {},
                onItemEdit = {},
            )
        }
    }
}
