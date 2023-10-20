package com.kssidll.arrugarq.ui.screen.product


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
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.component.*
import com.kssidll.arrugarq.ui.component.chart.*
import com.kssidll.arrugarq.ui.component.list.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.*
import java.text.*
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ProductScreen(
    onBack: () -> Unit,
    state: ProductScreenState,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    requestMoreItems: () -> Unit,
    onCategoryClick: (category: ProductCategory) -> Unit,
    onProducerClick: (producer: ProductProducer) -> Unit,
    onShopClick: (shop: Shop) -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(
                        text = state.product.value?.name.orEmpty(),
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    ) {
        Box(Modifier.padding(it)) {
            ProductScreenContent(
                state = state,
                onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                requestMoreItems = requestMoreItems,
                onCategoryClick = onCategoryClick,
                onProducerClick = onProducerClick,
                onShopClick = onShopClick,
            )
        }
    }
}

@Composable
internal fun ProductScreenContent(
    state: ProductScreenState,
    onSpentByTimePeriodSwitch: (TimePeriodFlowHandler.Periods) -> Unit,
    requestMoreItems: () -> Unit,
    onCategoryClick: (category: ProductCategory) -> Unit,
    onProducerClick: (producer: ProductProducer) -> Unit,
    onShopClick: (shop: Shop) -> Unit,
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
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(it),
        ) {
            item {
                Column {
                    Spacer(Modifier.height(40.dp))

                    TotalAverageAndMedianSpendingComponent(
                        spentByTimeData = state.chartData.value.collectAsState(initial = emptyList()).value,
                        totalSpentData = state.totalSpentData.floatValue,
                    )

                    Spacer(Modifier.height(28.dp))

                    SpendingSummaryComponent(
                        modifier = Modifier.animateContentSize(),
                        spentByTimeData = state.chartData.value.collectAsState(initial = emptyList()).value,
                        spentByTimePeriod = state.spentByTimePeriod.value,
                        onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                        columnChartEntryModelProducer = state.columnChartEntryModelProducer,
                        runInitialAnimation = !state.finishedChartAnimation
                    )

                    state.finishedChartAnimation = true

                    Spacer(Modifier.height(12.dp))

                    ShopPriceCompareChart(
                        items = state.productPriceByShopByTimeItems.toList(),
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
                        itemClickable = false,
                        onCategoryClick = onCategoryClick,
                        onProducerClick = onProducerClick,
                        onShopClick = onShopClick,
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
                state = ProductScreenState(
                    items = generateRandomFullItemList().toMutableStateList(),
                    chartData = remember { mutableStateOf(generateRandomItemSpentByTimeListFlow()) }
                ),
                onSpentByTimePeriodSwitch = {},
                requestMoreItems = {},
                onCategoryClick = {},
                onProducerClick = {},
                onShopClick = {},
            )
        }
    }
}
