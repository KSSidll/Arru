package com.kssidll.arrugarq.ui.component.list

import android.content.res.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.theme.*

const val defaultRankingListAnimationTime: Int = 1200
val defaultRankingListAnimationSpec: AnimationSpec<Float> = tween(defaultRankingListAnimationTime)

private val firstTextStyle: TextStyle = Typography.titleLarge
private val secondTextStyle: TextStyle = Typography.titleMedium
private val otherTextStyle: TextStyle = Typography.titleSmall

private fun getTextStyle(position: Int): TextStyle {
    return when(position) {
        0 -> firstTextStyle
        1 -> secondTextStyle
        else -> otherTextStyle
    }
}

/**
 * @param items: List of items to display, the items will be sorted by their value
 * @param modifier: Modifier applied to the container
 * @param displayCount: How many items to display, 0 means all
 * @param animationSpec: Animation Spec for the item relative to max value animation
 */
@Composable
fun RankingList(
    items: List<Rankable>,
    modifier: Modifier = Modifier,
    displayCount: Int = 6,
    animationSpec: AnimationSpec<Float> = defaultRankingListAnimationSpec,
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

    Row(modifier = modifier) {
        val itemModifier = Modifier
            .height(36.dp)
            .padding(4.dp)

        Column {
            displayItems.forEachIndexed { index, it ->
                Box(itemModifier) {
                    Text(
                        modifier = Modifier.align(Alignment.CenterStart),
                        text = it.getDisplayName(),
                        style = getTextStyle(index),
                    )
                }
            }
        }

        Column(modifier = Modifier.width(IntrinsicSize.Min)) {
            displayItems.forEachIndexed { index, it ->
                Box(itemModifier.fillMaxWidth()) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = it.getDisplayValue(),
                        style = getTextStyle(index),
                    )
                }
            }
        }

        Column {
            displayItems.forEachIndexed { index, it ->
                Box(
                    modifier = itemModifier,
                ) {
                    RankingItemProgressBar(
                        progressValue = it.getValue()
                            .toFloat() / maxItemValue,
                        modifier = Modifier
                            .align(alignment = Alignment.Center)
                            .fillMaxWidth()
                            .height(getTextStyle(index).fontSize.value.dp.minus(6.dp)),
                        animationSpec = animationSpec,
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
    animationSpec: AnimationSpec<Float> = defaultRankingListAnimationSpec,
) {
    var targetValue by remember { mutableFloatStateOf(0F) }

    LaunchedEffect(progressValue) {
        targetValue = progressValue
    }

    val animatedValue = animateFloatAsState(
        targetValue = targetValue,
        animationSpec = animationSpec,
        label = "Ranking List item value relative to the highest item value animation"
    )

    Surface(
        modifier = modifier,
        shape = ShapeDefaults.Medium,
    ) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.tertiary,
            progress = animatedValue.value,
        )
    }
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
