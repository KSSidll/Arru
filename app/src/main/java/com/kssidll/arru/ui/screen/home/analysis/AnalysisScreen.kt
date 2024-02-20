package com.kssidll.arru.ui.screen.home.analysis


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.screen.home.analysis.components.*
import com.kssidll.arru.ui.theme.*

private val TileOuterPadding: Dp = 8.dp
private val TileInnerPadding: Dp = 12.dp

/**
 * @param year Year for which the main data is fetched
 * @param month Month for which the main data is fetched, in range of 1 - 12
 * @param onMonthIncrement Callback called to request [month] increment, should handle overflow and increase year
 * @param onMonthDecrement Callback called to request [month] decrement, should handle underflow and decrease year
 * @param setCategorySpending List of items representing the category wise spending for currently set [year] and [month]
 * @param compareCategorySpending List of items representing the category wise spending for previous [month] of currently set [year] and [month]
 * @param onCategorySpendingComparisonCardClick Callback called when the category spending comparison card is clicked
 * @param onShopSpendingComparisonCardClick Callback called when the shop spending comparison card is clicked
 */
@Composable
internal fun AnalysisScreen(
    isExpandedScreen: Boolean,
    year: Int,
    month: Int,
    onMonthIncrement: () -> Unit,
    onMonthDecrement: () -> Unit,
    setCategorySpending: List<ItemSpentByCategory>,
    compareCategorySpending: List<ItemSpentByCategory>,
    setShopSpending: List<TransactionTotalSpentByShop>,
    compareShopSpending: List<TransactionTotalSpentByShop>,
    onCategorySpendingComparisonCardClick: () -> Unit,
    onShopSpendingComparisonCardClick: () -> Unit,
) {
    if (isExpandedScreen) {
        ExpandedAnalysisScreenContent(
            year = year,
            month = month,
            onMonthIncrement = onMonthIncrement,
            onMonthDecrement = onMonthDecrement,
            setCategorySpending = setCategorySpending,
            compareCategorySpending = compareCategorySpending,
            setShopSpending = setShopSpending,
            compareShopSpending = compareShopSpending,
            onCategorySpendingComparisonCardClick = onCategorySpendingComparisonCardClick,
            onShopSpendingComparisonCardClick = onShopSpendingComparisonCardClick,
        )
    } else {
        AnalysisScreenContent(
            year = year,
            month = month,
            onMonthIncrement = onMonthIncrement,
            onMonthDecrement = onMonthDecrement,
            setCategorySpending = setCategorySpending,
            compareCategorySpending = compareCategorySpending,
            setShopSpending = setShopSpending,
            compareShopSpending = compareShopSpending,
            onCategorySpendingComparisonCardClick = onCategorySpendingComparisonCardClick,
            onShopSpendingComparisonCardClick = onShopSpendingComparisonCardClick,
        )
    }
}

@Composable
private fun AnalysisScreenContent(
    year: Int,
    month: Int,
    onMonthIncrement: () -> Unit,
    onMonthDecrement: () -> Unit,
    setCategorySpending: List<ItemSpentByCategory>,
    compareCategorySpending: List<ItemSpentByCategory>,
    setShopSpending: List<TransactionTotalSpentByShop>,
    compareShopSpending: List<TransactionTotalSpentByShop>,
    onCategorySpendingComparisonCardClick: () -> Unit,
    onShopSpendingComparisonCardClick: () -> Unit,

    ) {
    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                AnimatedVisibility(visible = compareCategorySpending.isEmpty() && setCategorySpending.isEmpty() && compareShopSpending.isEmpty() && setShopSpending.isEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_data_to_display_text),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                DateHeader(
                    year = year,
                    month = month,
                    onMonthIncrement = onMonthIncrement,
                    onMonthDecrement = onMonthDecrement,
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = compareCategorySpending.isNotEmpty() || setCategorySpending.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    modifier = Modifier
                        .padding(TileOuterPadding)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .heightIn(min = 144.dp)
                            .clickable {
                                onCategorySpendingComparisonCardClick()
                            }
                    ) {
                        SpendingComparisonList(
                            listHeader = stringResource(id = R.string.categories),
                            leftSideItems = compareCategorySpending,
                            leftSideHeader = stringResource(id = R.string.previous),
                            rightSideItems = setCategorySpending,
                            rightSideHeader = stringResource(id = R.string.current),
                            itemDisplayLimit = 6,
                            modifier = Modifier.padding(TileInnerPadding)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(visible = compareShopSpending.isNotEmpty() || setShopSpending.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    modifier = Modifier
                        .padding(TileOuterPadding)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .heightIn(min = 144.dp)
                            .clickable {
                                onShopSpendingComparisonCardClick()
                            }
                    ) {
                        SpendingComparisonList(
                            listHeader = stringResource(id = R.string.shops),
                            leftSideItems = compareShopSpending,
                            leftSideHeader = stringResource(id = R.string.previous),
                            rightSideItems = setShopSpending,
                            rightSideHeader = stringResource(id = R.string.current),
                            itemDisplayLimit = 6,
                            modifier = Modifier.padding(TileInnerPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandedAnalysisScreenContent(
    year: Int,
    month: Int,
    onMonthIncrement: () -> Unit,
    onMonthDecrement: () -> Unit,
    setCategorySpending: List<ItemSpentByCategory>,
    compareCategorySpending: List<ItemSpentByCategory>,
    setShopSpending: List<TransactionTotalSpentByShop>,
    compareShopSpending: List<TransactionTotalSpentByShop>,
    onCategorySpendingComparisonCardClick: () -> Unit,
    onShopSpendingComparisonCardClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(top = 4.dp)) {
                DateHeader(
                    year = year,
                    month = month,
                    onMonthIncrement = onMonthIncrement,
                    onMonthDecrement = onMonthDecrement,
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            AnimatedVisibility(visible = compareCategorySpending.isEmpty() && setCategorySpending.isEmpty() && compareShopSpending.isEmpty() && setShopSpending.isEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.no_data_to_display_text),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Row {
                AnimatedVisibility(
                    visible = compareCategorySpending.isNotEmpty() || setCategorySpending.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier.padding(TileOuterPadding)
                    ) {
                        Box(
                            modifier = Modifier
                                .heightIn(min = 144.dp)
                                .clickable {
                                    onCategorySpendingComparisonCardClick()
                                }
                        ) {
                            SpendingComparisonList(
                                listHeader = stringResource(id = R.string.categories),
                                leftSideItems = compareCategorySpending,
                                leftSideHeader = stringResource(id = R.string.previous),
                                rightSideItems = setCategorySpending,
                                rightSideHeader = stringResource(id = R.string.current),
                                itemDisplayLimit = 6,
                                modifier = Modifier.padding(TileInnerPadding)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = compareShopSpending.isNotEmpty() || setShopSpending.isNotEmpty(),
                    modifier = Modifier.weight(1f)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                        modifier = Modifier.padding(TileOuterPadding)
                    ) {
                        Box(
                            modifier = Modifier
                                .heightIn(min = 144.dp)
                                .clickable {
                                    onShopSpendingComparisonCardClick()
                                }
                        ) {
                            SpendingComparisonList(
                                listHeader = stringResource(id = R.string.shops),
                                leftSideItems = compareShopSpending,
                                leftSideHeader = stringResource(id = R.string.previous),
                                rightSideItems = setShopSpending,
                                rightSideHeader = stringResource(id = R.string.current),
                                itemDisplayLimit = 6,
                                modifier = Modifier.padding(TileInnerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun AnalysisScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnalysisScreen(
                isExpandedScreen = false,
                year = 2021,
                month = 12,
                onMonthIncrement = {},
                onMonthDecrement = {},
                setCategorySpending = ItemSpentByCategory.generateList(),
                compareCategorySpending = ItemSpentByCategory.generateList(),
                setShopSpending = TransactionTotalSpentByShop.generateList(),
                compareShopSpending = TransactionTotalSpentByShop.generateList(),
                onCategorySpendingComparisonCardClick = {},
                onShopSpendingComparisonCardClick = {},
            )
        }
    }
}

@PreviewExpanded
@Composable
private fun ExpandedAnalysisScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnalysisScreen(
                isExpandedScreen = true,
                year = 2021,
                month = 12,
                onMonthIncrement = {},
                onMonthDecrement = {},
                setCategorySpending = ItemSpentByCategory.generateList(),
                compareCategorySpending = ItemSpentByCategory.generateList(),
                setShopSpending = TransactionTotalSpentByShop.generateList(),
                compareShopSpending = TransactionTotalSpentByShop.generateList(),
                onCategorySpendingComparisonCardClick = {},
                onShopSpendingComparisonCardClick = {},
            )
        }
    }
}
