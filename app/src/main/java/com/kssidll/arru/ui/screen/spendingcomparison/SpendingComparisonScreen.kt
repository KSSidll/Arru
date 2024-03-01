package com.kssidll.arru.ui.screen.spendingcomparison


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.res.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.R
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SpendingComparisonScreen(
    onBack: () -> Unit,
    title: String,
    leftSideItems: Data<List<T>>,
    leftSideHeader: String,
    rightSideItems: Data<List<T>>,
    rightSideHeader: String,
    modifier: Modifier = Modifier,
) where T: RankSource {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    // TODO add animation on loading / info when none available
    val leftItems = if (leftSideItems is Data.Loaded) {
        leftSideItems.data
    } else emptyList()

    val rightItems = if (rightSideItems is Data.Loaded) {
        rightSideItems.data
    } else emptyList()


    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(title)
                },
                scrollBehavior = scrollBehavior,
            )
        }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            AnimatedVisibility(
                visible = leftSideItems.loadedEmpty() && rightSideItems.loadedEmpty(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.align(Alignment.Center)) {
                        Text(
                            text = stringResource(id = R.string.no_data_to_display_text),
                            textAlign = TextAlign.Center,
                            style = Typography.titleLarge,
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = leftSideItems.loadedData() || rightSideItems.loadedData(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column {
                    Spacer(modifier = Modifier.height(24.dp))

                    SpendingComparisonList(
                        listHeader = String(),
                        leftSideItems = leftItems,
                        leftSideHeader = leftSideHeader,
                        rightSideItems = rightItems,
                        rightSideHeader = rightSideHeader,
                        modifier = modifier
                            .padding(it)
                            .widthIn(max = 688.dp)
                            .verticalScroll(state = rememberScrollState())
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SpendingComparisonScreenPreview() {
    ArrugarqTheme {
        Surface {
            SpendingComparisonScreen(
                onBack = {},
                title = "test",
                leftSideItems = Data.Loaded(ItemSpentByCategory.generateList(4)),
                leftSideHeader = "left",
                rightSideItems = Data.Loaded(ItemSpentByCategory.generateList(4)),
                rightSideHeader = "right",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptySpendingComparisonScreenPreview() {
    ArrugarqTheme {
        Surface {
            SpendingComparisonScreen(
                onBack = {},
                title = "test",
                leftSideItems = Data.Loaded(emptyList()),
                leftSideHeader = "left",
                rightSideItems = Data.Loaded(emptyList()),
                rightSideHeader = "right",
            )
        }
    }
}

@PreviewExpanded
@Composable
private fun ExpandedSpendingComparisonScreenPreview() {
    ArrugarqTheme {
        Surface {
            SpendingComparisonScreen(
                onBack = {},
                title = "test",
                leftSideItems = Data.Loaded(ItemSpentByCategory.generateList(4)),
                leftSideHeader = "left",
                rightSideItems = Data.Loaded(ItemSpentByCategory.generateList(4)),
                rightSideHeader = "right",
            )
        }
    }
}

@PreviewExpanded
@Composable
private fun ExpandedEmptySpendingComparisonScreenPreview() {
    ArrugarqTheme {
        Surface {
            SpendingComparisonScreen(
                onBack = {},
                title = "test",
                leftSideItems = Data.Loaded(emptyList()),
                leftSideHeader = "left",
                rightSideItems = Data.Loaded(emptyList()),
                rightSideHeader = "right",
            )
        }
    }
}
