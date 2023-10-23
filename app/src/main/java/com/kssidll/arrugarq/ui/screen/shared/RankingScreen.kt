package com.kssidll.arrugarq.ui.screen.shared

import android.content.res.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.input.nestedscroll.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.component.list.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*

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
) where T: Rankable {
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
                .verticalScroll(state = rememberScrollState())
        ) {
            RankingScreenContent(
                data = data,
                onItemClick = onItemClick,
                onItemClickLabel = onItemClickLabel,
                onItemLongClick = onItemLongClick,
                onItemLongClickLabel = onItemLongClickLabel,
            )
        }
    }
}

@Composable
private fun <T> RankingScreenContent(
    data: List<T>,
    onItemClick: ((T) -> Unit)? = null,
    onItemClickLabel: String? = null,
    onItemLongClick: ((T) -> Unit)? = null,
    onItemLongClickLabel: String? = null,
) where T: Rankable {
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

@Preview(
    group = "Ranking Screen",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Ranking Screen",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun RankingScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            RankingScreen(
                onBack = {},
                title = "test",
                data = generateRandomItemSpentByCategoryList(),
                onItemClick = {},
                onItemClickLabel = String(),
                onItemLongClick = {},
                onItemLongClickLabel = String(),
            )
        }
    }
}
