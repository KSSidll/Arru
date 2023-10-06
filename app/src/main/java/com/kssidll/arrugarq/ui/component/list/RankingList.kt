package com.kssidll.arrugarq.ui.component.list

import android.content.res.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.*
import androidx.compose.ui.*
import androidx.compose.ui.platform.*
import androidx.compose.ui.text.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.helper.*
import com.kssidll.arrugarq.ui.theme.*

private val firstTextStyle: TextStyle = Typography.titleLarge
private val secondTextStyle: TextStyle = Typography.titleMedium
private val otherTextStyle: TextStyle = Typography.titleSmall

private fun getTextStyle(
    position: Int,
    scaleByRank: Boolean
): TextStyle {
    if (!scaleByRank) return secondTextStyle

    return when (position) {
        0 -> firstTextStyle
        1 -> secondTextStyle
        else -> otherTextStyle
    }
}

private fun getRowHeight(
    position: Int,
    scaleByRank: Boolean
): Dp {
    if (!scaleByRank) return 60.dp

    return when (position) {
        0 -> 70.dp
        1 -> 60.dp
        else -> 50.dp
    }
}

/**
 * @param T: Type of item, needs to implement Rankable
 * @param items: List of items to display, the items will be sorted by their value
 * @param modifier: Modifier applied to the container
 * @param innerItemPadding: Padding applied to the item container
 * @param displayCount: How many items to display, 0 means all
 * @param animationSpec: Animation Spec for the item relative to max value animation
 * @param scaleByRank: Whether to scale the item values based on their position
 * @param onItemClick: Function to call when an item is clicked, null disables click event
 */
@Composable
fun <T> RankingList(
    items: List<T>,
    modifier: Modifier = Modifier,
    innerItemPadding: PaddingValues = PaddingValues(0.dp),
    displayCount: Int = 6,
    animationSpec: AnimationSpec<Float> = tween(1200),
    scaleByRank: Boolean = true,
    onItemClick: ((T) -> Unit)? = null,
) where T: Rankable {
    val displayItems: SnapshotStateList<T> = remember { mutableStateListOf() }
    var maxItemValue by remember { mutableLongStateOf(Long.MAX_VALUE) }

    LaunchedEffect(items) {
        if (items.isNotEmpty()) {
            displayItems.clear()

            var sortedItems = items.sortedByDescending { it.getSortValue() }
            if (displayCount > 0) sortedItems = sortedItems.take(displayCount)

            displayItems.addAll(sortedItems)
            maxItemValue = displayItems.first()
                .getSortValue()
        }
    }

    val layoutDirection = LocalLayoutDirection.current
    val startPadding = innerItemPadding.calculateStartPadding(layoutDirection)
    val topPadding = innerItemPadding.calculateTopPadding()
    val bottomPadding = innerItemPadding.calculateBottomPadding()
    val endPadding = innerItemPadding.calculateEndPadding(layoutDirection)

    Box(modifier = modifier) {
        Row {
            Column(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .widthIn(max = 160.dp)
            ) {
                displayItems.forEachIndexed { index, it ->
                    Box(
                        modifier = Modifier
                            .height(
                                getRowHeight(
                                    index,
                                    scaleByRank
                                )
                            )
                            .padding(
                                start = startPadding + 4.dp,
                                top = topPadding,
                                bottom = bottomPadding,
                                end = 4.dp,
                            )
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.CenterStart),
                            text = it.getDisplayName(),
                            style = getTextStyle(
                                index,
                                scaleByRank
                            ),
                            maxLines = 2,
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .horizontalScroll(state = rememberScrollState())
                    .width(IntrinsicSize.Min)
                    .widthIn(
                        min = 40.dp,
                        max = 128.dp
                    )
            ) {
                displayItems.forEachIndexed { index, it ->
                    Box(
                        modifier = Modifier
                            .height(
                                getRowHeight(
                                    index,
                                    scaleByRank
                                )
                            )
                            .padding(
                                start = 8.dp,
                                top = topPadding,
                                bottom = bottomPadding,
                                end = 8.dp,
                            )
                            .fillMaxWidth()
                    ) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = it.getDisplayValue(),
                            style = getTextStyle(
                                index,
                                scaleByRank
                            ),
                        )
                    }
                }
            }

            Column {
                displayItems.forEachIndexed { index, it ->
                    Box(
                        modifier = Modifier
                            .height(
                                getRowHeight(
                                    index,
                                    scaleByRank
                                )
                            )
                            .padding(
                                start = 4.dp,
                                top = topPadding,
                                bottom = bottomPadding,
                                end = endPadding + 4.dp,
                            )
                    ) {
                        RankingItemProgressBar(
                            progressValue = it.getSortValue() / maxItemValue.toFloat(),
                            modifier = Modifier
                                .align(alignment = Alignment.Center)
                                .fillMaxWidth()
                                .height(
                                    getTextStyle(
                                        index,
                                        scaleByRank
                                    ).fontSize.value.dp.minus(6.dp)
                                ),
                            animationSpec = animationSpec,
                        )
                    }
                }
            }
        }

        if (onItemClick != null) {
            Column(
                modifier = Modifier.matchParentSize()
            ) {
                displayItems.forEachIndexed { index, it ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(
                                getRowHeight(
                                    index,
                                    scaleByRank
                                )
                            )
                            .clickable {
                                onItemClick(it)
                            }
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
    animationSpec: AnimationSpec<Float> = tween(1200),
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
                items = getFakeSpentByShopData(),
            )
        }
    }
}
