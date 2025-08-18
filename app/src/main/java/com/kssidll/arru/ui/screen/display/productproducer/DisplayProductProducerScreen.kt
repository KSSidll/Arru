package com.kssidll.arru.ui.screen.display.productproducer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.data.data.ProductProducerEntity
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.ChartSource
import com.kssidll.arru.domain.data.loadedEmpty
import com.kssidll.arru.helper.generateRandomFloatValue
import com.kssidll.arru.ui.component.SpendingSummaryComponent
import com.kssidll.arru.ui.component.SpendingSummaryPeriod
import com.kssidll.arru.ui.component.TotalAverageAndMedianSpendingComponent
import com.kssidll.arru.ui.component.list.fullItemListContent
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

/**
 * @param onBack Called to request a back navigation
 * @param producer Producer for which the data is displayed
 * @param transactionItems Transaction items of [producer]
 * @param spentByTimeData Data list representing [producer] spending for current [spentByTimePeriod]
 * @param totalSpentData Value representing total [producer] spending
 * @param spentByTimePeriod Time period to get the [spentByTimeData] by
 * @param onSpentByTimePeriodSwitch Called to request [spentByTimePeriod] switch, Provides new
 *   period as argument
 * @param chartEntryModelProducer Model producer for [spentByTimeData] chart
 * @param onItemClick Callback called when the item is clicked. Provides product id as parameter
 * @param onItemCategoryClick Callback called when the item category label is clicked. Provides
 *   category id as parameter
 * @param onItemShopClick Callback called when the item shop label is clicked. Provides shop id as
 *   parameter
 * @param onItemLongClick Callback called when the item is long clicked/pressed. Provides item id as
 *   parameter
 * @param onEditAction Callback called when the 'edit' action is triggered
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayProductProducerScreen(
    onBack: () -> Unit,
    producer: ProductProducerEntity?,
    transactionItems: LazyPagingItems<Item>,
    spentByTimeData: ImmutableList<ChartSource>,
    totalSpentData: Float?,
    spentByTimePeriod: SpendingSummaryPeriod?,
    onSpentByTimePeriodSwitch: (SpendingSummaryPeriod) -> Unit,
    chartEntryModelProducer: CartesianChartModelProducer,
    onItemClick: (productId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
    onEditAction: () -> Unit,
) {
    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = { Text(text = producer?.name.orEmpty(), overflow = TextOverflow.Ellipsis) },
                actions = {
                    // 'edit' action
                    IconButton(onClick = { onEditAction() }) {
                        Icon(
                            imageVector = Icons.Rounded.Edit,
                            contentDescription = stringResource(R.string.edit),
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(27.dp),
                        )
                    }
                },
            )
        },
        contentWindowInsets =
            ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal),
        modifier =
            Modifier.windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
            ),
    ) { paddingValues ->
        Box(
            modifier =
                Modifier.padding(paddingValues).consumeWindowInsets(paddingValues).fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = transactionItems.loadedEmpty() && spentByTimeData.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(id = R.string.no_data_to_display_text),
                        textAlign = TextAlign.Center,
                        style = Typography.titleLarge,
                    )
                }
            }

            AnimatedVisibility(
                visible = transactionItems.itemCount != 0 || spentByTimeData.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                DisplayProductProducerScreenContent(
                    transactionItems = transactionItems,
                    spentByTimeData = spentByTimeData,
                    totalSpentData = totalSpentData,
                    spentByTimePeriod = spentByTimePeriod,
                    onSpentByTimePeriodSwitch = onSpentByTimePeriodSwitch,
                    chartEntryModelProducer = chartEntryModelProducer,
                    onItemClick = onItemClick,
                    onItemCategoryClick = onItemCategoryClick,
                    onItemShopClick = onItemShopClick,
                    onItemLongClick = onItemLongClick,
                )
            }
        }
    }
}

@Composable
private fun DisplayProductProducerScreenContent(
    transactionItems: LazyPagingItems<Item>,
    spentByTimeData: ImmutableList<ChartSource>,
    totalSpentData: Float?,
    spentByTimePeriod: SpendingSummaryPeriod?,
    onSpentByTimePeriodSwitch: (SpendingSummaryPeriod) -> Unit,
    chartEntryModelProducer: CartesianChartModelProducer,
    onItemClick: (productId: Long) -> Unit,
    onItemCategoryClick: (categoryId: Long) -> Unit,
    onItemShopClick: (shopId: Long) -> Unit,
    onItemLongClick: (itemId: Long) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val listState = rememberLazyListState()
    val firstVisibleItemIndex by remember { derivedStateOf { listState.firstVisibleItemIndex } }

    var previousFirstVisibleItemIndex by remember { mutableIntStateOf(0) }

    var returnActionButtonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(firstVisibleItemIndex) {
        if (
            previousFirstVisibleItemIndex > firstVisibleItemIndex + 1 && firstVisibleItemIndex >= 10
        ) {
            // scrolling up
            returnActionButtonVisible = true
            previousFirstVisibleItemIndex = firstVisibleItemIndex
        } else if (
            previousFirstVisibleItemIndex < firstVisibleItemIndex - 1 || firstVisibleItemIndex < 10
        ) {
            // scrolling down
            returnActionButtonVisible = false
            previousFirstVisibleItemIndex = firstVisibleItemIndex
        }
    }

    Scaffold(
        floatingActionButton = {
            AnimatedVisibility(
                visible = returnActionButtonVisible,
                enter =
                    slideInHorizontally(
                        animationSpec = tween(durationMillis = 300, easing = EaseOut),
                        initialOffsetX = { it },
                    ),
                exit =
                    slideOutHorizontally(
                        animationSpec = tween(durationMillis = 300, easing = EaseIn),
                        targetOffsetX = { it },
                    ),
            ) {
                FloatingActionButton(
                    onClick = { scope.launch { listState.animateScrollToItem(0) } },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ) {
                    Icon(imageVector = Icons.Rounded.ArrowUpward, contentDescription = null)
                }
            }
        },
        contentWindowInsets =
            ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.fillMaxWidth().padding(paddingValues).consumeWindowInsets(paddingValues),
        ) {
            item(contentType = "header") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.height(40.dp))

                    val totalSpent = totalSpentData ?: 0f

                    TotalAverageAndMedianSpendingComponent(
                        spentByTimeData = spentByTimeData,
                        totalSpentData = totalSpent,
                    )

                    Spacer(Modifier.height(28.dp))

                    AnimatedVisibility(visible = spentByTimeData.isNotEmpty()) {
                        SpendingSummaryComponent(
                            spentByTimeData = spentByTimeData,
                            spentByTimePeriod = spentByTimePeriod,
                            onSpentByTimePeriodUpdate = onSpentByTimePeriodSwitch,
                            columnChartEntryModelProducer = chartEntryModelProducer,
                        )

                        Spacer(Modifier.height(12.dp))
                    }
                }
            }

            fullItemListContent(
                transactionItems = transactionItems,
                onItemClick = {
                    // onItemClick(it.product.id)
                },
                onItemLongClick = {
                    // onItemLongClick(it.id)
                },
                onCategoryClick = {
                    // onItemCategoryClick(it.id)
                },
                onShopClick = {
                    // onItemShopClick(it.id)
                },
            )

            item { Box(modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)) }
        }
    }
}

@PreviewLightDark
@Composable
private fun DisplayProductProducerScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DisplayProductProducerScreen(
                onBack = {},
                producer = null,
                transactionItems =
                    flowOf(PagingData.from(Item.generateList())).collectAsLazyPagingItems(),
                spentByTimeData = ItemSpentChartData.generateList(),
                totalSpentData = generateRandomFloatValue(),
                spentByTimePeriod = SpendingSummaryPeriod.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = CartesianChartModelProducer(),
                onItemClick = {},
                onItemCategoryClick = {},
                onItemShopClick = {},
                onItemLongClick = {},
                onEditAction = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyDisplayProductProducerScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DisplayProductProducerScreen(
                onBack = {},
                producer = null,
                transactionItems =
                    flowOf(PagingData.from(emptyList<Item>())).collectAsLazyPagingItems(),
                spentByTimeData = emptyImmutableList(),
                totalSpentData = null,
                spentByTimePeriod = SpendingSummaryPeriod.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = CartesianChartModelProducer(),
                onItemClick = {},
                onItemCategoryClick = {},
                onItemShopClick = {},
                onItemLongClick = {},
                onEditAction = {},
            )
        }
    }
}

@PreviewExpanded
@Composable
private fun ExpandedDisplayProductProducerScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DisplayProductProducerScreen(
                onBack = {},
                producer = null,
                transactionItems =
                    flowOf(PagingData.from(Item.generateList())).collectAsLazyPagingItems(),
                spentByTimeData = ItemSpentChartData.generateList(),
                totalSpentData = generateRandomFloatValue(),
                spentByTimePeriod = SpendingSummaryPeriod.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = CartesianChartModelProducer(),
                onItemClick = {},
                onItemCategoryClick = {},
                onItemShopClick = {},
                onItemLongClick = {},
                onEditAction = {},
            )
        }
    }
}

@PreviewExpanded
@Composable
private fun ExpandedEmptyDisplayProductProducerScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DisplayProductProducerScreen(
                onBack = {},
                producer = null,
                transactionItems =
                    flowOf(PagingData.from(emptyList<Item>())).collectAsLazyPagingItems(),
                spentByTimeData = emptyImmutableList(),
                totalSpentData = null,
                spentByTimePeriod = SpendingSummaryPeriod.Month,
                onSpentByTimePeriodSwitch = {},
                chartEntryModelProducer = CartesianChartModelProducer(),
                onItemClick = {},
                onItemCategoryClick = {},
                onItemShopClick = {},
                onItemLongClick = {},
                onEditAction = {},
            )
        }
    }
}
