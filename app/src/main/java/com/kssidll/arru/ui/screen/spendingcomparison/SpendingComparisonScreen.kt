package com.kssidll.arru.ui.screen.spendingcomparison


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SpendingComparisonScreen(
    onBack: () -> Unit,
    title: String,
    leftSideItems: List<T>,
    leftSideHeader: String,
    rightSideItems: List<T>,
    rightSideHeader: String,
    modifier: Modifier = Modifier,
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
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            SpendingComparisonList(
                listHeader = String(),
                leftSideItems = leftSideItems,
                leftSideHeader = leftSideHeader,
                rightSideItems = rightSideItems,
                rightSideHeader = rightSideHeader,
                modifier = modifier
                    .padding(it)
                    .widthIn(max = 688.dp)
                    .verticalScroll(state = rememberScrollState())
            )
        }
    }
}

@PreviewLightDark
@PreviewExpanded
@Composable
private fun SpendingComparisonScreenPreview() {
    ArrugarqTheme {
        Surface {
            SpendingComparisonScreen(
                onBack = {},
                title = "test",
                leftSideItems = ItemSpentByCategory.generateList(4),
                leftSideHeader = "left",
                rightSideItems = ItemSpentByCategory.generateList(4),
                rightSideHeader = "right",
            )
        }
    }
}
