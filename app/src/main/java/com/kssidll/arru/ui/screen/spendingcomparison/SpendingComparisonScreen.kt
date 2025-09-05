package com.kssidll.arru.ui.screen.spendingcomparison

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.kssidll.arru.ExpandedPreviews
import com.kssidll.arru.R
import com.kssidll.arru.data.data.TotalSpentByCategory
import com.kssidll.arru.domain.data.emptyImmutableList
import com.kssidll.arru.domain.data.interfaces.RankSource
import com.kssidll.arru.ui.component.list.SpendingComparisonList
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.Typography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SpendingComparisonScreen(
    onBack: () -> Unit,
    title: String,
    leftSideItems: ImmutableList<T>,
    leftSideHeader: String,
    rightSideItems: ImmutableList<T>,
    rightSideHeader: String,
    modifier: Modifier = Modifier,
) where T : RankSource {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        topBar = {
            SecondaryAppBar(
                onBack = onBack,
                title = { Text(title) },
                scrollBehavior = scrollBehavior,
            )
        },
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(it).consumeWindowInsets(it).fillMaxSize(),
        ) {
            if (leftSideItems.isEmpty() && rightSideItems.isEmpty()) {
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

            if (leftSideItems.isNotEmpty() || rightSideItems.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                    Column(modifier = Modifier.width(600.dp).align(Alignment.TopCenter)) {
                        Spacer(modifier = Modifier.height(24.dp))

                        SpendingComparisonList(
                            listHeader = String(),
                            leftSideItems = leftSideItems,
                            leftSideHeader = leftSideHeader,
                            rightSideItems = rightSideItems,
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
    ArruTheme {
        Surface {
            SpendingComparisonScreen(
                onBack = {},
                title = "test",
                leftSideItems = TotalSpentByCategory.generateList(4).toImmutableList(),
                leftSideHeader = "left",
                rightSideItems = TotalSpentByCategory.generateList(4).toImmutableList(),
                rightSideHeader = "right",
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptySpendingComparisonScreenPreview() {
    ArruTheme {
        Surface {
            SpendingComparisonScreen(
                onBack = {},
                title = "test",
                leftSideItems = emptyImmutableList(),
                leftSideHeader = "left",
                rightSideItems = emptyImmutableList(),
                rightSideHeader = "right",
            )
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedSpendingComparisonScreenPreview() {
    ArruTheme {
        Surface {
            SpendingComparisonScreen(
                onBack = {},
                title = "test",
                leftSideItems = TotalSpentByCategory.generateList(4).toImmutableList(),
                leftSideHeader = "left",
                rightSideItems = TotalSpentByCategory.generateList(4).toImmutableList(),
                rightSideHeader = "right",
            )
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedEmptySpendingComparisonScreenPreview() {
    ArruTheme {
        Surface {
            SpendingComparisonScreen(
                onBack = {},
                title = "test",
                leftSideItems = emptyImmutableList(),
                leftSideHeader = "left",
                rightSideItems = emptyImmutableList(),
                rightSideHeader = "right",
            )
        }
    }
}
