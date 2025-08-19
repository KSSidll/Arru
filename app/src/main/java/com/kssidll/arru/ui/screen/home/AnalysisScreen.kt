package com.kssidll.arru.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.R
import com.kssidll.arru.helper.BetterNavigationSuiteScaffoldDefaults
import com.kssidll.arru.ui.component.list.SpendingComparisonList
import com.kssidll.arru.ui.screen.home.component.AnalysisDateHeader
import com.kssidll.arru.ui.theme.ArruTheme

private val TileOuterPadding: Dp = 8.dp
private val TileInnerPadding: Dp = 12.dp

@Composable
fun AnalysisScreen(
    uiState: HomeUiState,
    onEvent: (event: HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navSuiteType =
        BetterNavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(currentWindowAdaptiveInfo())

    if (navSuiteType != NavigationSuiteType.NavigationBar) {
        ExpandedAnalysisScreenContent(uiState = uiState, onEvent = onEvent, modifier = modifier)
    } else {
        AnalysisScreenContent(uiState = uiState, onEvent = onEvent, modifier = modifier)
    }
}

@Composable
fun AnalysisScreenContent(
    uiState: HomeUiState,
    onEvent: (event: HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        bottomBar = {
            Column(modifier = Modifier.padding(vertical = 12.dp)) {
                AnimatedVisibility(
                    visible = uiState.analysisScreenNothingToDisplayVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_data_to_display_text),
                            textAlign = TextAlign.Center,
                        )
                    }
                }

                AnalysisDateHeader(
                    year = uiState.analysisCurrentDateYear,
                    month = uiState.analysisCurrentDateMonth,
                    onMonthIncrement = { onEvent(HomeEvent.IncrementCurrentAnalysisDate) },
                    onMonthDecrement = { onEvent(HomeEvent.DecrementCurrentAnalysisDate) },
                )
            }
        },
        contentWindowInsets =
            ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal),
        modifier = modifier,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = uiState.analysisScreenCategoryCardVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                    modifier = Modifier.padding(TileOuterPadding).fillMaxWidth(),
                ) {
                    Box(
                        modifier =
                            Modifier.heightIn(min = 144.dp).clickable {
                                onEvent(
                                    HomeEvent.NavigateCategorySpendingComparison(
                                        year = uiState.analysisCurrentDateYear,
                                        month = uiState.analysisCurrentDateMonth,
                                    )
                                )
                            }
                    ) {
                        SpendingComparisonList(
                            listHeader = stringResource(id = R.string.categories),
                            leftSideItems = uiState.analysisPreviousDateCategoryData,
                            leftSideHeader = stringResource(id = R.string.previous),
                            rightSideItems = uiState.analysisCurrentDateCategoryData,
                            rightSideHeader = stringResource(id = R.string.current),
                            itemDisplayLimit = 6,
                            modifier = Modifier.padding(TileInnerPadding),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedVisibility(
                visible = uiState.analysisScreenShopCardVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Card(
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        ),
                    modifier = Modifier.padding(TileOuterPadding).fillMaxWidth(),
                ) {
                    Box(
                        modifier =
                            Modifier.heightIn(min = 144.dp).clickable {
                                onEvent(
                                    HomeEvent.NavigateShopSpendingComparison(
                                        year = uiState.analysisCurrentDateYear,
                                        month = uiState.analysisCurrentDateMonth,
                                    )
                                )
                            }
                    ) {
                        SpendingComparisonList(
                            listHeader = stringResource(id = R.string.shops),
                            leftSideItems = uiState.analysisPreviousDateShopData,
                            leftSideHeader = stringResource(id = R.string.previous),
                            rightSideItems = uiState.analysisCurrentDateShopData,
                            rightSideHeader = stringResource(id = R.string.current),
                            itemDisplayLimit = 6,
                            modifier = Modifier.padding(TileInnerPadding),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandedAnalysisScreenContent(
    uiState: HomeUiState,
    onEvent: (event: HomeEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            Box(modifier = Modifier.padding(top = 4.dp)) {
                AnalysisDateHeader(
                    year = uiState.analysisCurrentDateYear,
                    month = uiState.analysisCurrentDateMonth,
                    onMonthIncrement = { onEvent(HomeEvent.IncrementCurrentAnalysisDate) },
                    onMonthDecrement = { onEvent(HomeEvent.DecrementCurrentAnalysisDate) },
                )
            }
        },
        modifier = modifier.windowInsetsPadding(WindowInsets.statusBars),
    ) { paddingValues ->
        Column(
            modifier =
                Modifier.padding(paddingValues)
                    .consumeWindowInsets(paddingValues)
                    .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(
                visible = uiState.analysisScreenNothingToDisplayVisible,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(id = R.string.no_data_to_display_text),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Row {
                AnimatedVisibility(
                    visible = uiState.analysisScreenCategoryCardVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.weight(1f),
                ) {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                        modifier = Modifier.padding(TileOuterPadding).fillMaxWidth(),
                    ) {
                        Box(
                            modifier =
                                Modifier.heightIn(min = 144.dp).clickable {
                                    onEvent(
                                        HomeEvent.NavigateCategorySpendingComparison(
                                            year = uiState.analysisCurrentDateYear,
                                            month = uiState.analysisCurrentDateMonth,
                                        )
                                    )
                                }
                        ) {
                            SpendingComparisonList(
                                listHeader = stringResource(id = R.string.categories),
                                leftSideItems = uiState.analysisPreviousDateCategoryData,
                                leftSideHeader = stringResource(id = R.string.previous),
                                rightSideItems = uiState.analysisCurrentDateCategoryData,
                                rightSideHeader = stringResource(id = R.string.current),
                                itemDisplayLimit = 6,
                                modifier = Modifier.padding(TileInnerPadding),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                AnimatedVisibility(
                    visible = uiState.analysisScreenShopCardVisible,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.weight(1f),
                ) {
                    Card(
                        colors =
                            CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer
                            ),
                        modifier = Modifier.padding(TileOuterPadding).fillMaxWidth(),
                    ) {
                        Box(
                            modifier =
                                Modifier.heightIn(min = 144.dp).clickable {
                                    onEvent(
                                        HomeEvent.NavigateShopSpendingComparison(
                                            year = uiState.analysisCurrentDateYear,
                                            month = uiState.analysisCurrentDateMonth,
                                        )
                                    )
                                }
                        ) {
                            SpendingComparisonList(
                                listHeader = stringResource(id = R.string.shops),
                                leftSideItems = uiState.analysisPreviousDateShopData,
                                leftSideHeader = stringResource(id = R.string.previous),
                                rightSideItems = uiState.analysisCurrentDateShopData,
                                rightSideHeader = stringResource(id = R.string.current),
                                itemDisplayLimit = 6,
                                modifier = Modifier.padding(TileInnerPadding),
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
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnalysisScreen(uiState = HomeUiState(), onEvent = {})
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedAnalysisScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            AnalysisScreen(uiState = HomeUiState(), onEvent = {})
        }
    }
}
