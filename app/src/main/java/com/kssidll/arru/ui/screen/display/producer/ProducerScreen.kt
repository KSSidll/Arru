package com.kssidll.arru.ui.screen.display.producer


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import androidx.paging.*
import androidx.paging.compose.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.helper.*
import com.kssidll.arru.ui.component.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.theme.*
import com.patrykandpatrick.vico.core.entry.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.text.*
import java.util.*

/**
 * @param onBack Called to request a back navigation
 * @param producer Producer for which the data is displayed
 * @param transactionItems Transaction items of [producer]
 * @param spentByTimeData Data list representing [producer] spending for current [spentByTimePeriod]
 * @param totalSpentData Value representing total [producer] spending
 * @param spentByTimePeriod Time period to get the [spentByTimeData] by
 * @param onSpentByTimePeriodSwitch Called to request [spentByTimePeriod] switch, Provides new period as argument
 * @param chartEntryModelProducer Model producer for [spentByTimeData] chart
 * @param onItemClick Callback called when the item is clicked. Provides product id as parameter
 * @param onItemCategoryClick Callback called when the item category label is clicked. Provides category id as parameter
 * @param onItemShopClick Callback called when the item shop label is clicked. Provides shop id as parameter
 * @param onItemLongClick Callback called when the item is long clicked/pressed. Provides item id as parameter
 * @param onEditAction Callback called when the 'edit' action is triggered
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProducerScreen(
    onBack: () -> Unit,
    producer: ProductProducer?,
    transactionItems: LazyPagingItems<FullItem>,
    spentByTimeData: List<ChartSource>,
    totalSpentData: Float,
    spentByTimePeriod: TimePeriodFlowHandler.Periods?,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    chartEntryModelProducer: ChartEntryModelProducer,
    onItemClick: (productId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onEditAction: () -> Unit,
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
                    // 'edit' action
                    IconButton(
                        onClick = {
                            onEditAction()
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
                .fillMaxWidth()
                .padding(paddingValues)
        ) {
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                        onSpentByTimePeriodUpdate = onSpentByTimePeriodSwitch,
                        columnChartEntryModelProducer = chartEntryModelProducer,
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }

            items(
                transactionItems.itemCount,
                key = transactionItems.itemKey { it.id }
            ) { index ->
                val item = transactionItems[index]

                if (item != null) {
                    Column(modifier = Modifier.widthIn(max = 600.dp)) {
                        //... yeah
                        if (index == 0 || (transactionItems[index - 1] != null && item.date / 86400000 != transactionItems[index - 1]!!.date / 86400000)) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Surface(
                                    shape = RoundedCornerShape(
                                        topStart = 24.dp,
                                        topEnd = 24.dp
                                    ),
                                    color = MaterialTheme.colorScheme.surfaceContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                    ) {
                                        Text(
                                            modifier = Modifier.align(Alignment.Center),
                                            text = SimpleDateFormat(
                                                "MMM d, yyyy",
                                                Locale.getDefault()
                                            ).format(item.date),
                                            style = Typography.headlineMedium,
                                        )
                                    }
                                }
                            }
                        }

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
                            onShopClick = {
                                onItemShopClick(it.id)
                            },
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@PreviewExpanded
@Composable
fun ProducerScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProducerScreen(
                onBack = {},
                producer = null,
                transactionItems = flowOf(PagingData.from(FullItem.generateList())).collectAsLazyPagingItems(),
                spentByTimeData = ItemSpentByTime.generateList(),
                totalSpentData = generateRandomFloatValue(),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = ChartEntryModelProducer(),
                onItemClick = {},
                onItemCategoryClick = {},
                onItemShopClick = {},
                onItemLongClick = {},
                onEditAction = {},
            )
        }
    }
}