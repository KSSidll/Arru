package com.kssidll.arru.ui.screen.spendingcomparison


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.PreviewExpanded
import com.kssidll.arru.R
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.domain.data.Data
import com.kssidll.arru.domain.data.RankSource
import com.kssidll.arru.domain.data.loadedData
import com.kssidll.arru.domain.data.loadedEmpty
import com.kssidll.arru.ui.component.list.SpendingComparisonList
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography

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

    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = {
                    Text(title)
                },
                scrollBehavior = scrollBehavior,
            )
        },
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal),
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it)
                .fillMaxSize()
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
                val leftItems = if (leftSideItems is Data.Loaded) {
                    leftSideItems.data
                } else emptyList()

                val rightItems = if (rightSideItems is Data.Loaded) {
                    rightSideItems.data
                } else emptyList()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier
                            .width(600.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        Spacer(modifier = Modifier.height(24.dp))

                        SpendingComparisonList(
                            listHeader = String(),
                            leftSideItems = leftItems,
                            leftSideHeader = leftSideHeader,
                            rightSideItems = rightItems,
                            rightSideHeader = rightSideHeader,
                        )
                    }
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
