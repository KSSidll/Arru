package com.kssidll.arru.ui.screen.ranking

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arru.*
import com.kssidll.arru.data.data.*
import com.kssidll.arru.domain.data.*
import com.kssidll.arru.ui.component.list.*
import com.kssidll.arru.ui.component.other.*
import com.kssidll.arru.ui.theme.*

/**
 * Generic ranking screen
 * @param T Type of item, needs to implement [RankSource]
 * @param onBack Called to request a back navigation
 * @param title Text displayed on the top app bar
 * @param data List of items to display, items need to implement [RankSource]
 * @param onItemClick Function to call when an item is clicked, null disables click event if [onItemLongClick] is null as well
 * @param onItemClickLabel Semantic / accessibility label for the [onItemClick] action
 * @param onItemLongClick Function to call when an item is long clicked, null disables click event if [onItemClick] is null as well
 * @param onItemLongClickLabel Semantic / accessibility label for the [onItemLongClick] action
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> RankingScreen(
    onBack: () -> Unit,
    title: String,
    data: List<T>,
    onItemClick: ((T) -> Unit)? = null,
    onItemClickLabel: String? = null,
    onItemLongClick: ((T) -> Unit)? = null,
    onItemLongClickLabel: String? = null,
) where T: RankSource {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

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
        Box(
            modifier = Modifier
                .padding(it)
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .widthIn(max = 600.dp)
            ) {
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

@PreviewLightDark
@PreviewExpanded
@Composable
fun RankingScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RankingScreen(
                onBack = {},
                title = "test",
                data = ItemSpentByCategory.generateList(),
                onItemClick = {},
                onItemClickLabel = String(),
                onItemLongClick = {},
                onItemLongClickLabel = String(),
            )
        }
    }
}
