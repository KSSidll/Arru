package com.kssidll.arru.ui.component.list


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kssidll.arru.LocalCurrencyFormatLocale
import com.kssidll.arru.data.data.ItemSpentByCategory
import com.kssidll.arru.domain.data.RankSource
import com.kssidll.arru.ui.theme.ArrugarqTheme
import com.kssidll.arru.ui.theme.Typography
import com.kssidll.arru.ui.theme.optionalAlpha
import kotlin.math.roundToLong


private val ITEM_HEIGHT: Dp = 90.dp
private val HEADER_HEIGHT: Dp = 24.dp
private val DIFF_PERCENT_STRING_BOTTOM_PADDING: Dp = 16.dp

/**
 * @param listHeader String displayed as the list header
 * @param leftSideItems List of items represented on the left side
 * @param leftSideHeader String displayed as [leftSideItems] header
 * @param rightSideItems List of items represented on the right side, these are compared to [leftSideItems] and take sorting priority
 * @param rightSideHeader String displayed as [rightSideItems] header
 * @param modifier Optional modifier for the container
 * @param itemDisplayLimit Optional limit of how many items to display, defaults to null: display all items
 */
@Composable
fun <T> SpendingComparisonList(
    listHeader: String,
    leftSideItems: List<T>,
    leftSideHeader: String,
    rightSideItems: List<T>,
    rightSideHeader: String,
    modifier: Modifier = Modifier,
    itemDisplayLimit: Int? = null,
) where T: RankSource {
    val currencyLocale = LocalCurrencyFormatLocale.current

    Row(modifier = modifier) {
        val leftGroupped = leftSideItems.groupBy { it.displayName() }
        val rightSorted = rightSideItems.sortedByDescending { it.sortValue() }
        val rightGroupped = rightSideItems.groupBy { it.displayName() }
        var names = rightSorted.plus(
            leftSideItems.filterNot { it.displayName() in rightGroupped }
                .sortedBy { it.sortValue() }
        )
            .map { it.displayName() }

        if (itemDisplayLimit != null) {
            names = names.subList(
                0,
                itemDisplayLimit.coerceIn(
                    0,
                    names.size
                )
            )
        }

        Column(modifier = Modifier.width(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HEADER_HEIGHT)
            ) {
                Text(
                    text = listHeader,
                    style = Typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            names.forEachIndexed { index, it ->
                Box(
                    modifier = Modifier
                        .height(ITEM_HEIGHT)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = it,
                        style = Typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                    )
                }

                if (index != names.lastIndex) {
                    HorizontalDivider()
                }
            }
        }

        Column(modifier = Modifier.weight(1F)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HEADER_HEIGHT)
            ) {
                Text(
                    text = leftSideHeader,
                    style = Typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            names.forEachIndexed { index, it ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(ITEM_HEIGHT)
                ) {
                    val item = leftGroupped[it]
                    if (item != null) {
                        Text(
                            text = item[0].displayValue(currencyLocale),
                            style = Typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                if (index != names.lastIndex) {
                    HorizontalDivider()
                }
            }
        }

        Column(modifier = Modifier.weight(1F)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(HEADER_HEIGHT)
            ) {
                Text(
                    text = rightSideHeader,
                    style = Typography.bodyLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            names.forEachIndexed { index, it ->
                Box(
                    modifier = Modifier
                        .height(ITEM_HEIGHT)
                        .fillMaxWidth()
                ) {
                    val item = rightGroupped[it]
                    val otherItem = leftGroupped[it]
                    if (item != null) {
                        Text(
                            text = item[0].displayValue(currencyLocale),
                            style = Typography.bodyLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )

                        if (otherItem != null) {
                            val itemValue = item[0].value()
                                .roundToLong()
                            val otherItemValue = otherItem[0].value()
                                .roundToLong()

                            val diff: Long = (itemValue.toDouble()
                                .div(otherItemValue)).minus(1)
                                .times(100)
                                .toLong()
                            val diffStr: String = if (diff > 0) {
                                "+${diff} %"
                            } else {
                                "$diff %"
                            }

                            if (itemValue != otherItemValue) {
                                Text(
                                    text = diffStr,
                                    style = Typography.bodySmall,
                                    color = if (diff < 0) {
                                        MaterialTheme.colorScheme.primary.copy(optionalAlpha)
                                    } else {
                                        MaterialTheme.colorScheme.error.copy(optionalAlpha)
                                    },
                                    modifier = Modifier
                                        .padding(bottom = DIFF_PERCENT_STRING_BOTTOM_PADDING)
                                        .align(Alignment.BottomCenter)
                                )
                            }
                        }
                    }
                }

                if (index != names.lastIndex) {
                    HorizontalDivider()
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SpendingComparisonListPreview() {
    ArrugarqTheme {
        Surface {
            SpendingComparisonList(
                listHeader = "test",
                leftSideItems = ItemSpentByCategory.generateList(4),
                leftSideHeader = "left",
                rightSideItems = ItemSpentByCategory.generateList(4),
                rightSideHeader = "right",
            )
        }
    }
}
