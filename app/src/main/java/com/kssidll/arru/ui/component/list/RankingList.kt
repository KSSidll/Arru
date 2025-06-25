package com.kssidll.arru.ui.component.list

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.LocalCurrencyFormatLocale
import com.kssidll.arru.data.data.TransactionTotalSpentByShop
import com.kssidll.arru.domain.data.RankSource
import com.kssidll.arru.ui.component.other.ProgressBar
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList

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
    items: ImmutableList<T>,
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

    val currencyLocale = LocalCurrencyFormatLocale.current

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
                            text = it.displayValue(currencyLocale),
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
                            animationSpec = animationSpec,
                            modifier = Modifier
                                .align(alignment = Alignment.Center)
                                .fillMaxWidth()
                                .height(
                                    getTextStyle(
                                        index,
                                        scaleByRank
                                    ).fontSize.value.dp.minus(6.dp)
                                )
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

@PreviewLightDark
@Composable
private fun RankingListPreview() {
    ArrugarqTheme {
        Surface {
            RankingList(
                items = TransactionTotalSpentByShop.generateList().toImmutableList(),
            )
        }
    }
}
