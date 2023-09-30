package com.kssidll.arrugarq.ui.component.list

import android.content.res.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.theme.*

/**
 * @param items: List of items to display, the items will be sorted by their value
 * @param displayCount: How many items to display, 0 means all
 */
@Composable
fun RankingList(
    items: List<Rankable>,
    displayCount: Int = 6,
) {
    val displayItems: SnapshotStateList<Rankable> = remember { mutableStateListOf() }
    var maxItemValue by remember { mutableLongStateOf(Long.MAX_VALUE) }

    LaunchedEffect(items) {
        if (items.isNotEmpty()) {
            displayItems.clear()

            var sortedItems = items.sortedByDescending { it.getValue() }
            if (displayCount > 0) sortedItems = sortedItems.take(displayCount)

            displayItems.addAll(sortedItems)
            maxItemValue = displayItems.first()
                .getValue()
        }
    }

    Row(
        modifier = Modifier.padding(8.dp)
    ) {
        val itemModifier = Modifier
            .height(40.dp)
            .padding(4.dp)

        Column {
            displayItems.forEach {
                Text(
                    text = it.getDisplayName(),
                    modifier = itemModifier,
                )
                Spacer(Modifier.width(20.dp))
            }
        }

        Column {
            displayItems.forEach {
                Text(
                    text = it.getDisplayValue(),
                    modifier = itemModifier,
                )
            }
        }

        Column {
            displayItems.forEach {
                Box(
                    modifier = itemModifier,
                ) {
                    RankingItemProgressBar(
                        progressValue = it.getValue()
                            .toFloat() / maxItemValue,
                        modifier = Modifier
                            .align(alignment = Alignment.Center)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingItemProgressBar(
    progressValue: Float,
    modifier: Modifier = Modifier,
) {
    var animatedValue by remember { mutableFloatStateOf(0F) }

    LaunchedEffect(progressValue) {
        animatedValue = progressValue
    }

    LinearProgressIndicator(
        modifier = modifier,
        color = MaterialTheme.colorScheme.tertiary,
        progress = animatedValue,
    )
}

@Preview(
    group = "Ranking List",
    name = "Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Preview(
    group = "Ranking List",
    name = "Light",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun RankingListPreview() {
    ArrugarqTheme {
        Surface {
            RankingList(
                items = listOf(
                    ItemSpentByShop(
                        shop = Shop("test1"),
                        total = 168200
                    ),
                    ItemSpentByShop(
                        shop = Shop("test2"),
                        total = 10000
                    ),
                    ItemSpentByShop(
                        shop = Shop("test3"),
                        total = 100000
                    ),
                    ItemSpentByShop(
                        shop = Shop("test4"),
                        total = 61000
                    ),
                    ItemSpentByShop(
                        shop = Shop("test5"),
                        total = 27600
                    ),
                )
            )
        }
    }
}
