package com.kssidll.arru.ui.screen.ranking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.kssidll.arru.ui.component.list.RankingList
import com.kssidll.arru.ui.component.other.SecondaryAppBar
import com.kssidll.arru.ui.theme.ArruTheme
import com.kssidll.arru.ui.theme.Typography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

/**
 * Generic ranking screen
 *
 * @param T Type of item, needs to implement [RankSource]
 * @param onBack Called to request a back navigation
 * @param title Text displayed on the top app bar
 * @param data List of items to display, items need to implement [RankSource]
 * @param onItemClick Function to call when an item is clicked, null disables click event if
 *   [onItemLongClick] is null as well
 * @param onItemClickLabel Semantic / accessibility label for the [onItemClick] action
 * @param onItemLongClick Function to call when an item is long clicked, null disables click event
 *   if [onItemClick] is null as well
 * @param onItemLongClickLabel Semantic / accessibility label for the [onItemLongClick] action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RankingScreen(
    onBack: () -> Unit,
    title: String,
    data: ImmutableList<T>,
    modifier: Modifier = Modifier,
    onItemClick: ((T) -> Unit)? = null,
    onItemClickLabel: String? = null,
    onItemLongClick: ((T) -> Unit)? = null,
    onItemLongClickLabel: String? = null,
) where T : RankSource {
    Scaffold(
        topBar = { SecondaryAppBar(onBack = onBack, title = { Text(title) }) },
        contentWindowInsets =
            ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Horizontal),
        modifier =
            modifier.windowInsetsPadding(
                WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal)
            ),
    ) {
        Box(
            modifier =
                Modifier.padding(it)
                    .consumeWindowInsets(it)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
        ) {
            AnimatedVisibility(
                visible = data.isEmpty(),
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

            AnimatedVisibility(visible = data.isNotEmpty(), enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.align(Alignment.Center).widthIn(max = 600.dp)) {
                        RankingList(
                            innerItemPadding = PaddingValues(horizontal = 16.dp),
                            items = data,
                            displayCount = 0,
                            scaleByRank = false,
                            onItemClick = onItemClick,
                            onItemClickLabel = onItemClickLabel,
                            onItemLongClick = onItemLongClick,
                            onItemLongClickLabel = onItemLongClickLabel,
                        )
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun RankingScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RankingScreen(
                onBack = {},
                title = "test",
                data = TotalSpentByCategory.generateList().toImmutableList(),
                onItemClick = {},
                onItemClickLabel = String(),
                onItemLongClick = {},
                onItemLongClickLabel = String(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyRankingScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RankingScreen(
                onBack = {},
                title = "test",
                data = emptyImmutableList(),
                onItemClick = {},
                onItemClickLabel = String(),
                onItemLongClick = {},
                onItemLongClickLabel = String(),
            )
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedRankingScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RankingScreen(
                onBack = {},
                title = "test",
                data = TotalSpentByCategory.generateList().toImmutableList(),
                onItemClick = {},
                onItemClickLabel = String(),
                onItemLongClick = {},
                onItemLongClickLabel = String(),
            )
        }
    }
}

@ExpandedPreviews
@Composable
private fun ExpandedEmptyRankingScreenPreview() {
    ArruTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RankingScreen(
                onBack = {},
                title = "test",
                data = emptyImmutableList(),
                onItemClick = {},
                onItemClickLabel = String(),
                onItemLongClick = {},
                onItemLongClickLabel = String(),
            )
        }
    }
}
