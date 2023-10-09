package com.kssidll.arrugarq.ui.screen.home.transactions

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.screen.home.transactions.component.*
import com.kssidll.arrugarq.ui.theme.*
import kotlinx.coroutines.flow.*
import java.sql.Date
import java.text.*
import java.util.*

@Composable
fun TransactionsScreen(
    items: Flow<List<FullItem>>,
) {
    TransactionsScreenContent(
        items = items.collectAsState(emptyList()).value,
    )
}

@Composable
private fun TransactionsScreenContent(
    items: List<FullItem>,
) {
    val grouppedItems: SnapshotStateList<Pair<Long, List<FullItem>>> =
        remember { mutableStateListOf() }

    LaunchedEffect(items) {
        grouppedItems.clear()
        grouppedItems.addAll(
            items.groupBy { it.embeddedItem.item.date / 86400000 }
                .toList()
                .sortedByDescending { it.first })
    }

    LazyColumn {
        grouppedItems.forEachIndexed { index, group ->
            item {
                Column(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    if (index != 0) {
                        HorizontalDivider()
                        Spacer(Modifier.height(6.dp))
                    }

                    Text(
                        text = SimpleDateFormat(
                            "yyyy-MM-dd",
                            Locale.getDefault()
                        ).format(group.first * 86400000),
                        style = Typography.titleLarge,
                    )
                }
            }

            items(group.second) { embeddedItem ->
                TransactionItem(embeddedItem)
            }
        }
    }
}

@Preview(
    group = "Transactions Screen",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Transactions Screen",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun TransactionsScreenPreview() {
    ArrugarqTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            TransactionsScreenContent(
                items = generateRandomFullItemList(
                    itemDateTimeFrom = Date.valueOf("2022-06-01").time,
                    itemDateTimeUntil = Date.valueOf("2022-06-04").time,
                ),
            )
        }
    }
}
