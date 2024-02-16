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
import androidx.compose.ui.semantics.*
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import androidx.compose.ui.tooling.preview.*
import androidx.compose.ui.unit.*
import com.kssidll.arrugarq.data.data.*
import com.kssidll.arrugarq.domain.data.*
import com.kssidll.arrugarq.ui.component.other.*
import com.kssidll.arrugarq.ui.theme.*

private val firstTextStyle: TextStyle = Typography.titleLarge
private val secondTextStyle: TextStyle = Typography.titleMedium
private val otherTextStyle: TextStyle = Typography.titleSmall

/**
 * Determines which [TextStyle] to use for the item depending on [position] and [scaleByRank]
 * @return [TextStyle] to use
 * @param position Position of the item in the list
 * @param scaleByRank Whether to use rank scaling, if True, items with 1st and 2nd position will have different styles than 3rd position onward
 */
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

/**
 * Determines what row height to use for the item depending on [position] and [scaleByRank]
 * @return Row height, in [Dp], to use
 * @param position Position of the item in the list
 * @param scaleByRank Whether to use rank scaling, if True, items with 1st and 2nd position will have different row heights than 3rd position onward
 */
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
 * Generic ranking list
 * @param T Type of item, needs to implement [RankSource]
 * @param items List of items to display, the items will be sorted by their value, items need to implement [RankSource]
 * @param modifier Modifier applied to the container
 * @param innerItemPadding Padding applied to the item container
 * @param displayCount How many items to display, 0 means all
 * @param animationSpec Animation Spec for the item relative to max value animation
 * @param scaleByRank Whether to scale the item values based on their position
 * @param onItemClick Function to call when an item is clicked, null disables click event if [onItemLongClick] is null as well
 * @param onItemClickLabel Semantic / accessibility label for the [onItemClick] action
 * @param onItemLongClick Function to call when an item is long clicked, null disables click event if [onItemClick] is null as well
 * @param onItemLongClickLabel Semantic / accessibility label for the [onItemLongClick] action
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun <T> RankingList(
    items: List<T>,
    modifier: Modifier = Modifier,
    innerItemPadding: PaddingValues = PaddingValues(0.dp),
    displayCount: Int = 6,
    animationSpec: AnimationSpec<Float> = tween(1200),
    scaleByRank: Boolean = true,
    onItemClick: ((T) -> Unit)? = null,
    onItemClickLabel: String? = null,
    onItemLongClick: ((T) -> Unit)? = null,
    onItemLongClickLabel: String? = null,
) where T: RankSource {
    val displayItems: SnapshotStateList<T> = remember { mutableStateListOf() }
    var maxItemValue by remember { mutableLongStateOf(Long.MAX_VALUE) }

    LaunchedEffect(items) {
        if (items.isNotEmpty()) {
            displayItems.clear()

            var sortedItems = items.sortedByDescending { it.sortValue() }
            if (displayCount > 0) sortedItems = sortedItems.take(displayCount)

            displayItems.addAll(sortedItems)
            maxItemValue = displayItems.first()
                .sortValue()
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
                    .width(IntrinsicSize.Max)
                    .widthIn(max = 180.dp)
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
                            text = it.displayName(),
                            style = getTextStyle(
                                index,
                                scaleByRank
                            ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
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
                        max = 144.dp
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
                            text = it.displayValue(),
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
                        ProgressBar(
                            progressValue = it.sortValue() / maxItemValue.toFloat(),
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

        if (onItemClick != null && onItemLongClick != null) {
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
                            .combinedClickable(
                                role = Role.Button,
                                onClick = {
                                    onItemClick(it)
                                },
                                onClickLabel = onItemClickLabel,
                                onLongClick = {
                                    onItemLongClick(it)
                                },
                                onLongClickLabel = onItemLongClickLabel,
                            )
                    )
                }
            }
        }

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
                items = ItemSpentByShop.generateList(),
            )
        }
    }
}
