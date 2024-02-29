package com.kssidll.arru.ui.screen.display.product


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
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
import com.kssidll.arru.ui.component.chart.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.theme.*
import com.patrykandpatrick.vico.core.entry.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * @param onBack Called to request a back navigation
 * @param product Product for which the data is displayed
 * @param transactionItems Transaction items of [product]
 * @param spentByTimeData Data list representing [product] spending for current [spentByTimePeriod]
 * @param productPriceByShopByTimeData Data list representing [product] price per shop in time
 * @param totalSpentData Value representing total [product] spending
 * @param spentByTimePeriod Time period to get the [spentByTimeData] by
 * @param onSpentByTimePeriodSwitch Called to request [spentByTimePeriod] switch, Provides new period as argument
 * @param chartEntryModelProducer Model producer for [spentByTimeData] chart
 * @param onItemCategoryClick Callback called when the item category label is clicked. Provides category id as parameter
 * @param onItemProducerClick Callback called when the item producer label is clicked. Provides producer id as parameter
 * @param onItemShopClick Callback called when the item shop label is clicked. Provides shop id as parameter
 * @param onItemLongClick Callback called when the item is long clicked/pressed. Provides item id as parameter
 * @param onEditAction Callback called when the 'edit' action is triggered
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductScreen(
    onBack: () -> Unit,
    product: Product?,
    transactionItems: LazyPagingItems<FullItem>,
    spentByTimeData: Data<List<ItemSpentByTime>>,
    productPriceByShopByTimeData: Data<List<ProductPriceByShopByTime>>,
    totalSpentData: Data<Float?>,
    spentByTimePeriod: TimePeriodFlowHandler.Periods?,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    chartEntryModelProducer: ChartEntryModelProducer,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemProducerClick: (producerId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onEditAction: () -> Unit,
) {
    Box {
        AnimatedVisibility(
            visible = transactionItems.loadedEmpty() && spentByTimeData.loadedEmpty(),
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.no_data_to_display_text),
                    textAlign = TextAlign.Center,
                    style = Typography.titleLarge,
                )
            }
        }

        AnimatedVisibility(
            visible = transactionItems.itemCount != 0 || spentByTimeData.loadedData(),
            enter = fadeIn(),
            exit = fadeOut(),
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
                                text = product?.name.orEmpty(),
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        actions = {
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

                            val chartData = if (spentByTimeData is Data.Loaded) {
                                spentByTimeData.data
                            } else emptyList()

                            val totalSpent = if (totalSpentData is Data.Loaded) {
                                totalSpentData.data ?: 0f
                            } else 0f

                            TotalAverageAndMedianSpendingComponent(
                                spentByTimeData = chartData,
                                totalSpentData = totalSpent,
                            )

                            Spacer(Modifier.height(28.dp))

                            AnimatedVisibility(visible = spentByTimeData.loadedData()) {
                                if (spentByTimeData is Data.Loaded) {
                                    SpendingSummaryComponent(
                                        spentByTimeData = spentByTimeData.data,
                                        spentByTimePeriod = spentByTimePeriod,
                                        onSpentByTimePeriodUpdate = onSpentByTimePeriodSwitch,
                                        columnChartEntryModelProducer = chartEntryModelProducer,
                                    )

                                    Spacer(Modifier.height(12.dp))
                                }
                            }

                            AnimatedVisibility(visible = productPriceByShopByTimeData.loadedData()) {
                                if (productPriceByShopByTimeData is Data.Loaded) {
                                    ShopPriceCompareChart(
                                        items = productPriceByShopByTimeData.data,
                                    )

                                    Spacer(Modifier.height(12.dp))
                                }
                            }
                        }
                    }

                    fullItemListContent(
                        transactionItems = transactionItems,
                        onItemLongClick = {
                            onItemLongClick(it.id)
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

@PreviewLightDark
@Composable
fun ProductScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProductScreen(
                onBack = {},
                product = null,
                transactionItems = flowOf(PagingData.from(FullItem.generateList())).collectAsLazyPagingItems(),
                spentByTimeData = Data.Loaded(ItemSpentByTime.generateList()),
                productPriceByShopByTimeData = Data.Loaded(ProductPriceByShopByTime.generateList()),
                totalSpentData = Data.Loaded(generateRandomFloatValue()),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = ChartEntryModelProducer(),
                onItemCategoryClick = {},
                onItemProducerClick = {},
                onItemShopClick = {},
                onItemLongClick = {},
                onEditAction = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
fun EmptyProductScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProductScreen(
                onBack = {},
                product = null,
                transactionItems = flowOf(PagingData.from(emptyList<FullItem>())).collectAsLazyPagingItems(),
                spentByTimeData = Data.Loading(),
                productPriceByShopByTimeData = Data.Loading(),
                totalSpentData = Data.Loaded(generateRandomFloatValue()),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = ChartEntryModelProducer(),
                onItemCategoryClick = {},
                onItemProducerClick = {},
                onItemShopClick = {},
                onItemLongClick = {},
                onEditAction = {},
            )
        }
    }
}

@PreviewExpanded
@Composable
fun ExpandedProductScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProductScreen(
                onBack = {},
                product = null,
                transactionItems = flowOf(PagingData.from(FullItem.generateList())).collectAsLazyPagingItems(),
                spentByTimeData = Data.Loaded(ItemSpentByTime.generateList()),
                productPriceByShopByTimeData = Data.Loaded(ProductPriceByShopByTime.generateList()),
                totalSpentData = Data.Loaded(generateRandomFloatValue()),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = ChartEntryModelProducer(),
                onItemCategoryClick = {},
                onItemProducerClick = {},
                onItemShopClick = {},
                onItemLongClick = {},
                onEditAction = {},
            )
        }
    }
}

@PreviewExpanded
@Composable
fun ExpandedEmptyProductScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            ProductScreen(
                onBack = {},
                product = null,
                transactionItems = flowOf(PagingData.from(emptyList<FullItem>())).collectAsLazyPagingItems(),
                spentByTimeData = Data.Loading(),
                productPriceByShopByTimeData = Data.Loading(),
                totalSpentData = Data.Loaded(generateRandomFloatValue()),
                spentByTimePeriod = TimePeriodFlowHandler.Periods.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = ChartEntryModelProducer(),
                onItemCategoryClick = {},
                onItemProducerClick = {},
                onItemShopClick = {},
                onItemLongClick = {},
                onEditAction = {},
            )
        }
    }
}
