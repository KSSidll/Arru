package com.kssidll.arru.ui.screen.display.shop

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.R
import com.kssidll.arru.data.view.Item
import com.kssidll.arru.domain.data.data.ItemSpentChartData
import com.kssidll.arru.domain.data.loadedEmpty
import com.kssidll.arru.helper.generateRandomFloatValue
import com.kssidll.arru.ui.component.SpendingSummaryComponent
import com.kssidll.arru.ui.component.TotalAverageAndMedianSpendingComponent
import com.kssidll.arru.ui.component.list.itemList
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.Typography
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DisplayShopScreen(
    uiState: DisplayShopUiState,
    onEvent: (event: DisplayShopEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = uiState.items.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = { onEvent(DisplayShopEvent.NavigateBack) },
                title = { Text(text = uiState.shopName, overflow = TextOverflow.Ellipsis) },
                actions = {
                    // 'edit' action
                    IconButton(onClick = { onEvent(DisplayShopEvent.NavigateEditShop) }) {
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
            modifier.windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
            ),
    ) { paddingValues ->
        Box(
            modifier =
                Modifier.padding(paddingValues).consumeWindowInsets(paddingValues).fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = items.loadedEmpty() && uiState.spentByTime.isEmpty(),
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
                visible = items.itemCount != 0 || uiState.spentByTime.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                DisplayShopScreenContent(uiState, items, onEvent)
            }
        }
    }
}

@Composable
private fun DisplayShopScreenContent(
    uiState: DisplayShopUiState,
    items: LazyPagingItems<Item>,
    onEvent: (event: DisplayShopEvent) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val firstVisibleItemIndex by remember {
        derivedStateOf { uiState.listState.firstVisibleItemIndex }
    }

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
                    onClick = { scope.launch { uiState.listState.animateScrollToItem(0) } },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier =
                        Modifier.windowInsetsPadding(
                            WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
                        ),
                ) {
                    Icon(imageVector = Icons.Rounded.ArrowUpward, contentDescription = null)
                }
            }
        },
        contentWindowInsets =
            ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal),
    ) { paddingValues ->
        LazyColumn(
            state = uiState.listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.fillMaxWidth().padding(paddingValues).consumeWindowInsets(paddingValues),
        ) {
            item(contentType = "header") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(Modifier.height(40.dp))

                    TotalAverageAndMedianSpendingComponent(
                        totalChartEntryModelProducer = uiState.totalChartEntryModelProducer,
                        totalSpentValue = uiState.totalSpentValue,
                        averageChartEntryModelProducer = uiState.averageChartEntryModelProducer,
                        averageSpentValue = uiState.averageSpentValue,
                        medianChartEntryModelProducer = uiState.medianChartEntryModelProducer,
                        medianSpentValue = uiState.medianSpentValue,
                    )

                    Spacer(Modifier.height(28.dp))

                    AnimatedVisibility(visible = uiState.spentByTime.isNotEmpty()) {
                        SpendingSummaryComponent(
                            spentByTimeData = uiState.spentByTime,
                            spentByTimePeriod = uiState.spentByTimePeriod,
                            onSpentByTimePeriodUpdate = {
                                onEvent(DisplayShopEvent.SetSpentByTimePeriod(it))
                            },
                            columnChartEntryModelProducer = uiState.chartEntryModelProducer,
                        )
                    }

                    Spacer(Modifier.height(12.dp))
                }
            }

            itemList(
                transactionItems = items,
                onItemClick = { onEvent(DisplayShopEvent.NavigateDisplayProduct(it.productId)) },
                onItemLongClick = { onEvent(DisplayShopEvent.NavigateEditItem(it.id)) },
                onCategoryClick = {
                    onEvent(DisplayShopEvent.NavigateDisplayProductCategory(it.productCategoryId))
                },
                onCategoryLongClick = {
                    onEvent(DisplayShopEvent.NavigateEditProductCategory(it.productCategoryId))
                },
                onProducerClick = {
                    it.productProducerId?.let { productProducerId ->
                        onEvent(DisplayShopEvent.NavigateDisplayProductProducer(productProducerId))
                    }
                },
                onProducerLongClick = {
                    it.productProducerId?.let { productProducerId ->
                        onEvent(DisplayShopEvent.NavigateEditProductProducer(productProducerId))
                    }
                },
                modifier = Modifier.width(600.dp),
            )

            item {
                Box(
                    modifier =
                        Modifier.windowInsetsPadding(
                            WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
                        )
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DisplayShopScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DisplayShopScreen(
                uiState =
                    DisplayShopUiState(
                        spentByTime = ItemSpentChartData.generateList(),
                        totalSpentValue = generateRandomFloatValue(),
                        averageSpentValue = generateRandomFloatValue(),
                        medianSpentValue = generateRandomFloatValue(),
                    ),
                onEvent = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyDisplayShopScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DisplayShopScreen(uiState = DisplayShopUiState(), onEvent = {})
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedDisplayShopScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DisplayShopScreen(
                uiState =
                    DisplayShopUiState(
                        spentByTime = ItemSpentChartData.generateList(),
                        totalSpentValue = generateRandomFloatValue(),
                        averageSpentValue = generateRandomFloatValue(),
                        medianSpentValue = generateRandomFloatValue(),
                    ),
                onEvent = {},
            )
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedEmptyDisplayShopScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            DisplayShopScreen(uiState = DisplayShopUiState(), onEvent = {})
        }
    }
}
